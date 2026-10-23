package com.thanatos.recoveryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class JournalDb extends SQLiteOpenHelper {
    public static class Entry {
        public long id;
        public long time;
        public String text;
    }

    public JournalDb(Context c) { super(c, "journal.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE entries (id INTEGER PRIMARY KEY AUTOINCREMENT, time INTEGER NOT NULL, text TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {}

    public void add(String text) {
        ContentValues v = new ContentValues();
        v.put("time", System.currentTimeMillis());
        v.put("text", text);
        getWritableDatabase().insert("entries", null, v);
    }

    public void delete(long id) {
        getWritableDatabase().delete("entries", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Entry> all() {
        List<Entry> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT id,time,text FROM entries ORDER BY time DESC", null);
        while (c.moveToNext()) {
            Entry e = new Entry();
            e.id = c.getLong(0);
            e.time = c.getLong(1);
            e.text = c.getString(2);
            out.add(e);
        }
        c.close();
        return out;
    }
}
