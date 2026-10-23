package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JobTrackerActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;

    JobTrackerDb db;
    TextView summary;
    ArrayAdapter<String> adapter;
    List<JobTrackerDb.App> apps = new ArrayList<>();
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

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

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        db = new JobTrackerDb(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BLACK);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView title = tv("Job tracker", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        summary = tv("", 16);
        root.addView(summary);

        root.addView(ContactsStore.margin(
                ContactsStore.styled(this, "Add application", v -> startAdd())));
        root.addView(tv("Tap an application to update its status.", 14));

        ListView lv = new ListView(this);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView t = (TextView) super.getView(position, convertView, parent);
                t.setTextColor(GREEN);
                return t;
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((p, v, pos, id) -> {
            if (pos < apps.size()) showActions(apps.get(pos));
        });
        root.addView(lv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        refresh();
    }

    void startAdd() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), 0);
        EditText c = new EditText(this);
        c.setHint("Company, e.g. FedEx");
        EditText r = new EditText(this);
        r.setHint("Role, e.g. package handler");
        EditText n = new EditText(this);
        n.setHint("Notes (optional): contact, how you applied");
        form.addView(c);
        form.addView(r);
        form.addView(n);
        new AlertDialog.Builder(this)
                .setTitle("Add application")
                .setView(form)
                .setPositiveButton("Next", (d, w) -> {
                    String company = c.getText().toString().trim();
                    if (company.isEmpty()) company = "Application";
                    pickDate(company, r.getText().toString().trim(),
                            n.getText().toString().trim());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void pickDate(String company, String role, String notes) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar c = Calendar.getInstance();
            c.set(y, m, d, 10, 0, 0);
            c.set(Calendar.MILLISECOND, 0);
            long applied = c.getTimeInMillis();

            Calendar f = (Calendar) c.clone();
            f.add(Calendar.DAY_OF_YEAR, 7);
            if (f.getTimeInMillis() <= System.currentTimeMillis()) {
                f = Calendar.getInstance();
                f.add(Calendar.DAY_OF_YEAR, 1);
                f.set(Calendar.HOUR_OF_DAY, 10);
                f.set(Calendar.MINUTE, 0);
                f.set(Calendar.SECOND, 0);
                f.set(Calendar.MILLISECOND, 0);
            }
            JobTrackerDb.App a = db.add(company, role, applied, notes, f.getTimeInMillis());
            JobTrackerDb.schedule(this, a);
            refresh();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dlg.setTitle("Date you applied");
        dlg.getDatePicker().setMaxDate(System.currentTimeMillis());
        dlg.show();
    }

    void showActions(JobTrackerDb.App a) {
        String[] opts = {"Mark: Applied", "Mark: Interviewing", "Mark: Offer",
                "Mark: Not selected", "Mark: Withdrew", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(a.company)
                .setItems(opts, (d, which) -> {
                    if (which == 5) {
                        confirmDelete(a);
                        return;
                    }
                    String s = JobTrackerDb.STATUSES[which];
                    JobTrackerDb.cancel(this, a);
                    db.setStatus(a.id, s);
                    a.status = s;
                    JobTrackerDb.schedule(this, a);
                    refresh();
                })
                .show();
    }

    void confirmDelete(JobTrackerDb.App a) {
        new AlertDialog.Builder(this)
                .setMessage("Delete " + a.company + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    JobTrackerDb.cancel(this, a);
                    db.delete(a.id);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void refresh() {
        apps = db.all();
        adapter.clear();
        int active = 0, interviewing = 0, offers = 0;
        long now = System.currentTimeMillis();
        for (JobTrackerDb.App a : apps) {
            if (JobTrackerDb.active(a.status)) active++;
            if ("Interviewing".equals(a.status)) interviewing++;
            if ("Offer".equals(a.status)) offers++;
            StringBuilder sb = new StringBuilder();
            sb.append(a.company);
            if (a.role != null && !a.role.isEmpty()) sb.append(" - ").append(a.role);
            sb.append("\nApplied ").append(df.format(new Date(a.applied)));
            if (JobTrackerDb.active(a.status) && a.followup > now) {
                sb.append("  |  Follow up ").append(df.format(new Date(a.followup)));
            }
            sb.append("\nStatus: ").append(a.status);
            if (a.notes != null && !a.notes.isEmpty()) sb.append("\n").append(a.notes);
            adapter.add(sb.toString());
        }
        if (apps.isEmpty()) adapter.add("No applications yet. Tap Add application.");
        summary.setText(apps.size() + " applications  |  " + active + " waiting  |  "
                + interviewing + " interviewing  |  " + offers + " offers");
        adapter.notifyDataSetChanged();
    }
}
