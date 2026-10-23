package com.thanatos.recoveryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class CheckinDb extends SQLiteOpenHelper {
    public static class Row {
        public String day;
        public int mood;
        public float sleep;
        public int urge;
        public String win;
    }

    public CheckinDb(Context c) { super(c, "checkins.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE checkins (day TEXT PRIMARY KEY, mood INTEGER, sleep REAL, urge INTEGER, win TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {}

    public void save(String day, int mood, float sleep, int urge, String win) {
        ContentValues v = new ContentValues();
        v.put("day", day);
        v.put("mood", mood);
        v.put("sleep", sleep);
        v.put("urge", urge);
        v.put("win", win);
        getWritableDatabase().insertWithOnConflict("checkins", null, v,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    static Row row(Cursor c) {
        Row r = new Row();
        r.day = c.getString(0);
        r.mood = c.getInt(1);
        r.sleep = c.getFloat(2);
        r.urge = c.getInt(3);
        r.win = c.getString(4);
        return r;
    }

    public Row get(String day) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT day,mood,sleep,urge,win FROM checkins WHERE day=?", new String[]{day});
        Row r = c.moveToFirst() ? row(c) : null;
        c.close();
        return r;
    }

    public List<Row> since(String fromDay) {
        List<Row> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT day,mood,sleep,urge,win FROM checkins WHERE day >= ? ORDER BY day ASC",
                new String[]{fromDay});
        while (c.moveToNext()) out.add(row(c));
        c.close();
        return out;
    }

    public List<Row> latest(int n) {
        List<Row> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT day,mood,sleep,urge,win FROM checkins ORDER BY day DESC LIMIT " + n, null);
        while (c.moveToNext()) out.add(row(c));
        c.close();
        return out;
    }
}
