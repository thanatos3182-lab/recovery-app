package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BackupActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    static final int REQ_EXPORT = 1;
    static final int REQ_RESTORE = 2;
    static final String[] PREFS = {"recovery", "live", "checkin", "contacts", "housing",
            "jobs", "meetings", "milestones", "plan", "basics"};
    static final String[] DBS = {"appointments.db", "checkins.db", "housing.db",
            "jobtracker.db", "journal.db", "routine.db"};

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    TextView tv(String text, int sp) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(sp);
        t.setTextColor(GREEN);
        t.setPadding(0, dp(10), 0, dp(6));
        return t;
    }

    GradientDrawable outline() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(BLACK);
        g.setStroke(dp(1), GREEN);
        g.setCornerRadius(dp(8));
        return g;
    }

    Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(GREEN);
        b.setBackground(outline());
        return b;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Backup and restore", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Save everything in the app (journal, plan, check-ins, contacts, trackers, progress) to one file, so you can move to a new phone or a new version without losing it.", 15));
        root.addView(tv("The file is plain text and includes your journal. Keep it somewhere private, like your own email or a password-protected folder.", 15));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(16), 0, 0);

        Button exp = button("Save a backup");
        exp.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/json");
            i.putExtra(Intent.EXTRA_TITLE, "chrysalis-backup-"
                    + new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()) + ".json");
            startActivityForResult(i, REQ_EXPORT);
        });
        root.addView(exp, lp);

        Button res = button("Restore from a backup");
        res.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(i, REQ_RESTORE);
        });
        root.addView(res, lp);

        setContentView(scroll);
    }

    @Override
    protected void onActivityResult(int req, int result, Intent data) {
        super.onActivityResult(req, result, data);
        if (result != RESULT_OK || data == null || data.getData() == null) return;
        final Uri uri = data.getData();
        if (req == REQ_EXPORT) {
            export(uri);
        } else if (req == REQ_RESTORE) {
            new AlertDialog.Builder(this)
                    .setTitle("Restore this backup?")
                    .setMessage("This replaces everything currently in the app with what is in the backup file.")
                    .setPositiveButton("Restore", (dlg, w) -> restore(uri))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // ---------- export ----------

    void export(Uri uri) {
        try {
            JSONObject root = new JSONObject();
            root.put("app", "chrysalis");
            root.put("version", 1);
            root.put("created", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date()));
            JSONObject prefs = new JSONObject();
            for (String name : PREFS) prefs.put(name, dumpPrefs(name));
            root.put("prefs", prefs);
            JSONObject dbs = new JSONObject();
            for (String name : DBS) {
                JSONObject one = dumpDb(name);
                if (one != null) dbs.put(name, one);
            }
            root.put("dbs", dbs);
            OutputStream os = getContentResolver().openOutputStream(uri, "wt");
            os.write(root.toString().getBytes("UTF-8"));
            os.close();
            Toast.makeText(this, "Backup saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Backup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    JSONObject dumpPrefs(String name) throws Exception {
        JSONObject out = new JSONObject();
        Map<String, ?> all = getSharedPreferences(name, MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> en : all.entrySet()) {
            Object v = en.getValue();
            JSONObject item = new JSONObject();
            if (v instanceof Boolean) {
                item.put("t", "b");
                item.put("v", ((Boolean) v).booleanValue());
            } else if (v instanceof Integer) {
                item.put("t", "i");
                item.put("v", ((Integer) v).intValue());
            } else if (v instanceof Long) {
                item.put("t", "l");
                item.put("v", ((Long) v).longValue());
            } else if (v instanceof Float) {
                item.put("t", "f");
                item.put("v", (double) ((Float) v).floatValue());
            } else if (v instanceof Set) {
                item.put("t", "ss");
                JSONArray a = new JSONArray();
                for (Object o : (Set<?>) v) a.put(String.valueOf(o));
                item.put("v", a);
            } else if (v != null) {
                item.put("t", "s");
                item.put("v", v.toString());
            } else {
                continue;
            }
            out.put(en.getKey(), item);
        }
        return out;
    }

    JSONObject dumpDb(String name) throws Exception {
        File f = getDatabasePath(name);
        if (!f.exists()) return null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(f.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        JSONObject out = new JSONObject();
        try {
            ArrayList<String> tables = new ArrayList<>();
            Cursor tc = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' "
                    + "AND name NOT LIKE 'sqlite_%' AND name <> 'android_metadata'", null);
            while (tc.moveToNext()) tables.add(tc.getString(0));
            tc.close();
            for (String t : tables) {
                JSONArray rows = new JSONArray();
                Cursor c = db.rawQuery("SELECT * FROM \"" + t + "\"", null);
                while (c.moveToNext()) {
                    JSONObject row = new JSONObject();
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        String col = c.getColumnName(i);
                        switch (c.getType(i)) {
                            case Cursor.FIELD_TYPE_NULL:
                                row.put(col, JSONObject.NULL);
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(col, c.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(col, c.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_BLOB:
                                row.put(col, new JSONObject().put("b64",
                                        Base64.encodeToString(c.getBlob(i), Base64.NO_WRAP)));
                                break;
                            default:
                                row.put(col, c.getString(i));
                        }
                    }
                    rows.put(row);
                }
                c.close();
                out.put(t, rows);
            }
        } finally {
            db.close();
        }
        return out;
    }

    // ---------- restore ----------

    void ensureSchemas() {
        new AppointmentDb(this).getWritableDatabase().close();
        new CheckinDb(this).getWritableDatabase().close();
        new HousingDb(this).getWritableDatabase().close();
        new JobTrackerDb(this).getWritableDatabase().close();
        new JournalDb(this).getWritableDatabase().close();
        new RoutineDb(this).getWritableDatabase().close();
    }

    void restore(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int r;
            while ((r = is.read(buf)) > 0) bo.write(buf, 0, r);
            is.close();
            JSONObject root = new JSONObject(new String(bo.toByteArray(), "UTF-8"));
            if (!"chrysalis".equals(root.optString("app"))) {
                Toast.makeText(this, "That is not a Chrysalis backup file", Toast.LENGTH_LONG).show();
                return;
            }
            ensureSchemas();
            JSONObject prefs = root.optJSONObject("prefs");
            if (prefs != null) {
                for (String name : PREFS) {
                    JSONObject p = prefs.optJSONObject(name);
                    if (p != null) loadPrefs(name, p);
                }
            }
            JSONObject dbs = root.optJSONObject("dbs");
            if (dbs != null) {
                for (String name : DBS) {
                    JSONObject one = dbs.optJSONObject(name);
                    if (one != null) loadDb(name, one);
                }
            }
            new AlertDialog.Builder(this)
                    .setTitle("Restored")
                    .setMessage("Your data is back. The app will close now. Open it again to see everything.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dlg, w) -> finishAffinity())
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Restore failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void loadPrefs(String name, JSONObject p) throws Exception {
        SharedPreferences.Editor e = getSharedPreferences(name, MODE_PRIVATE).edit();
        e.clear();
        Iterator<String> it = p.keys();
        while (it.hasNext()) {
            String k = it.next();
            JSONObject item = p.getJSONObject(k);
            String t = item.getString("t");
            switch (t) {
                case "b":
                    e.putBoolean(k, item.getBoolean("v"));
                    break;
                case "i":
                    e.putInt(k, item.getInt("v"));
                    break;
                case "l":
                    e.putLong(k, item.getLong("v"));
                    break;
                case "f":
                    e.putFloat(k, (float) item.getDouble("v"));
                    break;
                case "ss": {
                    JSONArray a = item.getJSONArray("v");
                    Set<String> s = new HashSet<>();
                    for (int i = 0; i < a.length(); i++) s.add(a.getString(i));
                    e.putStringSet(k, s);
                    break;
                }
                default:
                    e.putString(k, item.getString("v"));
            }
        }
        e.commit();
    }

    void loadDb(String name, JSONObject d) throws Exception {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                getDatabasePath(name).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        try {
            db.beginTransaction();
            try {
                Iterator<String> it = d.keys();
                while (it.hasNext()) {
                    String t = it.next();
                    Cursor chk = db.rawQuery(
                            "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?", new String[]{t});
                    boolean exists = chk.moveToFirst();
                    chk.close();
                    if (!exists) continue;
                    db.execSQL("DELETE FROM \"" + t + "\"");
                    JSONArray rows = d.getJSONArray(t);
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.getJSONObject(i);
                        ContentValues cv = new ContentValues();
                        Iterator<String> ck = row.keys();
                        while (ck.hasNext()) {
                            String col = ck.next();
                            Object v = row.get(col);
                            if (v == JSONObject.NULL) {
                                cv.putNull(col);
                            } else if (v instanceof Double) {
                                cv.put(col, (Double) v);
                            } else if (v instanceof Number) {
                                cv.put(col, ((Number) v).longValue());
                            } else if (v instanceof JSONObject) {
                                cv.put(col, Base64.decode(((JSONObject) v).getString("b64"), Base64.NO_WRAP));
                            } else {
                                cv.put(col, v.toString());
                            }
                        }
                        db.insertOrThrow(t, null, cv);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
    }
}
