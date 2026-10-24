package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLUE = 0xFF5AB4FF;

    CheckBox cOverview, cPlan, cCheckins, cAppts, cRoutine, cSavings;
    TextView preview;
    SharedPreferences rp, mp;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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

    CheckBox box(String label, boolean on) {
        CheckBox c = new CheckBox(this);
        c.setText(label);
        c.setTextColor(BLUE);
        c.setTextSize(16);
        c.setChecked(on);
        c.setButtonTintList(ColorStateList.valueOf(GREEN));
        c.setOnCheckedChangeListener((v, checked) -> refresh());
        return c;
    }

    Button btn(String label, Runnable r) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(BLUE);
        b.setBackgroundColor(0xFF0D1B2A);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(6), 0, dp(6));
        b.setLayoutParams(lp);
        b.setOnClickListener(v -> r.run());
        return b;
    }

    String dayKey(int daysAgo) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return fmt.format(c.getTime());
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
        scroll.setBackgroundColor(0xFF000000);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Counselor summary", 28, GREEN, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Pick what to include, check the preview, then share it however you like. "
                + "Your journal and support contacts are never included.", 14, BLUE, false));

        cOverview = box("Overview (days, milestones)", true);
        cPlan = box("My plan (reasons, go-to plan)", true);
        cCheckins = box("Check-ins, last 30 days", true);
        cAppts = box("Upcoming appointments", true);
        cRoutine = box("Daily structure", true);
        cSavings = box("Savings (money info)", false);
        root.addView(cOverview);
        root.addView(cPlan);
        root.addView(cCheckins);
        root.addView(cAppts);
        root.addView(cRoutine);
        root.addView(cSavings);

        root.addView(tv("Preview", 20, GREEN, true));
        preview = tv("", 15, BLUE, false);
        preview.setTextIsSelectable(true);
        root.addView(preview);

        root.addView(btn("Share summary", () -> share()));
        root.addView(btn("Copy to clipboard", () -> copy()));

        setContentView(scroll);
        refresh();
    }

    void refresh() {
        try {
            preview.setText(build());
        } catch (Exception e) {
            preview.setText("Couldn't build the summary.");
        }
    }

    void share() {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, "My recovery summary");
        send.putExtra(Intent.EXTRA_TEXT, preview.getText().toString());
        startActivity(Intent.createChooser(send, "Share summary"));
    }

    void copy() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("summary", preview.getText().toString()));
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    String build() {
        StringBuilder sb = new StringBuilder();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        sb.append("RECOVERY SUMMARY\nPrepared ").append(df.format(new Date()))
                .append("\nSelf-reported in RecoveryApp.\n");

        long d = days();

        if (cOverview.isChecked()) {
            sb.append("\nOVERVIEW\n");
            if (d < 0) {
                sb.append("Start date not set.\n");
            } else {
                sb.append("Days in recovery: ").append(d).append("\n");
                sb.append("Start date: ").append(df.format(new Date(rp.getLong("start", 0)))).append("\n");
                int last = 0, next = 0;
                for (int ms : MilestonesActivity.MILESTONES) {
                    if (d >= ms) last = ms;
                    else if (next == 0) next = ms;
                }
                if (last > 0) sb.append("Last milestone reached: day ").append(last).append("\n");
                if (next > 0) sb.append("Next milestone: day ").append(next)
                        .append(" (in ").append(next - d).append(" days)\n");
            }
        }

        if (cPlan.isChecked()) {
            sb.append("\nMY PLAN\n");
            String reasons = PlanStore.get(this, "reasons").trim();
            String go = PlanStore.get(this, "gotoPlan").trim();
            if (reasons.isEmpty() && go.isEmpty()) {
                sb.append("Nothing written yet.\n");
            } else {
                if (!reasons.isEmpty()) sb.append("Reasons I'm doing this:\n").append(reasons).append("\n");
                if (!go.isEmpty()) sb.append("\nWhen a craving hits:\n").append(go).append("\n");
            }
        }

        if (cCheckins.isChecked()) {
            sb.append("\nCHECK-INS (LAST 30 DAYS)\n");
            List<CheckinDb.Row> rows = new CheckinDb(this).since(dayKey(29));
            if (rows.isEmpty()) {
                sb.append("No check-ins in this period.\n");
            } else {
                double m = 0, u = 0, s = 0;
                for (CheckinDb.Row r : rows) {
                    m += r.mood;
                    u += r.urge;
                    s += r.sleep;
                }
                int n = rows.size();
                sb.append(String.format(Locale.US,
                        "Check-ins: %d of 30 days\nAverage mood: %.1f of 5\nAverage strongest urge: %.1f of 10\nAverage sleep: %.1f hrs\n",
                        n, m / n, u / n, s / n));
                StringBuilder hi = new StringBuilder();
                int shown = 0;
                for (int i = rows.size() - 1; i >= 0 && shown < 5; i--) {
                    if (rows.get(i).urge >= 7) {
                        hi.append("  ").append(rows.get(i).day).append(" (urge ").append(rows.get(i).urge).append(")\n");
                        shown++;
                    }
                }
                if (shown > 0) sb.append("Recent high-urge days (7+):\n").append(hi);
                StringBuilder wins = new StringBuilder();
                shown = 0;
                for (int i = rows.size() - 1; i >= 0 && shown < 5; i--) {
                    String w = rows.get(i).win;
                    if (w != null && !w.isEmpty()) {
                        wins.append("  ").append(rows.get(i).day).append(": ").append(w).append("\n");
                        shown++;
                    }
                }
                if (shown > 0) sb.append("Recent wins:\n").append(wins);
            }
        }

        if (cAppts.isChecked()) {
            sb.append("\nUPCOMING APPOINTMENTS\n");
            List<AppointmentDb.Appt> list = new AppointmentDb(this).upcoming();
            if (list.isEmpty()) {
                sb.append("None scheduled.\n");
            } else {
                DateFormat dt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
                for (AppointmentDb.Appt a : list) {
                    sb.append("  ").append(dt.format(new Date(a.time))).append(" - ").append(a.title).append("\n");
                }
            }
        }

        if (cRoutine.isChecked()) {
            sb.append("\nDAILY STRUCTURE\n");
            List<RoutineDb.Block> blocks = new RoutineDb(this).all();
            if (blocks.isEmpty()) {
                sb.append("Nothing planned yet.\n");
            } else {
                for (RoutineDb.Block bl : blocks) {
                    sb.append("  ").append(RoutineActivity.t(bl.start)).append(" to ")
                            .append(RoutineActivity.t(bl.end)).append(": ")
                            .append(bl.kind == 1 ? "HIGH-RISK WINDOW" : bl.title).append("\n");
                }
            }
        }

        if (cSavings.isChecked()) {
            sb.append("\nSAVINGS\n");
            double w = 0, g = 0;
            try { w = Double.parseDouble(mp.getString("weekly", "").trim()); } catch (Exception e) { }
            try { g = Double.parseDouble(mp.getString("goalAmt", "").trim()); } catch (Exception e) { }
            if (w <= 0 || d < 0) {
                sb.append("Not set up.\n");
            } else {
                double saved = d * w / 7.0;
                sb.append(String.format(Locale.US, "Estimated weekly spending avoided: $%,.2f\nEstimated saved so far: $%,.2f\n", w, saved));
                String gn = mp.getString("goalName", "").trim();
                if (g > 0) sb.append(String.format(Locale.US, "Goal%s: $%,.2f of $%,.2f\n",
                        gn.isEmpty() ? "" : " (" + gn + ")", saved, g));
            }
        }

        return sb.toString().trim();
    }
}
