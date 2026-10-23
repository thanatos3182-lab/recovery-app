package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    ArrayAdapter<String> adapter;
    List<JournalDb.Entry> entries = new ArrayList<>();

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("recovery", MODE_PRIVATE);
        db = new JournalDb(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));

        dayText = new TextView(this);
        dayText.setTextSize(56);
        dayText.setTypeface(null, Typeface.BOLD);
        dayText.setGravity(Gravity.CENTER);
        root.addView(dayText);

        subText = new TextView(this);
        subText.setTextSize(16);
        subText.setGravity(Gravity.CENTER);
        root.addView(subText);

        Button dateBtn = new Button(this);
        dateBtn.setText("Set start date");
        dateBtn.setOnClickListener(v -> pickDate());
        root.addView(dateBtn);
        Nav.add(this, root);

        input = new EditText(this);
        input.setHint("How are you doing today?");
        input.setMinLines(3);
        input.setGravity(Gravity.TOP);
        root.addView(input);

        Button saveBtn = new Button(this);
        saveBtn.setText("Save entry");
        saveBtn.setOnClickListener(v -> saveEntry());
        root.addView(saveBtn);

        ListView list = new ListView(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);
        list.setOnItemLongClickListener((p, v, pos, id) -> {
            confirmDelete(entries.get(pos));
            return true;
        });
        root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        adapter.clear();
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        for (JournalDb.Entry e : entries) {
            adapter.add(f.format(new Date(e.time)) + "\n" + e.text);
        }
        adapter.notifyDataSetChanged();
    }
}
