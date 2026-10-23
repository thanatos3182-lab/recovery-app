package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashLog {
    static void save(Context c, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        c.getSharedPreferences("crash", Context.MODE_PRIVATE)
                .edit().putString("trace", sw.toString()).commit();
    }

    public static void show(Activity a) {
        SharedPreferences p = a.getSharedPreferences("crash", Context.MODE_PRIVATE);
        String s = p.getString("trace", null);
        if (s == null) return;
        p.edit().remove("trace").apply();
        if (s.length() > 2500) s = s.substring(0, 2500);
        new AlertDialog.Builder(a)
                .setTitle("Last crash")
                .setMessage(s)
                .setPositiveButton("OK", null)
                .show();
    }
}
