package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

public class PlanStore {
    static SharedPreferences p(Context c) {
        return c.getSharedPreferences("plan", Context.MODE_PRIVATE);
    }

    public static String get(Context c, String key) {
        return p(c).getString(key, "");
    }

    public static void put(Context c, String key, String val) {
        p(c).edit().putString(key, val).apply();
    }

    public static void peek(Activity a, int urge, Runnable next) {
        String reasons = get(a, "reasons").trim();
        String go = get(a, "gotoPlan").trim();
        if (urge < 7 || (reasons.isEmpty() && go.isEmpty())) {
            next.run();
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (!reasons.isEmpty()) sb.append("MY REASONS\n").append(reasons).append("\n\n");
        if (!go.isEmpty()) sb.append("MY PLAN WHEN IT HITS\n").append(go);
        new AlertDialog.Builder(a)
                .setTitle("Remember why you're doing this")
                .setMessage(sb.toString().trim())
                .setCancelable(false)
                .setPositiveButton("Continue", (d, w) -> next.run())
                .show();
    }
}
