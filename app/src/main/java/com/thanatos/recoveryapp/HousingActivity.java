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

public class HousingActivity extends Activity {
    SharedPreferences prefs;
    EditText zip;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("housing", MODE_PRIVATE);
        String saved = prefs.getString("zip",
                getSharedPreferences("jobs", MODE_PRIVATE).getString("zip", ""));

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Housing");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView note = new TextView(this);
        note.setText("Enter your ZIP (or City, ST) once, then tap a site. It's saved on this phone only.");
        note.setPadding(0, dp(8), 0, dp(8));
        root.addView(note);

        zip = new EditText(this);
        zip.setHint("ZIP code or City, ST");
        zip.setText(saved);
        root.addView(zip);

        header(root, "Sober living / recovery housing");
        addBtn(root, "Sober living near me (Google)", v -> open(
                "https://www.google.com/search?q=" + Uri.encode("sober living homes near " + z()), true));
        addBtn(root, "Oxford House (affordable, peer-run)", v -> open("https://www.oxfordhouse.org/", false));
        addBtn(root, "NARR (certified recovery residences)", v -> open("https://narronline.org/", false));
        addBtn(root, "SAMHSA treatment and housing finder", v -> open("https://findtreatment.gov/", false));

        header(root, "Apartments and rooms");
        addBtn(root, "Zillow rentals", v -> open(
                "https://www.zillow.com/homes/for_rent/" + slug() + "_rb/", true));
        addBtn(root, "Apartments.com", v -> open(
                "https://www.apartments.com/" + slug() + "/", true));
        addBtn(root, "Rooms for rent (Google)", v -> open(
                "https://www.google.com/search?q=" + Uri.encode("rooms for rent near " + z()), true));

        header(root, "Help paying / finding a place");
        addBtn(root, "211 (local housing help)", v -> open("https://www.211.org/", false));
        addBtn(root, "HUD (find housing assistance)", v -> open("https://www.hud.gov/findshelter", false));

        setContentView(scroll);
    }

    String z() { return zip.getText().toString().trim(); }

    String slug() {
        return z().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    void header(LinearLayout root, String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(18);
        t.setPadding(0, dp(20), 0, dp(4));
        root.addView(t);
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
        prefs.edit().putString("zip", z()).apply();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
