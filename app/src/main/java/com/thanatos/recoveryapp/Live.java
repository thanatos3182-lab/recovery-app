package com.thanatos.recoveryapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.provider.Settings;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class Live {
    static boolean animationsOff(Context c) {
        return Settings.Global.getFloat(c.getContentResolver(),
                Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f;
    }

    static String label(long n) {
        return n + (n == 1 ? " day" : " days");
    }

    public static void countUp(final TextView t, final long days) {
        Object tag = t.getTag();
        long from = tag instanceof Long ? (Long) tag : 0L;
        t.setTag(days);
        if (animationsOff(t.getContext()) || from == days) {
            t.setText(label(days));
            return;
        }
        ValueAnimator va = ValueAnimator.ofInt((int) from, (int) days);
        va.setDuration(900);
        va.addUpdateListener(a -> t.setText(label((int) a.getAnimatedValue())));
        va.start();
    }

    public static void press(final View v) {
        v.setOnTouchListener((view, e) -> {
            int a = e.getActionMasked();
            if (a == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).start();
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            } else if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
    }
}
