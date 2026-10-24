package com.thanatos.recoveryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Toast;
import java.util.Calendar;

public class ButterflyView extends View {
    static final int GREEN = 0xFF00FF00;

    Path upper = new Path(), lower = new Path(), body = new Path(), antennae = new Path();
    Paint wingUp = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint wingLow = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint bodyP = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint ant = new Paint(Paint.ANTI_ALIAS_FLAG);
    long flutterStart = 0, flutterUntil = 0;
    boolean big = false;
    boolean still;
    public Runnable onTap;

    public ButterflyView(Context c) {
        super(c);
        upper.moveTo(54, 50);
        upper.cubicTo(44, 28, 22, 26, 24, 42);
        upper.cubicTo(25, 52, 40, 56, 54, 54);
        upper.close();
        lower.moveTo(54, 56);
        lower.cubicTo(40, 58, 28, 62, 32, 74);
        lower.cubicTo(36, 84, 50, 74, 54, 60);
        lower.close();
        body.addRoundRect(52.5f, 44f, 55.5f, 72f, 1.5f, 1.5f, Path.Direction.CW);
        antennae.moveTo(54, 45);
        antennae.cubicTo(52, 38, 49, 34, 46, 32);
        antennae.moveTo(54, 45);
        antennae.cubicTo(56, 38, 59, 34, 62, 32);

        wingUp.setStyle(Paint.Style.FILL);
        wingUp.setColor(GREEN);
        wingLow.setStyle(Paint.Style.FILL);
        wingLow.setColor(0xFF00C800);
        glow.setStyle(Paint.Style.FILL);
        glow.setColor(GREEN);
        bodyP.setColor(0xFF007700);
        ant.setStyle(Paint.Style.STROKE);
        ant.setColor(GREEN);
        ant.setStrokeWidth(1.5f);
        ant.setStrokeCap(Paint.Cap.ROUND);

        still = Settings.Global.getFloat(c.getContentResolver(),
                Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f;
        setContentDescription("Butterfly. Tap for a message.");
        setOnClickListener(v -> {
            flutter(1600, false);
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (onTap != null) onTap.run();
        });
    }

    void flutter(long ms, boolean celebrate) {
        flutterStart = SystemClock.uptimeMillis();
        flutterUntil = flutterStart + ms;
        big = celebrate;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SharedPreferences rp = getContext().getSharedPreferences("recovery", Context.MODE_PRIVATE);
        if (!rp.contains("start")) return;
        Calendar t = Calendar.getInstance();
        t.set(Calendar.HOUR_OF_DAY, 0);
        t.set(Calendar.MINUTE, 0);
        t.set(Calendar.SECOND, 0);
        t.set(Calendar.MILLISECOND, 0);
        long days = Math.max(0, Math.round((t.getTimeInMillis() - rp.getLong("start", 0)) / 86400000.0));
        SharedPreferences lp = getContext().getSharedPreferences("live", Context.MODE_PRIVATE);
        for (int ms : MilestonesActivity.MILESTONES) {
            if (days == ms && lp.getLong("celebrated", -1) != days) {
                lp.edit().putLong("celebrated", days).apply();
                flutter(3500, true);
                Toast.makeText(getContext(), "Day " + days + ". You earned this one.",
                        Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    static float bump(float x, float c, float w) {
        float d = (x - c) / w;
        return (float) Math.exp(-d * d);
    }

    void wings(Canvas c, float f, boolean mirror) {
        c.save();
        if (mirror) c.scale(-1f, 1f, 54f, 54f);
        c.scale(f, 1f, 54f, 54f);
        c.drawPath(upper, wingUp);
        c.drawPath(lower, wingLow);
        c.restore();
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        long now = SystemClock.uptimeMillis();
        float w = getWidth(), h = getHeight();
        float s = Math.min(w, h) / 108f;
        boolean fl = now < flutterUntil;

        // heartbeat: a "lub-dub" every 1.1 seconds
        float ph = (now % 1100L) / 1100f;
        float pulse = still ? 0f : bump(ph, 0.12f, 0.05f) + 0.7f * bump(ph, 0.38f, 0.06f);
        if (big && fl) pulse *= 1.6f;

        float flap;
        float bob = 0f;
        if (still) {
            flap = 1f;
        } else if (fl) {
            float k = (now - flutterStart) / 1000f;
            flap = 0.65f + 0.35f * (float) Math.cos(2 * Math.PI * 7f * k);
            float p = (now - flutterStart) / (float) (flutterUntil - flutterStart);
            bob = -6f * (float) Math.sin(Math.PI * p);
        } else {
            flap = 0.88f + 0.12f * (float) Math.cos(2 * Math.PI * (now % 3600L) / 3600.0);
        }
        if (!still) bob += 2.5f * (float) Math.sin(2 * Math.PI * (now % 4200L) / 4200.0);

        c.save();
        c.translate((w - 108f * s) / 2f, (h - 108f * s) / 2f);
        c.scale(s, s);

        int a1 = (int) Math.min(255, 18 + 70 * pulse);
        glow.setAlpha(a1);
        c.drawCircle(54, 56, 38 + 8 * pulse, glow);
        glow.setAlpha((int) (a1 * 0.5f));
        c.drawCircle(54, 56, 50 + 6 * pulse, glow);

        float ps = 1f + 0.05f * pulse;
        c.translate(0, bob);
        c.scale(ps, ps, 54f, 56f);
        wings(c, flap, false);
        wings(c, flap, true);
        c.drawPath(body, bodyP);
        c.drawPath(antennae, ant);
        c.restore();

        if (!still) postInvalidateOnAnimation();
    }
}
