package com.thanatos.recoveryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentsActivity extends Activity {
    AppointmentDb db;
    ArrayAdapter<String> adapter;
    List<AppointmentDb.Appt> appts = new ArrayList<>();

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        db = new AppointmentDb(this);

        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));

        TextView title = new TextView(this);
        title.setText("Appointments");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        Button add = new Button(this);
        add.setText("Add appointment");
        add.setOnClickListener(v -> startAdd());
        root.addView(add);

        TextView hint = new TextView(this);
        hint.setText("Long-press an appointment to delete it.");
        hint.setGravity(Gravity.CENTER);
        root.addView(hint);

        ListView list = new ListView(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);
        list.setOnItemLongClickListener((p, v, pos, id) -> {
            if (pos < appts.size()) confirmDelete(appts.get(pos));
            return true;
        });
        root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        refresh();
    }

    void startAdd() {
        EditText t = new EditText(this);
        t.setHint("e.g. Counseling, Dr. Smith, group meeting");
        new AlertDialog.Builder(this)
                .setTitle("New appointment")
                .setView(t)
                .setPositiveButton("Next", (d, w) -> {
                    String name = t.getText().toString().trim();
                    if (name.isEmpty()) name = "Appointment";
                    pickDateTime(name);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void pickDateTime(String name) {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (v, y, m, d) -> {
            new TimePickerDialog(this, (tv, h, min) -> {
                Calendar c = Calendar.getInstance();
                c.set(y, m, d, h, min, 0);
                c.set(Calendar.MILLISECOND, 0);
                AppointmentDb.Appt a = db.add(name, c.getTimeInMillis());
                Reminders.schedule(this, a);
                refresh();
            }, 9, 0, false).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)).show();
    }

    void confirmDelete(AppointmentDb.Appt a) {
        new AlertDialog.Builder(this)
                .setMessage("Delete this appointment?")
                .setPositiveButton("Delete", (d, w) -> {
                    Reminders.cancel(this, a);
                    db.delete(a.id);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void refresh() {
        appts = db.upcoming();
        adapter.clear();
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        for (AppointmentDb.Appt a : appts) {
            adapter.add(f.format(new Date(a.time)) + "\n" + a.title);
        }
        if (appts.isEmpty()) adapter.add("No upcoming appointments yet.");
        adapter.notifyDataSetChanged();
    }
}
