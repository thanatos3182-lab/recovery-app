package com.thanatos.recoveryapp;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PlanActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    static final String[][] FIELDS = {
        {"reasons", "My reasons for recovery",
         "Why I'm doing this: the people, goals, and life I want..."},
        {"triggers", "My triggers",
         "People, places, feelings, and times of day that set off cravings..."},
        {"warnings", "My early warning signs",
         "What I notice in my thoughts, mood, or behavior before a craving or slip..."},
        {"gotoPlan", "When it hits, I will...",
         "My first 3 moves, and who I'll call..."}
    };
    EditText[] boxes = new EditText[FIELDS.length];

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

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("My Plan", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Write this when you're feeling steady. It shows up in Craving SOS when the urge is 7 or higher, which is when you need your own words most.", 14));

        for (int i = 0; i < FIELDS.length; i++) {
            root.addView(tv(FIELDS[i][1], 20));
            EditText e = new EditText(this);
            e.setHint(FIELDS[i][2]);
            e.setHintTextColor(0xFF008800);
            e.setTextColor(GREEN);
            e.setMinLines(3);
            e.setGravity(Gravity.TOP);
            e.setText(PlanStore.get(this, FIELDS[i][0]));
            e.setBackground(outline());
            e.setPadding(dp(12), dp(12), dp(12), dp(12));
            boxes[i] = e;
            root.addView(e);
        }

        Button save = new Button(this);
        save.setText("Save");
        save.setAllCaps(false);
        save.setTextColor(GREEN);
        save.setBackground(outline());
        save.setOnClickListener(v -> {
            save();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(16), 0, 0);
        root.addView(save, lp);

        setContentView(scroll);
    }

    void save() {
        for (int i = 0; i < boxes.length; i++) {
            PlanStore.put(this, FIELDS[i][0], boxes[i].getText().toString().trim());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }
}
