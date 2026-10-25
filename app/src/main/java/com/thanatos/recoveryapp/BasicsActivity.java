package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class BasicsActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    SharedPreferences prefs;
    EditText zip;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    TextView tv(String text, int sp) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(sp);
        t.setTextColor(GREEN);
        t.setPadding(0, dp(6), 0, dp(4));
        return t;
    }

    GradientDrawable outline() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(BLACK);
        g.setStroke(dp(1), GREEN);
        g.setCornerRadius(dp(8));
        return g;
    }

    LinearLayout vbox() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    String readAsset(String name) throws Exception {
        InputStream is = getAssets().open(name);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int r;
        while ((r = is.read(buf)) > 0) bo.write(buf, 0, r);
        is.close();
        return new String(bo.toByteArray(), "UTF-8");
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("basics", MODE_PRIVATE);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = vbox();
        root.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Basics", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Step-by-step checklists for getting back on your feet. Your progress is saved on this phone only.", 14));

        zip = new EditText(this);
        zip.setHint("ZIP code or City, ST (for \"near me\" links)");
        zip.setHintTextColor(0xFF008800);
        zip.setTextColor(GREEN);
        zip.setText(prefs.getString("zip", getSharedPreferences("jobs", MODE_PRIVATE).getString("zip", "")));
        zip.setBackground(outline());
        zip.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.addView(zip);

        try {
            JSONArray lists = new JSONArray(readAsset("basics.json"));
            for (int i = 0; i < lists.length(); i++) addList(root, lists.getJSONObject(i));
        } catch (Exception e) {
            root.addView(tv("Could not load Basics: " + e.getMessage(), 14));
        }
        setContentView(scroll);
    }

    void addList(LinearLayout root, JSONObject list) throws JSONException {
        final String id = list.getString("id");
        final String listTitle = list.getString("title");
        JSONArray steps = list.getJSONArray("steps");
        final String[] keys = new String[steps.length()];
        for (int i = 0; i < keys.length; i++) keys[i] = id + ":" + steps.getJSONObject(i).getString("id");

        final LinearLayout body = vbox();
        body.setVisibility(View.GONE);
        final TextView head = tv("", 19);
        head.setMinHeight(dp(48));
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(0, dp(10), 0, dp(10));
        final Runnable upd = () -> {
            int n = 0;
            for (String k : keys) if (prefs.getBoolean(k, false)) n++;
            head.setText((body.getVisibility() == View.VISIBLE ? "\u25BE " : "\u25B8 ")
                    + listTitle + "  (" + n + " of " + keys.length + ")");
        };
        head.setOnClickListener(v -> {
            body.setVisibility(body.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            upd.run();
        });
        root.addView(head);
        body.addView(tv(list.getString("intro"), 14));
        for (int i = 0; i < steps.length(); i++) addStep(body, steps.getJSONObject(i), keys[i], upd);
        root.addView(body);
        upd.run();
    }

    void addStep(LinearLayout body, JSONObject step, final String key, final Runnable upd) throws JSONException {
        LinearLayout card = vbox();
        card.setBackground(outline());
        card.setPadding(dp(12), dp(8), dp(12), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(8), 0, dp(4));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        String stepTitle = step.getString("title");
        CheckBox cb = new CheckBox(this);
        cb.setChecked(prefs.getBoolean(key, false));
        cb.setButtonTintList(ColorStateList.valueOf(GREEN));
        cb.setContentDescription("Mark done: " + stepTitle);
        cb.setOnCheckedChangeListener((v, checked) -> {
            prefs.edit().putBoolean(key, checked).apply();
            upd.run();
        });
        row.addView(cb);
        final TextView t = tv(stepTitle, 17);
        t.setMinHeight(dp(44));
        t.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(t, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        card.addView(row);

        final LinearLayout detail = vbox();
        detail.setVisibility(View.GONE);
        t.setOnClickListener(v -> detail.setVisibility(
                detail.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        detail.addView(tv(step.getString("why"), 15));
        addBullets(detail, step.optString("bringLabel", "What to bring"), step.getJSONArray("bring"));
        addBullets(detail, "Good to know", step.getJSONArray("tips"));

        JSONArray links = step.getJSONArray("links");
        for (int i = 0; i < links.length(); i++) {
            JSONObject l = links.getJSONObject(i);
            final String url = l.optString("url", "");
            final String search = l.optString("search", "");
            TextView a = tv(l.getString("label"), 15);
            a.setPaintFlags(a.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            a.setMinHeight(dp(44));
            a.setGravity(Gravity.CENTER_VERTICAL);
            a.setOnClickListener(v -> {
                String target = url;
                if (!search.isEmpty()) {
                    String z = zip.getText().toString().trim();
                    if (z.isEmpty()) {
                        Toast.makeText(this, "Enter your ZIP or city at the top first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    target = "https://www.google.com/search?q=" + Uri.encode(search + z);
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(target)));
            });
            detail.addView(a);
        }
        card.addView(detail);
        body.addView(card, lp);
    }

    void addBullets(LinearLayout box, String label, JSONArray items) throws JSONException {
        TextView h = tv(label, 15);
        h.setTypeface(null, android.graphics.Typeface.BOLD);
        h.setPadding(0, dp(10), 0, dp(2));
        box.addView(h);
        for (int i = 0; i < items.length(); i++) box.addView(tv("\u2022 " + items.getString(i), 14));
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.edit().putString("zip", zip.getText().toString().trim()).apply();
    }
}
