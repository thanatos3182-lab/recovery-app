package com.thanatos.recoveryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDb extends SQLiteOpenHelper {
    public static class Appt {
        public long id;
        public String title;
        public long time;
    }

    public AppointmentDb(Context c) { super(c, "appointments.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE appts (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, time INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {}

    public Appt add(String title, long time) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("time", time);
        Appt a = new Appt();
        a.id = getWritableDatabase().insert("appts", null, v);
        a.title = title;
        a.time = time;
        return a;
    }

    public void delete(long id) {
        getWritableDatabase().delete("appts", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Appt> upcoming() {
        List<Appt> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,title,time FROM appts WHERE time >= ? ORDER BY time ASC",
                new String[]{String.valueOf(System.currentTimeMillis() - 3600000L)});
        while (c.moveToNext()) {
            Appt a = new Appt();
            a.id = c.getLong(0);
            a.title = c.getString(1);
            a.time = c.getLong(2);
            out.add(a);
        }
        c.close();
        return out;
    }
}
