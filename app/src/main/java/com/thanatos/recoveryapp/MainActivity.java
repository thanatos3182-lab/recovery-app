package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    SharedPreferences prefs;
    JournalDb db;
    TextView dayText, subText;
    EditText input;
    LinearLayout entriesBox;
    List<JournalDb.Entry> entries = new ArrayList<>();

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    void go(Class<?> c) {
        startActivity(new Intent(this, c));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("recovery", MODE_PRIVATE);
        db = new JournalDb(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF000000);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));
        scroll.addView(root);

        root.addView(Banner.create(this));

        LinearLayout now = Section.create(this, "Right now");
        Section.addItem(this, now, "Craving SOS", () -> go(CravingSosActivity.class));
        Section.addItem(this, now, "My Plan", () -> go(PlanActivity.class));
        Section.addItem(this, now, "Support contacts", () -> go(ContactsActivity.class));
        Section.addItem(this, now, "Crisis: 988", () -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))));
        root.addView(now);

        LinearLayout daily = Section.create(this, "Daily");
        Section.addItem(this, daily, "Daily check-in", () -> go(CheckinActivity.class));
        Section.addItem(this, daily, "Pattern insights", () -> go(InsightsActivity.class));
        Section.addItem(this, daily, "Milestones and savings", () -> go(MilestonesActivity.class));
        Section.addItem(this, daily, "Appointments", () -> go(AppointmentsActivity.class));
        Section.addItem(this, daily, "Meetings", () -> go(MeetingsActivity.class));
        Section.addItem(this, daily, "Find a sponsor", () -> go(SponsorActivity.class));
        Section.addItem(this, daily, "Set start date", () -> pickDate());
        root.addView(daily);

        LinearLayout life = Section.create(this, "Next steps");
        Section.addItem(this, life, "Local jobs", () -> go(JobsActivity.class));
        Section.addItem(this, life, "Job tracker", () -> go(JobTrackerActivity.class));
        Section.addItem(this, life, "Housing", () -> go(HousingActivity.class));
        root.addView(life);

        LinearLayout learn = Section.create(this, "Learn");
        Section.addItem(this, learn, "Coping skills", () -> go(SkillsActivity.class));
        Section.addItem(this, learn, "Neuroscience Class", () -> go(BrainActivity.class));
        root.addView(learn);

        dayText = new TextView(this);
        dayText.setTextSize(56);
        dayText.setTextColor(0xFF00FF00);
        dayText.setTypeface(null, Typeface.BOLD);
        dayText.setGravity(Gravity.CENTER);
        root.addView(dayText);

        subText = new TextView(this);
        subText.setTextSize(16);
        subText.setTextColor(0xFF00FF00);
        subText.setGravity(Gravity.CENTER);
        root.addView(subText);

        input = new EditText(this);
        input.setTextColor(0xFF00FF00);
        input.setHintTextColor(0xFF00FF00);
        input.setHint("How are you doing today?");
        input.setMinLines(3);
        input.setGravity(Gravity.TOP);
        root.addView(input);

        Button saveBtn = new Button(this);
        saveBtn.setText("Save entry");
        saveBtn.setOnClickListener(v -> saveEntry());
        root.addView(saveBtn);

        entriesBox = new LinearLayout(this);
        entriesBox.setOrientation(LinearLayout.VERTICAL);
        root.addView(entriesBox);

        setContentView(scroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CrashLog.show(this);
        refresh();
    }

    void pickDate() {
        Calendar c = Calendar.getInstance();
        if (prefs.contains("start")) c.setTimeInMillis(prefs.getLong("start", 0));
        DatePickerDialog d = new DatePickerDialog(this, (view, y, m, day) -> {
            Calendar s = Calendar.getInstance();
            s.set(y, m, day, 0, 0, 0);
            s.set(Calendar.MILLISECOND, 0);
            prefs.edit().putLong("start", s.getTimeInMillis()).apply();
            refresh();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.getDatePicker().setMaxDate(System.currentTimeMillis());
        d.show();
    }

    void saveEntry() {
        String t = input.getText().toString().trim();
        if (t.isEmpty()) {
            Toast.makeText(this, "Write something first", Toast.LENGTH_SHORT).show();
            return;
        }
        db.add(t);
        input.setText("");
        refresh();
    }

    void confirmDelete(JournalDb.Entry e) {
        new AlertDialog.Builder(this)
                .setMessage("Delete this entry?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.delete(e.id);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void refresh() {
        if (prefs.contains("start")) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diff = today.getTimeInMillis() - prefs.getLong("start", 0);
            long days = Math.max(0, Math.round(diff / 86400000.0));
            dayText.setText(days + (days == 1 ? " day" : " days"));
            subText.setText("One day at a time.");
        } else {
            dayText.setText("--");
            subText.setText("Set your start date to begin counting.");
        }
        entries = db.all();
        entriesBox.removeAllViews();
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        for (JournalDb.Entry e : entries) {
            TextView t = new TextView(this);
            t.setText(f.format(new Date(e.time)) + "\n" + e.text);
            t.setTextSize(16);
            t.setPadding(dp(8), dp(12), dp(8), dp(12));
            t.setOnLongClickListener(v -> {
                confirmDelete(e);
                return true;
            });
            entriesBox.addView(t);
        }
    }
}
