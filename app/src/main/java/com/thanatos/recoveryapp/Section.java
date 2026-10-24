package com.thanatos.recoveryapp;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Section {
    static int dp(Activity a, int v) {
        return Math.round(v * a.getResources().getDisplayMetrics().density);
    }

    public static LinearLayout create(Activity a, String title) {
        LinearLayout box = new LinearLayout(a);
        box.setOrientation(LinearLayout.VERTICAL);

        Button header = new Button(a);
        header.setText("\u25B8  " + title);
        header.setAllCaps(false);
        header.setTextSize(22);
        header.setTypeface(null, Typeface.BOLD);
        header.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        header.setBackgroundColor(0xFF0A2A0A);
        header.setTextColor(0xFF00FF00);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hp.setMargins(0, dp(a, 10), 0, dp(a, 4));
        header.setLayoutParams(hp);
        Live.press(header);

        LinearLayout content = new LinearLayout(a);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setVisibility(View.GONE);

        header.setOnClickListener(v -> {
            boolean open = content.getVisibility() == View.VISIBLE;
            content.setVisibility(open ? View.GONE : View.VISIBLE);
            if (!open) { content.setAlpha(0f); content.animate().alpha(1f).setDuration(220).start(); }
            header.setText((open ? "\u25B8  " : "\u25BE  ") + title);
        });

        box.addView(header);
        box.addView(content);
        return box;
    }

    public static void addItem(Activity a, LinearLayout section, String text, Runnable action) {
        LinearLayout content = (LinearLayout) section.getChildAt(1);

        LinearLayout row;
        int n = content.getChildCount();
        if (n == 0 || ((LinearLayout) content.getChildAt(n - 1)).getChildCount() >= 2) {
            row = new LinearLayout(a);
            row.setOrientation(LinearLayout.HORIZONTAL);
            content.addView(row);
        } else {
            row = (LinearLayout) content.getChildAt(n - 1);
        }

        Button b = new Button(a);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setMinHeight(dp(a, 64));
        b.setTextColor(0xFF5AB4FF);
        b.setBackgroundColor(0xFF0D1B2A);
        b.setOnClickListener(v -> action.run());
        Live.press(b);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(a, 4), dp(a, 4), dp(a, 4), dp(a, 4));
        b.setLayoutParams(lp);
        row.addView(b);
    }
}
