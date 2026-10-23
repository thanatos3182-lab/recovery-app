package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContactsStore {
    public static final String MSG = "Rough moment. Can you talk or stay on text for a bit?";

    static JSONArray load(Context c) {
        try {
            return new JSONArray(c.getSharedPreferences("contacts", Context.MODE_PRIVATE)
                    .getString("list", "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    static void save(Context c, JSONArray arr) {
        c.getSharedPreferences("contacts", Context.MODE_PRIVATE)
                .edit().putString("list", arr.toString()).apply();
    }

    public static void add(Context c, String name, String number, String role) {
        try {
            JSONArray arr = load(c);
            JSONObject o = new JSONObject();
            o.put("name", name);
            o.put("number", number);
            o.put("role", role);
            arr.put(o);
            save(c, arr);
        } catch (Exception ignore) {
        }
    }

    public static void remove(Context c, int index) {
        JSONArray old = load(c);
        JSONArray out = new JSONArray();
        for (int i = 0; i < old.length(); i++) {
            if (i != index) out.put(old.opt(i));
        }
        save(c, out);
    }

    static String clean(String n) {
        return n.replaceAll("[^0-9+]", "");
    }

    public static void call(Activity a, String number) {
        a.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + clean(number))));
    }

    public static void text(Activity a, String number) {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + clean(number)));
        i.putExtra("sms_body", MSG);
        a.startActivity(i);
    }

    public static Button styled(Activity a, String text, View.OnClickListener l) {
        Button x = new Button(a);
        x.setText(text);
        x.setAllCaps(false);
        x.setTextColor(0xFF00FF00);
        GradientDrawable g = new GradientDrawable();
        g.setColor(0xFF000000);
        g.setStroke(Math.round(a.getResources().getDisplayMetrics().density), 0xFF00FF00);
        g.setCornerRadius(24f);
        x.setBackground(g);
        x.setOnClickListener(l);
        return x;
    }

    static Button margin(Button b) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 12, 16, 12);
        b.setLayoutParams(lp);
        return b;
    }

    // Used inside Craving SOS "Reach out" skill
    public static void addButtons(Activity a, LinearLayout box) {
        JSONArray arr = load(a);
        if (arr.length() == 0) {
            TextView t = new TextView(a);
            t.setText("No contacts saved yet. Add them under Recovery Tools > Support contacts.");
            t.setTextColor(0xFF00FF00);
            t.setTextSize(16);
            box.addView(t);
            return;
        }
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;
            final String name = o.optString("name");
            final String number = o.optString("number");
            box.addView(margin(styled(a, "Call " + name, v -> call(a, number))));
            box.addView(margin(styled(a, "Text " + name, v -> text(a, number))));
        }
    }
}
