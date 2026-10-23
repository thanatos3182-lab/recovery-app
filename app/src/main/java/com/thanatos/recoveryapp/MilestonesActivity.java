package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MilestonesActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    static final int[] MILESTONES = {1, 3, 7, 14, 30, 60, 90, 120, 180, 270, 365, 500, 730, 1000, 1095};

    SharedPreferences rp, mp;
    EditText weekly, goalName, goalAmt;
    TextView summary, list;

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

    EditText field(String hint, String saved, boolean number) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(0xFF008800);
        e.setTextColor(GREEN);
        e.setText(saved);
        if (number) {
            e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        GradientDrawable g = new GradientDrawable();
        g.setColor(BLACK);
        g.setStroke(dp(1), GREEN);
        g.setCornerRadius(dp(8));
        e.setBackground(g);
        e.setPadding(dp(12), dp(12), dp(12), dp(12));
        return e;
    }

    double num(EditText e) {
        try {
            return Double.parseDouble(e.getText().toString().trim());
        } catch (Exception x) {
            return 0;
        }
    }

    String money(double v) {
        return String.format(Locale.US, "$%,.2f", v);
    }

    long days() {
        if (!rp.contains("start")) return -1;
        Calendar t = Calendar.getInstance();
        t.set(Calendar.HOUR_OF_DAY, 0);
        t.set(Calendar.MINUTE, 0);
        t.set(Calendar.SECOND, 0);
        t.set(Calendar.MILLISECOND, 0);
        long diff = t.getTimeInMillis() - rp.getLong("start", 0);
        return Math.max(0, Math.round(diff / 86400000.0));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        rp = getSharedPreferences("recovery", MODE_PRIVATE);
        mp = getSharedPreferences("milestones", MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Milestones and savings", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        summary = tv("", 24);
        root.addView(summary);

        root.addView(tv("Money saved (optional)", 20));
        root.addView(tv("About how much did you spend per week on what you're leaving behind?", 14));
        weekly = field("Weekly amount, e.g. 60", mp.getString("weekly", ""), true);
        root.addView(weekly);
        root.addView(tv("Saving toward something? (optional)", 14));
        goalName = field("Goal, e.g. new tires", mp.getString("goalName", ""), false);
        root.addView(goalName);
        goalAmt = field("Goal amount, e.g. 400", mp.getString("goalAmt", ""), true);
        root.addView(goalAmt);
        root.addView(ContactsStore.margin(ContactsStore.styled(this, "Save", v -> {
            mp.edit()
                    .putString("weekly", weekly.getText().toString().trim())
                    .putString("goalName", goalName.getText().toString().trim())
                    .putString("goalAmt", goalAmt.getText().toString().trim())
                    .apply();
            refresh();
        })));

        root.addView(tv("Milestones", 20));
        list = tv("", 18);
        root.addView(list);

        setContentView(scroll);
        refresh();
    }

    void refresh() {
        long d = days();
        if (d < 0) {
            summary.setText("Set your start date on the main screen to see your milestones.");
            list.setText("");
            return;
        }

        StringBuilder s = new StringBuilder();
        s.append(d).append(d == 1 ? " day" : " days");
        double w = num(weekly);
        double saved = d * w / 7.0;
        if (w > 0) s.append("\n").append(money(saved)).append(" saved so far");
        double g = num(goalAmt);
        if (g > 0) {
            int pct = (int) Math.min(100, Math.round(saved / g * 100));
            String gn = goalName.getText().toString().trim();
            s.append("\nGoal").append(gn.isEmpty() ? "" : " (" + gn + ")")
                    .append(": ").append(money(saved)).append(" of ").append(money(g))
                    .append(" (").append(pct).append("%)");
        }
        summary.setText(s.toString());

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(rp.getLong("start", 0));
        DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM);
        StringBuilder m = new StringBuilder();
        boolean nextMarked = false;
        for (int ms : MILESTONES) {
            if (d >= ms) {
                m.append("\u2713  Day ").append(ms).append("\n");
            } else {
                Calendar c = (Calendar) start.clone();
                c.add(Calendar.DAY_OF_YEAR, ms);
                long left = ms - d;
                m.append(nextMarked ? "\u25CB  " : "\u2192  ")
                        .append("Day ").append(ms).append(" - ").append(f.format(c.getTime()))
                        .append(" (in ").append(left).append(left == 1 ? " day)" : " days)")
                        .append("\n");
                nextMarked = true;
            }
        }
        list.setText(m.toString().trim());
    }
}
