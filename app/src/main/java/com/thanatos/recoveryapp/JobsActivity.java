package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class JobsActivity extends Activity {
    SharedPreferences prefs;
    EditText zip, what;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("jobs", MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Local jobs");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView note = new TextView(this);
        note.setText("Enter your ZIP (or City, ST) once, then tap a site to see openings near you. It's saved on this phone only.");
        note.setPadding(0, dp(8), 0, dp(8));
        root.addView(note);

        zip = new EditText(this);
        zip.setHint("ZIP code or City, ST");
        zip.setText(prefs.getString("zip", ""));
        root.addView(zip);

        what = new EditText(this);
        what.setHint("Search (e.g. part time, warehouse, cashier)");
        what.setText(prefs.getString("what", "part time"));
        root.addView(what);

        addBtn(root, "Indeed (newest first)", v -> open(
                "https://www.indeed.com/jobs?q=" + Uri.encode(q())
                + "&l=" + Uri.encode(z()) + "&sort=date", true));

        addBtn(root, "Google Jobs", v -> open(
                "https://www.google.com/search?q=" + Uri.encode(q() + " jobs near " + z())
                + "&ibp=htl;jobs", true));

        addBtn(root, "LinkedIn (part-time)", v -> open(
                "https://www.linkedin.com/jobs/search/?keywords=" + Uri.encode(q())
                + "&location=" + Uri.encode(z()) + "&f_JT=P", true));

        addBtn(root, "Snagajob (hourly work)", v -> open("https://www.snagajob.com/", false));

        addBtn(root, "CareerOneStop (free job help + local job centers)",
                v -> open("https://www.careeronestop.org/", false));

        setContentView(scroll);
    }

    String z() { return zip.getText().toString().trim(); }

    String q() {
        String s = what.getText().toString().trim();
        return s.isEmpty() ? "part time" : s;
    }

    void addBtn(LinearLayout root, String label, View.OnClickListener l) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setAllCaps(false);
        btn.setOnClickListener(l);
        root.addView(btn);
    }

    void open(String url, boolean needsLocation) {
        if (needsLocation && z().isEmpty()) {
            Toast.makeText(this, "Enter your ZIP or city first", Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit().putString("zip", z()).putString("what", q()).apply();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
