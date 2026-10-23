package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.function.IntConsumer;

public class CravingSosActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;

    LinearLayout content;
    CountDownTimer timer;
    int urgeStart, urgeEnd;
    String feeling = "Other";
    String skill = "";
    String note = "";
    boolean slip = false;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BLACK);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(btn("Need a person now? Call or text 988", v -> dial("988")));

        setContentView(root);
        showRate("You reached out, and that's already a win. Let's ride this out together.\n\nHow strong is the urge right now?",
                v -> { urgeStart = v; PlanStore.peek(this, v, () -> showFeeling()); });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    // ---------- helpers ----------

    TextView tv(String text, int sp) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(sp);
        t.setTextColor(GREEN);
        t.setPadding(0, dp(8), 0, dp(8));
        return t;
    }

    Button btn(String text, View.OnClickListener l) {
        Button x = new Button(this);
        x.setText(text);
        x.setAllCaps(false);
        x.setTextColor(GREEN);
        GradientDrawable g = new GradientDrawable();
        g.setColor(BLACK);
        g.setStroke(dp(1), GREEN);
        g.setCornerRadius(dp(8));
        x.setBackground(g);
        x.setOnClickListener(l);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(8), dp(6), dp(8), dp(6));
        x.setLayoutParams(lp);
        return x;
    }

    void clear() {
        content.removeAllViews();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    void dial(String number) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }

    // ---------- steps ----------

    void showRate(String prompt, IntConsumer done) {
        clear();
        content.addView(tv("Craving SOS", 28));
        content.addView(tv(prompt, 18));
        TextView val = tv("5", 48);
        val.setGravity(Gravity.CENTER);
        content.addView(val);
        SeekBar bar = new SeekBar(this);
        bar.setMax(10);
        bar.setProgress(5);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) {
                val.setText(String.valueOf(p));
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        content.addView(bar);
        content.addView(tv("0 = none          10 = intense", 14));
        content.addView(btn("Next", v -> done.accept(bar.getProgress())));
    }

    void showFeeling() {
        clear();
        content.addView(tv("What's going on?", 24));
        content.addView(tv("Tap what fits best, or tell me in your own words.", 16));
        for (String f : CoachContent.FEELINGS) {
            content.addView(btn(f, v -> { feeling = f; showReply(); }));
        }
        EditText et = new EditText(this);
        et.setHint("Type here (stays on your phone)");
        et.setTextColor(GREEN);
        et.setHintTextColor(0xFF008800);
        content.addView(et);
        content.addView(btn("Send", v -> {
            String t = et.getText().toString().trim();
            if (t.isEmpty()) return;
            if (CoachContent.isCrisis(t)) {
                showCrisis();
                return;
            }
            note = t;
            String m = CoachContent.matchFeeling(t);
            if (m != null) feeling = m;
            slip = CoachContent.isSlip(t);
            showReply();
        }));
    }

    void showCrisis() {
        new AlertDialog.Builder(this)
                .setTitle("Please talk to a person now")
                .setMessage("It sounds like you may be in serious distress. You matter, and help is free and available right now.\n\n"
                        + "Call or text 988 (Suicide & Crisis Lifeline, 24/7).\n\n"
                        + "If you may have taken too much of something, call 911 or Poison Control at 1-800-222-1222 right away.\n\n"
                        + "If you are in immediate danger, call 911.")
                .setPositiveButton("Call 988", (d, w) -> dial("988"))
                .setNegativeButton("Close", null)
                .show();
    }

    void showReply() {
        clear();
        content.addView(tv(CoachContent.reply(feeling), 18));
        if (slip) {
            content.addView(tv(CoachContent.SLIP, 16));
            content.addView(btn("Call SAMHSA helpline (free, 24/7)", v -> dial("18006624357")));
        }
        content.addView(tv("Pick one to try right now:", 18));
        for (String s : CoachContent.skillsFor(feeling)) {
            content.addView(btn(s, v -> startSkill(s)));
        }
        content.addView(btn("Show all skills", v -> showAllSkills()));
    }

    void showAllSkills() {
        clear();
        content.addView(tv("Pick a skill", 24));
        for (String s : CoachContent.ALL_SKILLS) {
            content.addView(btn(s, v -> startSkill(s)));
        }
    }

    void startSkill(String s) {
        skill = s;
        walk(0);
    }

    void walk(int i) {
        String[] steps = CoachContent.steps(skill);
        clear();
        content.addView(tv(skill, 24));
        content.addView(tv("Step " + (i + 1) + " of " + steps.length, 14));
        content.addView(tv(steps[i], 20));
        if (skill.equals("Reach out") && i == 1) ContactsStore.addButtons(this, content);
        boolean last = i == steps.length - 1;
        content.addView(btn(last ? "Done. Start the 10-minute wait" : "Next", v -> {
            if (last) showTimer(); else walk(i + 1);
        }));
        if (i > 0) content.addView(btn("Back", v -> walk(i - 1)));
    }

    void showTimer() {
        clear();
        content.addView(tv("Ride the wave", 24));
        content.addView(tv("Give it 10 minutes. Urges usually rise, peak, and fade on their own. You don't have to do anything but wait. Put the phone down if you can, or keep breathing slowly.", 18));
        TextView clock = tv("10:00", 56);
        clock.setGravity(Gravity.CENTER);
        content.addView(clock);
        content.addView(btn("Try another skill", v -> showAllSkills()));
        content.addView(btn("I'm ready to check in", v -> showReRate()));
        final long end = System.currentTimeMillis() + 10 * 60 * 1000L;
        timer = new CountDownTimer(10 * 60 * 1000L, 1000) {
            public void onTick(long ms) {
                long left = Math.max(0, end - System.currentTimeMillis()) / 1000;
                clock.setText(String.format("%d:%02d", left / 60, left % 60));
            }
            public void onFinish() {
                showReRate();
            }
        }.start();
    }

    void showReRate() {
        showRate("Check in. How strong is the urge now?", v -> { urgeEnd = v; showResult(); });
    }

    void showResult() {
        clear();
        save();
        content.addView(tv("Urge: " + urgeStart + " to " + urgeEnd, 28));
        if (urgeEnd < urgeStart || urgeEnd <= 2) {
            content.addView(tv("It dropped. You just showed yourself that cravings pass, and every time you ride one out, the pattern gets weaker. That counts.", 18));
        } else {
            content.addView(tv("It's still strong, and that's okay. That doesn't mean you failed or that the skill didn't work. Some waves take longer. You have options:", 18));
            content.addView(btn("Try another skill", v -> { urgeStart = urgeEnd; showAllSkills(); }));
            content.addView(btn("Reach out to someone", v -> { urgeStart = urgeEnd; startSkill("Reach out"); }));
            content.addView(btn("Call or text 988", v -> dial("988")));
            content.addView(btn("SAMHSA helpline (free, 24/7)", v -> dial("18006624357")));
        }
        content.addView(btn("Done", v -> finish()));
    }

    void save() {
        String s = "Craving SOS: urge " + urgeStart + " to " + urgeEnd
                + " (" + feeling + "), skill: " + skill;
        if (!note.isEmpty()) s += ". Note: " + note;
        new JournalDb(this).add(s);
    }
}
