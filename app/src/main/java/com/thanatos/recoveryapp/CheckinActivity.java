package com.thanatos.recoveryapp;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckinActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    static final String BLOCKS = "\u2581\u2582\u2583\u2584\u2585\u2586\u2587\u2588";

    CheckinDb db;
    SeekBar moodBar, sleepBar, urgeBar;
    EditText win;
    TextView trends, wins, reminderText;

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

    String dayKey(int daysAgo) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime());
    }

    void bind(SeekBar bar, TextView label, String text, int offset) {
        label.setText(text + (bar.getProgress() + offset));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) {
                label.setText(text + (p + offset));
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        db = new CheckinDb(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Daily check-in", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("30 seconds. You can update today's answers any time.", 14));

        TextView moodLabel = tv("", 18);
        moodBar = new SeekBar(this);
        moodBar.setMax(4);
        moodBar.setProgress(2);
        bind(moodBar, moodLabel, "Mood today (1 rough, 5 great): ", 1);
        root.addView(moodLabel);
        root.addView(moodBar);

        TextView sleepLabel = tv("", 18);
        sleepBar = new SeekBar(this);
        sleepBar.setMax(12);
        sleepBar.setProgress(7);
        bind(sleepBar, sleepLabel, "Sleep last night (hours): ", 0);
        root.addView(sleepLabel);
        root.addView(sleepBar);

        TextView urgeLabel = tv("", 18);
        urgeBar = new SeekBar(this);
        urgeBar.setMax(10);
        urgeBar.setProgress(0);
        bind(urgeBar, urgeLabel, "Strongest urge today (0 to 10): ", 0);
        root.addView(urgeLabel);
        root.addView(urgeBar);

        root.addView(tv("One win today, big or small", 18));
        win = new EditText(this);
        win.setHint("e.g. went to a meeting, ate a real meal, made it through Friday");
        win.setHintTextColor(0xFF008800);
        win.setTextColor(GREEN);
        win.setMinLines(2);
        win.setGravity(Gravity.TOP);
        GradientDrawable g = new GradientDrawable();
        g.setColor(BLACK);
        g.setStroke(dp(1), GREEN);
        g.setCornerRadius(dp(8));
        win.setBackground(g);
        win.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.addView(win);

        CheckinDb.Row today = db.get(dayKey(0));
        if (today != null) {
            moodBar.setProgress(today.mood - 1);
            sleepBar.setProgress(Math.round(today.sleep));
            urgeBar.setProgress(today.urge);
            win.setText(today.win == null ? "" : today.win);
        }

        root.addView(ContactsStore.margin(
                ContactsStore.styled(this, "Save check-in", v -> saveToday())));

        root.addView(tv("Last 14 days", 20));
        trends = tv("", 16);
        trends.setTypeface(Typeface.MONOSPACE);
        root.addView(trends);

        root.addView(tv("Recent wins", 20));
        wins = tv("", 16);
        root.addView(wins);

        root.addView(tv("Daily reminder", 20));
        reminderText = tv("", 16);
        root.addView(reminderText);
        root.addView(ContactsStore.margin(
                ContactsStore.styled(this, "Set reminder time", v -> pickTime())));
        root.addView(ContactsStore.margin(
                ContactsStore.styled(this, "Turn reminder off", v -> {
                    CheckinReminder.off(this);
                    refresh();
                })));

        setContentView(scroll);
        refresh();
    }

    void saveToday() {
        db.save(dayKey(0), moodBar.getProgress() + 1, sleepBar.getProgress(),
                urgeBar.getProgress(), win.getText().toString().trim());
        Toast.makeText(this, "Check-in saved", Toast.LENGTH_SHORT).show();
        refresh();
    }

    void pickTime() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        new TimePickerDialog(this, (view, h, m) -> {
            CheckinReminder.set(this, h, m);
            refresh();
        }, CheckinReminder.hour(this), CheckinReminder.minute(this), false).show();
    }

    String spark(Double[] vals, double lo, double hi) {
        StringBuilder sb = new StringBuilder();
        for (Double v : vals) {
            if (v == null) {
                sb.append('\u00b7');
                continue;
            }
            int idx = (int) Math.round((v - lo) / (hi - lo) * 7);
            idx = Math.max(0, Math.min(7, idx));
            sb.append(BLOCKS.charAt(idx));
        }
        return sb.toString();
    }

    String reminderStatus() {
        if (!CheckinReminder.enabled(this)) return "Reminder is off.";
        int h = CheckinReminder.hour(this);
        int m = CheckinReminder.minute(this);
        return String.format(Locale.US, "Reminder daily at %d:%02d %s",
                (h % 12 == 0 ? 12 : h % 12), m, h < 12 ? "AM" : "PM");
    }

    void refresh() {
        List<CheckinDb.Row> rows = db.since(dayKey(13));
        Map<String, CheckinDb.Row> byDay = new HashMap<>();
        for (CheckinDb.Row r : rows) byDay.put(r.day, r);

        Double[] mood = new Double[14];
        Double[] urge = new Double[14];
        double sleepSum = 0;
        int n = 0;
        for (int i = 0; i < 14; i++) {
            CheckinDb.Row r = byDay.get(dayKey(13 - i));
            if (r == null) continue;
            mood[i] = (double) r.mood;
            urge[i] = (double) r.urge;
            sleepSum += r.sleep;
            n++;
        }
        String t = "Mood   " + spark(mood, 1, 5)
                + "\nUrge   " + spark(urge, 0, 10)
                + "\n(oldest to newest, dot = no check-in)"
                + "\nCheck-ins: " + n + " of last 14 days";
        if (n > 0) {
            t += "\nAvg sleep: " + String.format(Locale.US, "%.1f", sleepSum / n) + " hrs";
        }
        trends.setText(t);

        StringBuilder sb = new StringBuilder();
        int shown = 0;
        for (CheckinDb.Row r : db.latest(60)) {
            if (r.win == null || r.win.isEmpty()) continue;
            sb.append(r.day).append(": ").append(r.win).append("\n");
            if (++shown >= 5) break;
        }
        wins.setText(sb.length() == 0 ? "Your wins will show up here." : sb.toString().trim());

        reminderText.setText(reminderStatus());
    }
}
