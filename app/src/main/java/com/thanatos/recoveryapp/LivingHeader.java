package com.thanatos.recoveryapp;

import android.app.Activity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;

public class LivingHeader {
    static final String[] TAPS = {
        "Still here. Still going.",
        "One breath at a time.",
        "You showed up. That counts.",
        "Steady heart, steady steps.",
        "Small things add up.",
        "Rest if you need to. Then keep going.",
        "Be gentle with yourself today.",
        "Look how far you've come."
    };

    static int dp(Activity a, int v) {
        return Math.round(v * a.getResources().getDisplayMetrics().density);
    }

    static String greeting() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h < 5 || h >= 22) return "Still up? Be gentle with yourself tonight.";
        if (h < 12) return "Good morning. One day at a time.";
        if (h < 17) return "Afternoon check: water, food, a deep breath.";
        return "Evening. Another day, handled.";
    }

    public static LinearLayout create(final Activity a) {
        LinearLayout box = new LinearLayout(a);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_HORIZONTAL);

        final ButterflyView bv = new ButterflyView(a);
        box.addView(bv, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(a, 130)));

        final TextView cap = new TextView(a);
        cap.setText(greeting());
        cap.setTextSize(15);
        cap.setTextColor(0xFF00FF00);
        cap.setGravity(Gravity.CENTER);
        cap.setPadding(0, 0, 0, dp(a, 8));
        box.addView(cap);

        final int[] i = {0};
        bv.onTap = () -> cap.animate().alpha(0f).setDuration(120).withEndAction(() -> {
            cap.setText(TAPS[i[0]++ % TAPS.length]);
            cap.animate().alpha(1f).setDuration(200).start();
        }).start();

        return box;
    }
}
