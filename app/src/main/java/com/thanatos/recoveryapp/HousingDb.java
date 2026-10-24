package com.thanatos.recoveryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class HousingDb extends SQLiteOpenHelper {
    public static class Place {
        public long id;
        public String name = "", kind = "", phone = "", cost = "", notes = "";
        public int status;
        public long follow;
    }

    public HousingDb(Context c) { super(c, "housing.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE places (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, kind TEXT, phone TEXT, cost TEXT, notes TEXT, status INTEGER NOT NULL DEFAULT 0, follow INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {}

    ContentValues vals(Place p) {
        ContentValues v = new ContentValues();
        v.put("name", p.name);
        v.put("kind", p.kind);
        v.put("phone", p.phone);
        v.put("cost", p.cost);
        v.put("notes", p.notes);
        v.put("status", p.status);
        v.put("follow", p.follow);
        return v;
    }

    public void add(Place p) {
        getWritableDatabase().insert("places", null, vals(p));
    }

    public void update(Place p) {
        getWritableDatabase().update("places", vals(p), "id=?", new String[]{String.valueOf(p.id)});
    }

    public void delete(long id) {
        getWritableDatabase().delete("places", "id=?", new String[]{String.valueOf(id)});
    }

    static String s(Cursor c, int i) {
        String x = c.getString(i);
        return x == null ? "" : x;
    }

    public List<Place> all() {
        List<Place> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,name,kind,phone,cost,notes,status,follow FROM places ORDER BY id DESC", null);
        while (c.moveToNext()) {
            Place p = new Place();
            p.id = c.getLong(0);
            p.name = s(c, 1);
            p.kind = s(c, 2);
            p.phone = s(c, 3);
            p.cost = s(c, 4);
            p.notes = s(c, 5);
            p.status = c.getInt(6);
            p.follow = c.getLong(7);
            out.add(p);
        }
        c.close();
        return out;
    }
}
