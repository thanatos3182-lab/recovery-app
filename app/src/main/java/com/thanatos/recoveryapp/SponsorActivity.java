package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SponsorActivity extends Activity {
    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Find a sponsor");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        header(root, "How it works");
        text(root, "A sponsor is someone further along in recovery who walks you through it. "
                + "You don't have to find the perfect one. Most people start with a temporary sponsor "
                + "and switch if it isn't a fit.\n\n"
                + "1. Go to several different meetings. Online ones count.\n"
                + "2. Notice who you relate to when they share.\n"
                + "3. Ask them after the meeting, or ask the group for a temporary sponsor.\n"
                + "4. Swap numbers and try a few calls before deciding.");

        header(root, "Good things to ask");
        text(root, "- How long have you been in recovery?\n"
                + "- Have you worked the steps (or your program's approach)?\n"
                + "- How often are you open to talking?\n"
                + "- What do you expect from the people you sponsor?");

        header(root, "Find people (online meetings and communities)");
        link(root, "AA online meetings (Online Intergroup)", "https://aa-intergroup.org/");
        link(root, "NA meeting search", "https://www.na.org/meetingsearch/");
        link(root, "SMART Recovery community", "https://www.smartrecovery.org/community/");
        link(root, "In The Rooms (free online recovery community)", "https://www.intherooms.com/");
        link(root, "Sober Grid (peer support app)", "https://www.sobergrid.com/");
        link(root, "Celebrate Recovery (faith-based)", "https://www.celebraterecovery.com/");

        setContentView(scroll);
    }

    void header(LinearLayout root, String t) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(18);
        v.setPadding(0, dp(20), 0, dp(4));
        root.addView(v);
    }

    void text(LinearLayout root, String t) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(15);
        root.addView(v);
    }

    void link(LinearLayout root, String label, String url) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setAllCaps(false);
        btn.setOnClickListener((View v) ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
        root.addView(btn);
    }
}
