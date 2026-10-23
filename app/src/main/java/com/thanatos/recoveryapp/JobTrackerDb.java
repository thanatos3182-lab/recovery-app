package com.thanatos.recoveryapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class JobTrackerDb extends SQLiteOpenHelper {
    public static class App {
        public long id;
        public String company;
        public String role;
        public String status;
        public String notes;
        public long applied;
        public long followup;
    }

    public static final String[] STATUSES =
            {"Applied", "Interviewing", "Offer", "Not selected", "Withdrew"};

    public JobTrackerDb(Context c) { super(c, "jobtracker.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE apps (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "company TEXT NOT NULL, role TEXT, applied INTEGER, status TEXT, "
                + "notes TEXT, followup INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {}

    public static boolean active(String status) {
        return "Applied".equals(status) || "Interviewing".equals(status);
    }

    public App add(String company, String role, long applied, String notes, long followup) {
        ContentValues v = new ContentValues();
        v.put("company", company);
        v.put("role", role);
        v.put("applied", applied);
        v.put("status", "Applied");
        v.put("notes", notes);
        v.put("followup", followup);
        App a = new App();
        a.id = getWritableDatabase().insert("apps", null, v);
        a.company = company;
        a.role = role;
        a.applied = applied;
        a.status = "Applied";
        a.notes = notes;
        a.followup = followup;
        return a;
    }

    public void setStatus(long id, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        getWritableDatabase().update("apps", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        getWritableDatabase().delete("apps", "id=?", new String[]{String.valueOf(id)});
    }

    public List<App> all() {
        List<App> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,company,role,applied,status,notes,followup FROM apps ORDER BY applied DESC",
                null);
        while (c.moveToNext()) {
            App a = new App();
            a.id = c.getLong(0);
            a.company = c.getString(1);
            a.role = c.getString(2);
            a.applied = c.getLong(3);
            a.status = c.getString(4);
            a.notes = c.getString(5);
            a.followup = c.getLong(6);
            out.add(a);
        }
        c.close();
        return out;
    }

    // ---------- follow-up reminders (reuses ReminderReceiver) ----------

    static PendingIntent pi(Context c, App a) {
        Intent i = new Intent(c, ReminderReceiver.class);
        i.putExtra("title", "Follow up: " + a.company);
        i.putExtra("time", a.followup);
        return PendingIntent.getBroadcast(c, (int) (100000 + a.id), i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void schedule(Context c, App a) {
        if (!active(a.status) || a.followup <= System.currentTimeMillis()) return;
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, a.followup, pi(c, a));
    }

    public static void cancel(Context c, App a) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi(c, a));
    }

    public static void rescheduleAll(Context c) {
        for (App a : new JobTrackerDb(c).all()) schedule(c, a);
    }
}
