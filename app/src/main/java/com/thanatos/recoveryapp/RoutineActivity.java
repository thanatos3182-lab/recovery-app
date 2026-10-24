package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RoutineActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLUE = 0xFF5AB4FF;
    static final int ORANGE = 0xFFFFA500;

    interface IntCb { void run(int v); }

    RoutineDb db;
    TextView summary, gapsView;
    LinearLayout listBox;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    static String t(int m) {
        int h = m / 60;
        return String.format(Locale.US, "%d:%02d %s", (h % 12 == 0 ? 12 : h % 12), m % 60, h < 12 ? "AM" : "PM");
    }

    String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
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
        db = new RoutineDb(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF000000);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Daily structure", 28, GREEN, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Plan your day, mark the hours that are hardest, and fill them before they arrive. "
                + "Tap an activity to check it off today. Long-press to delete.", 14, BLUE, false));

        root.addView(btn("Add activity", () -> addActivity()));
        root.addView(btn("Add high-risk window", () -> addRisk()));

        summary = tv("", 18, GREEN, true);
        root.addView(summary);
        gapsView = tv("", 15, ORANGE, false);
        root.addView(gapsView);

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

    void pickTime(String label, int defMin, IntCb cb) {
        TimePickerDialog d = new TimePickerDialog(this, (view, h, m) -> cb.run(h * 60 + m),
                defMin / 60, defMin % 60, false);
        d.setTitle(label);
        d.show();
    }

    void addActivity() {
        EditText name = new EditText(this);
        name.setHint("e.g. Meeting, work, walk, call sponsor");
        new AlertDialog.Builder(this)
                .setTitle("New activity")
                .setView(name)
                .setPositiveButton("Next", (d, w) -> {
                    String n = name.getText().toString().trim();
                    if (n.isEmpty()) n = "Activity";
                    final String title = n;
                    pickTime("Start time", 9 * 60, s ->
                            pickTime("End time", Math.min(s + 60, 1439), e -> save(0, title, s, e)));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void addRisk() {
        pickTime("High-risk window starts", 21 * 60, s ->
                pickTime("High-risk window ends", Math.min(s + 120, 1439), e ->
                        save(1, "High-risk window", s, e)));
    }

    void save(int kind, String title, int s, int e) {
        if (e <= s) {
            Toast.makeText(this, "End must be after start. For past midnight, add two entries.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        db.add(kind, title, s, e);
        refresh();
    }

    void confirmDelete(RoutineDb.Block bl) {
        new AlertDialog.Builder(this)
                .setMessage("Delete \"" + bl.title + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.delete(bl.id);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    String findGaps(List<RoutineDb.Block> all) {
        StringBuilder sb = new StringBuilder();
        for (RoutineDb.Block r : all) {
            if (r.kind != 1) continue;
            List<int[]> cov = new ArrayList<>();
            for (RoutineDb.Block a : all) {
                if (a.kind != 0) continue;
                int s = Math.max(a.start, r.start);
                int e = Math.min(a.end, r.end);
                if (s < e) cov.add(new int[]{s, e});
            }
            Collections.sort(cov, (x, y) -> x[0] - y[0]);
            int cur = r.start;
            for (int[] c : cov) {
                if (c[0] - cur >= 30) sb.append("\u26A0 Nothing planned ").append(t(cur)).append(" to ").append(t(c[0])).append("\n");
                cur = Math.max(cur, c[1]);
            }
            if (r.end - cur >= 30) sb.append("\u26A0 Nothing planned ").append(t(cur)).append(" to ").append(t(r.end)).append("\n");
        }
        return sb.toString().trim();
    }

    void refresh() {
        List<RoutineDb.Block> all = db.all();
        Set<Long> done = db.doneOn(today());
        listBox.removeAllViews();

        int acts = 0, doneN = 0, risks = 0;
        for (final RoutineDb.Block bl : all) {
            String line;
            int color;
            if (bl.kind == 1) {
                risks++;
                line = "\u26A0  " + t(bl.start) + " to " + t(bl.end) + "\nHigh-risk window";
                color = ORANGE;
            } else {
                acts++;
                boolean d = done.contains(bl.id);
                if (d) doneN++;
                line = (d ? "\u2611  " : "\u2610  ") + t(bl.start) + " to " + t(bl.end) + "\n" + bl.title;
                color = d ? GREEN : BLUE;
            }
            TextView row = tv(line, 16, color, false);
            row.setPadding(dp(8), dp(12), dp(8), dp(12));
            if (bl.kind == 0) {
                row.setOnClickListener(v -> {
                    db.toggleDone(bl.id, today());
                    refresh();
                });
            }
            row.setOnLongClickListener(v -> {
                confirmDelete(bl);
                return true;
            });
            listBox.addView(row);
        }

        if (all.isEmpty()) {
            summary.setText("Nothing planned yet.");
            gapsView.setText("Start with your hardest hours: tap \"Add high-risk window\", then fill them with activities.");
            return;
        }
        summary.setText(acts == 0 ? "No activities yet" : "Done today: " + doneN + " of " + acts);
        if (risks == 0) {
            gapsView.setText("Tip: add a high-risk window for the hours that are hardest, and I'll flag any empty stretch inside it.");
        } else {
            String g = findGaps(all);
            gapsView.setText(g.isEmpty()
                    ? "Your high-risk windows are covered."
                    : g + "\nIdeas: a meeting, a call, a walk, a shower and an early bed, or something absorbing like cooking or a puzzle.");
        }
    }
}
