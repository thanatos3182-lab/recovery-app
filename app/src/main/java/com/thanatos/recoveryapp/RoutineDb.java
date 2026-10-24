package com.thanatos.recoveryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoutineDb extends SQLiteOpenHelper {
    public static class Block {
        public long id;
        public int kind;   // 0 = activity, 1 = high-risk window
        public String title;
        public int start;  // minutes after midnight
        public int end;
    }

    public RoutineDb(Context c) { super(c, "routine.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE blocks (id INTEGER PRIMARY KEY AUTOINCREMENT, kind INTEGER NOT NULL, title TEXT NOT NULL, start INTEGER NOT NULL, endm INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE done (block_id INTEGER NOT NULL, day TEXT NOT NULL, PRIMARY KEY (block_id, day))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {}

    public void add(int kind, String title, int start, int end) {
        ContentValues v = new ContentValues();
        v.put("kind", kind);
        v.put("title", title);
        v.put("start", start);
        v.put("endm", end);
        getWritableDatabase().insert("blocks", null, v);
    }

    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] a = {String.valueOf(id)};
        db.delete("blocks", "id=?", a);
        db.delete("done", "block_id=?", a);
    }

    public List<Block> all() {
        List<Block> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,kind,title,start,endm FROM blocks ORDER BY start ASC, endm ASC", null);
        while (c.moveToNext()) {
            Block b = new Block();
            b.id = c.getLong(0);
            b.kind = c.getInt(1);
            b.title = c.getString(2);
            b.start = c.getInt(3);
            b.end = c.getInt(4);
            out.add(b);
        }
        c.close();
        return out;
    }

    public Set<Long> doneOn(String day) {
        Set<Long> out = new HashSet<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT block_id FROM done WHERE day=?", new String[]{day});
        while (c.moveToNext()) out.add(c.getLong(0));
        c.close();
        return out;
    }

    public void toggleDone(long id, String day) {
        SQLiteDatabase db = getWritableDatabase();
        String[] a = {String.valueOf(id), day};
        int n = db.delete("done", "block_id=? AND day=?", a);
        if (n == 0) {
            ContentValues v = new ContentValues();
            v.put("block_id", id);
            v.put("day", day);
            db.insert("done", null, v);
        }
    }
}
