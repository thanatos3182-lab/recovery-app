package com.thanatos.recoveryapp;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InsightsActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLUE = 0xFF5AB4FF;
    static final String[] DAYS = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    static final int[] ORDER = {2, 3, 4, 5, 6, 7, 1};
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    TextView text(String s, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, dp(8), 0, dp(4));
        return t;
    }

    String dayKey(int daysAgo) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return fmt.format(c.getTime());
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF000000);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(24));
        scroll.addView(root);

        TextView title = text("Pattern insights", 28, GREEN, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(text("Built from your daily check-ins. These are patterns in your own data, not diagnoses.", 14, BLUE, false));

        try {
            build(root);
        } catch (Exception e) {
            root.addView(text("Couldn't read your check-ins.", 16, BLUE, false));
        }
        setContentView(scroll);
    }

    void section(LinearLayout root, String head, String body) {
        root.addView(text(head, 20, GREEN, true));
        TextView t = text(body, 16, BLUE, false);
        t.setPadding(0, 0, 0, dp(8));
        root.addView(t);
    }

    static double avg(List<CheckinDb.Row> l, char f) {
        if (l.isEmpty()) return 0;
        double s = 0;
        for (CheckinDb.Row r : l) s += f == 'm' ? r.mood : f == 'u' ? r.urge : r.sleep;
        return s / l.size();
    }

    static List<CheckinDb.Row> range(List<CheckinDb.Row> all, String from, String to) {
        List<CheckinDb.Row> out = new ArrayList<>();
        for (CheckinDb.Row r : all) {
            if (r.day.compareTo(from) >= 0 && r.day.compareTo(to) <= 0) out.add(r);
        }
        return out;
    }

    static String trend(double d, double eps) {
        return d > eps ? "higher" : d < -eps ? "lower" : "about the same";
    }

    void build(LinearLayout root) throws Exception {
        List<CheckinDb.Row> all = new CheckinDb(this).since(dayKey(89));
        String today = dayKey(0);

        if (all.size() < 7) {
            section(root, "Not enough data yet",
                    "You have " + all.size() + " check-in" + (all.size() == 1 ? "" : "s")
                    + " so far. Insights start showing at 7 and get more useful past 14. "
                    + "Check in most days and come back.");
            return;
        }

        Set<String> days = new HashSet<>();
        for (CheckinDb.Row r : all) days.add(r.day);
        int k = days.contains(today) ? 0 : 1;
        int current = 0;
        while (k < 90 && days.contains(dayKey(k))) {
            current++;
            k++;
        }
        int longest = 0, run = 0;
        for (int i = 89; i >= 0; i--) {
            if (days.contains(dayKey(i))) {
                run++;
                longest = Math.max(longest, run);
            } else {
                run = 0;
            }
        }
        section(root, "Streaks", "Current streak: " + current + " day" + (current == 1 ? "" : "s")
                + "\nLongest (last 90 days): " + longest + " day" + (longest == 1 ? "" : "s"));

        List<CheckinDb.Row> m30 = range(all, dayKey(29), today);
        if (!m30.isEmpty()) {
            int wins = 0;
            for (CheckinDb.Row r : m30) if (r.win != null && !r.win.isEmpty()) wins++;
            section(root, "Last 30 days", String.format(Locale.US,
                    "Check-ins: %d of 30 days\nAvg mood: %.1f of 5\nAvg urge: %.1f of 10\nAvg sleep: %.1f hrs\nWins logged: %d",
                    m30.size(), avg(m30, 'm'), avg(m30, 'u'), avg(m30, 's'), wins));
        }

        List<CheckinDb.Row> thisW = range(all, dayKey(6), today);
        List<CheckinDb.Row> lastW = range(all, dayKey(13), dayKey(7));
        if (thisW.size() >= 3 && lastW.size() >= 3) {
            double mt = avg(thisW, 'm'), ml = avg(lastW, 'm');
            double ut = avg(thisW, 'u'), ul = avg(lastW, 'u');
            section(root, "This week vs last week", String.format(Locale.US,
                    "Mood: %.1f vs %.1f (%s)\nUrge: %.1f vs %.1f (%s)",
                    mt, ml, trend(mt - ml, 0.3), ut, ul, trend(ut - ul, 0.5)));
        }

        if (all.size() < 14) {
            section(root, "More patterns coming",
                    "Weekday and sleep patterns unlock at 14 check-ins. You're at " + all.size() + ".");
            return;
        }

        int[] cnt = new int[8];
        double[] um = new double[8], mm = new double[8];
        for (CheckinDb.Row r : all) {
            Calendar c = Calendar.getInstance();
            c.setTime(fmt.parse(r.day));
            int d = c.get(Calendar.DAY_OF_WEEK);
            cnt[d]++;
            um[d] += r.urge;
            mm[d] += r.mood;
        }
        StringBuilder wk = new StringBuilder();
        int hard = -1, easy = -1;
        double hardU = -1, easyU = 99;
        for (int d : ORDER) {
            if (cnt[d] == 0) {
                wk.append(DAYS[d]).append("  no data\n");
                continue;
            }
            double u = um[d] / cnt[d], m = mm[d] / cnt[d];
            wk.append(String.format(Locale.US, "%s  mood %.1f  urge %.1f  (%d)\n", DAYS[d], m, u, cnt[d]));
            if (cnt[d] >= 2) {
                if (u > hardU) { hardU = u; hard = d; }
                if (u < easyU) { easyU = u; easy = d; }
            }
        }
        TextView table = text(wk.toString().trim(), 16, BLUE, false);
        table.setTypeface(Typeface.MONOSPACE);
        root.addView(text("By day of the week", 20, GREEN, true));
        root.addView(table);
        String sum;
        if (hard > 0 && easy > 0 && hard != easy && hardU - easyU >= 1.0) {
            sum = String.format(Locale.US,
                    "Your strongest urges tend to land on %ss (avg %.1f). Easiest: %ss (avg %.1f). "
                    + "Worth planning something solid for %ss: a meeting, a call, a set routine.",
                    DAYS[hard], hardU, DAYS[easy], easyU, DAYS[hard]);
        } else {
            sum = "No big difference between days yet.";
        }
        TextView st = text(sum, 16, BLUE, false);
        st.setPadding(0, 0, 0, dp(8));
        root.addView(st);

        List<CheckinDb.Row> shortS = new ArrayList<>();
        List<CheckinDb.Row> goodS = new ArrayList<>();
        for (CheckinDb.Row r : all) {
            if (r.sleep < 6) shortS.add(r);
            else if (r.sleep >= 7) goodS.add(r);
        }
        String sl;
        if (shortS.size() >= 3 && goodS.size() >= 3) {
            double us = avg(shortS, 'u'), ug = avg(goodS, 'u');
            double ms = avg(shortS, 'm'), mg = avg(goodS, 'm');
            sl = String.format(Locale.US,
                    "Under 6 hrs (%d days): urge %.1f, mood %.1f\n7+ hrs (%d days): urge %.1f, mood %.1f\n\n",
                    shortS.size(), us, ms, goodS.size(), ug, mg);
            if (us - ug >= 0.7 || mg - ms >= 0.4) {
                sl += "On short-sleep days your urges or mood ran worse. Sleep may be worth protecting.";
            } else {
                sl += "No clear link between sleep and urges or mood in your data yet.";
            }
            sl += "\nPatterns aren't proof of cause. Other things can move both at once.";
        } else {
            sl = "Needs at least 3 short-sleep days (under 6 hrs) and 3 good-sleep days (7+ hrs) to compare. "
                    + "You have " + shortS.size() + " and " + goodS.size() + ".";
        }
        section(root, "Sleep link", sl);

        section(root, "Reminder",
                "These numbers come only from what you log. If something here worries you, "
                + "bring it to your counselor or care team. In crisis, call or text 988.");
    }
}
