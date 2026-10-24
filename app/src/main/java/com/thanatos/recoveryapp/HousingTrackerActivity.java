package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HousingTrackerActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLUE = 0xFF5AB4FF;
    static final int ORANGE = 0xFFFFA500;
    static final int GRAY = 0xFF888888;
    static final String[] STATUS = {"To contact", "Contacted", "Toured", "Applied", "Approved", "Not a fit"};
    static final String[] KINDS = {"Sober living", "Apartment", "Room for rent", "Other"};

    HousingDb db;
    LinearLayout listBox;
    TextView summary;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    TextView tv(String s, int sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(sp);
        v.setTextColor(color);
        if (bold) v.setTypeface(null, Typeface.BOLD);
        v.setPadding(0, dp(8), 0, dp(4));
        return v;
    }

    Button btn(String label, Runnable r) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(BLUE);
        b.setBackgroundColor(0xFF0D1B2A);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(4), 0, dp(4));
        b.setLayoutParams(lp);
        b.setOnClickListener(v -> r.run());
        return b;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        db = new HousingDb(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF000000);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Housing tracker", 28, GREEN, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Save places you're looking at and track each one. Tap a place for options. Long-press to delete.", 14, BLUE, false));

        root.addView(btn("Add place", () -> pickKind()));
        root.addView(btn("Find places (search links)", () ->
                startActivity(new Intent(this, HousingActivity.class))));

        summary = tv("", 18, GREEN, true);
        root.addView(summary);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        root.addView(listBox);

        setContentView(scroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    long dayStart(int offsetDays) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, offsetDays);
        return c.getTimeInMillis();
    }

    String dueText(HousingDb.Place p, long todayStart, long tomorrowStart) {
        if (p.follow <= 0) return "";
        DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if (p.follow < todayStart) return "Follow up: " + f.format(new Date(p.follow)) + " (overdue)";
        if (p.follow < tomorrowStart) return "Follow up: today";
        return "Follow up: " + f.format(new Date(p.follow));
    }

    void refresh() {
        List<HousingDb.Place> all = db.all();
        listBox.removeAllViews();
        long todayStart = dayStart(0);
        long tomorrowStart = dayStart(1);
        int dueCount = 0;

        for (final HousingDb.Place p : all) {
            int st = Math.max(0, Math.min(STATUS.length - 1, p.status));
            boolean open = st != 4 && st != 5;
            boolean isDue = open && p.follow > 0 && p.follow < tomorrowStart;
            if (isDue) dueCount++;

            StringBuilder s = new StringBuilder();
            s.append(STATUS[st]).append("  -  ").append(p.name).append("\n");
            StringBuilder meta = new StringBuilder(p.kind);
            if (!p.cost.isEmpty()) meta.append(" | ").append(p.cost);
            if (!p.phone.isEmpty()) meta.append(" | ").append(p.phone);
            s.append(meta);
            String due = dueText(p, todayStart, tomorrowStart);
            if (!due.isEmpty()) s.append("\n").append(due);
            if (!p.notes.isEmpty()) s.append("\n").append(p.notes);

            int color = st == 4 ? GREEN : st == 5 ? GRAY : isDue ? ORANGE : BLUE;
            TextView row = tv(s.toString(), 16, color, false);
            row.setPadding(dp(8), dp(12), dp(8), dp(12));
            row.setOnClickListener(v -> menu(p));
            row.setOnLongClickListener(v -> {
                confirmDelete(p);
                return true;
            });
            listBox.addView(row);
        }

        if (all.isEmpty()) {
            summary.setText("Nothing saved yet.");
        } else {
            summary.setText(all.size() + (all.size() == 1 ? " place" : " places")
                    + (dueCount > 0 ? "  |  " + dueCount + " follow-up" + (dueCount == 1 ? "" : "s") + " due" : ""));
        }
    }

    void pickKind() {
        new AlertDialog.Builder(this)
                .setTitle("What kind of place?")
                .setItems(KINDS, (d, i) -> {
                    HousingDb.Place p = new HousingDb.Place();
                    p.kind = KINDS[i];
                    form(p);
                })
                .show();
    }

    EditText field(String hint, String text, int type) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setText(text);
        e.setInputType(type);
        return e;
    }

    void form(final HousingDb.Place p) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(20), dp(8), dp(20), 0);
        final EditText name = field("Name (e.g. Oxford House, Main St)", p.name, InputType.TYPE_CLASS_TEXT);
        final EditText phone = field("Phone", p.phone, InputType.TYPE_CLASS_PHONE);
        final EditText cost = field("Cost (e.g. $150/week)", p.cost, InputType.TYPE_CLASS_TEXT);
        final EditText notes = field("Notes (requirements, waitlist, who you talked to)", p.notes,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        notes.setMinLines(2);
        box.addView(name);
        box.addView(phone);
        box.addView(cost);
        box.addView(notes);

        new AlertDialog.Builder(this)
                .setTitle(p.id == 0 ? "Add: " + p.kind : "Edit details")
                .setView(box)
                .setPositiveButton("Save", (d, w) -> {
                    String n = name.getText().toString().trim();
                    if (n.isEmpty()) {
                        Toast.makeText(this, "Add a name first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    p.name = n;
                    p.phone = phone.getText().toString().trim();
                    p.cost = cost.getText().toString().trim();
                    p.notes = notes.getText().toString().trim();
                    if (p.id == 0) db.add(p);
                    else db.update(p);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void menu(final HousingDb.Place p) {
        String[] items = {"Change status", "Set follow-up date", "Clear follow-up", "Call", "Edit details", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(p.name)
                .setItems(items, (d, i) -> {
                    if (i == 0) pickStatus(p);
                    else if (i == 1) pickFollow(p);
                    else if (i == 2) {
                        p.follow = 0;
                        db.update(p);
                        refresh();
                    } else if (i == 3) call(p);
                    else if (i == 4) form(p);
                    else confirmDelete(p);
                })
                .show();
    }

    void pickStatus(final HousingDb.Place p) {
        new AlertDialog.Builder(this)
                .setTitle("Status")
                .setItems(STATUS, (d, i) -> {
                    p.status = i;
                    db.update(p);
                    refresh();
                })
                .show();
    }

    void pickFollow(final HousingDb.Place p) {
        Calendar c = Calendar.getInstance();
        if (p.follow > 0) c.setTimeInMillis(p.follow);
        new DatePickerDialog(this, (view, y, m, day) -> {
            Calendar s = Calendar.getInstance();
            s.set(y, m, day, 0, 0, 0);
            s.set(Calendar.MILLISECOND, 0);
            p.follow = s.getTimeInMillis();
            db.update(p);
            refresh();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    void call(HousingDb.Place p) {
        if (p.phone.isEmpty()) {
            Toast.makeText(this, "No phone number saved", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(p.phone))));
    }

    void confirmDelete(final HousingDb.Place p) {
        new AlertDialog.Builder(this)
                .setMessage("Delete \"" + p.name + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.delete(p.id);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
