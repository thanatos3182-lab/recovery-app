package com.thanatos.recoveryapp;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyLetter {
    static final int GREEN = 0xFF00FF00;
    static final int BLUE = 0xFF5AB4FF;

    static final String[] THOUGHTS = {
        "You don't have to feel ready. You only have to start the next small thing.",
        "A slow day in recovery is still a day in recovery.",
        "Progress rarely announces itself. Look at where you were a month ago.",
        "Cravings are loud. Loud isn't the same as true.",
        "Asking for help is a skill, not a weakness.",
        "Today's job is small: get through today.",
        "You can feel the urge and still choose something else.",
        "Rest counts as part of the work.",
        "The you who kept going last week would be proud of today.",
        "A setback is a data point, not a verdict.",
        "Be as patient with yourself as you'd be with a friend.",
        "Small, boring routines build lives that hold.",
        "You're allowed to be proud of the days nobody else notices.",
        "What you practice grows. Pick what you want to grow.",
        "Hard feelings are weather. They move through.",
        "Done imperfectly still counts.",
        "Connection beats isolation, even when isolation feels easier.",
        "Quiet progress is still progress.",
        "You don't have to fix everything today. Just don't make it harder.",
        "Today is a good day to be kind to the person you're becoming."
    };

    static final String[][] FACTS = {
        {"Cravings often rise, peak, and fade within about 20 to 30 minutes if you don't act on them.", "Craving Neurobiology"},
        {"Putting a feeling into specific words has been linked to calmer amygdala activity.", "Emotion Labeling"},
        {"Your brain strengthens whatever you practice, whether it's a habit you want or one you don't.", "Neuroplasticity"},
        {"Breathing out longer than you breathe in nudges your body toward its rest-and-digest state.", "Body Awareness"},
        {"One night of poor sleep can make emotional reactions stronger and self-control weaker.", "Sleep and Exercise"},
        {"Dopamine is more about wanting than liking, which is why a craving can show up even when the thing no longer feels good.", "Dopamine and Reward"},
        {"Old learning isn't erased. New learning builds a competing memory, which is why practice beats willpower.", "Fear Extinction"},
        {"Stress can set off cravings with no obvious trigger. On hard days, treat the stress first.", "Stress and the HPA Axis"},
        {"In imaging studies, dopamine function and brain structure partly recover with sustained abstinence over months to years.", "Addiction & The Brain"},
        {"Reappraising a situation is linked to lower amygdala activity, so a new interpretation can change the reaction itself.", "Cognitive Reframing"},
        {"Replaying problems is your default mode network on autopilot. Noticing it is the first step out.", "Rumination and the Default Mode Network"},
        {"Your window of tolerance is the zone where you can think and feel at the same time. It can widen with practice.", "Window of Tolerance"},
        {"A craving often starts as a body sensation before it becomes a thought: a tight chest, restless hands.", "Interoception"},
        {"Waiting 10 minutes before acting gives an urge time to weaken and your prefrontal brakes time to catch up.", "Impulse Control"}
    };

    static final String[] CHALLENGES = {
        "Text one person just to say hi.",
        "Take a 10-minute walk with no phone.",
        "Drink a full glass of water before your next meal.",
        "Write down one thing you did well today.",
        "Name your feeling in three specific words.",
        "Do 2 minutes of slow breathing: in for 4, out for 6.",
        "Tidy one small spot: a drawer, a desk, a shelf.",
        "Plan tomorrow's hardest hour.",
        "Go to bed 30 minutes earlier tonight.",
        "Do something kind for someone without mentioning it.",
        "Read one lesson in the Brain section.",
        "Step outside for 5 minutes of daylight.",
        "Eat something with protein within an hour of waking.",
        "Call or text someone from your support list."
    };

    static int dp(Activity a, int v) {
        return Math.round(v * a.getResources().getDisplayMetrics().density);
    }

    static TextView tv(Activity a, String s, int sp, int color, boolean bold) {
        TextView t = new TextView(a);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, dp(a, 6), 0, dp(a, 2));
        return t;
    }

    public static LinearLayout create(final Activity a) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.YEAR) * 366 + c.get(Calendar.DAY_OF_YEAR);
        String thought = THOUGHTS[day % THOUGHTS.length];
        final String[] fact = FACTS[day % FACTS.length];
        String challenge = CHALLENGES[(day + 3) % CHALLENGES.length];

        LinearLayout card = new LinearLayout(a);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable g = new GradientDrawable();
        g.setColor(0xFF000000);
        g.setStroke(dp(a, 1), GREEN);
        g.setCornerRadius(dp(a, 12));
        card.setBackground(g);
        card.setPadding(dp(a, 16), dp(a, 12), dp(a, 16), dp(a, 16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(a, 16), 0, dp(a, 16));
        card.setLayoutParams(lp);

        card.addView(tv(a, "Today's letter", 22, GREEN, true));
        card.addView(tv(a, DateFormat.getDateInstance(DateFormat.LONG).format(new Date()), 14, BLUE, false));

        card.addView(tv(a, "Thought for today", 16, GREEN, true));
        card.addView(tv(a, thought, 16, BLUE, false));

        card.addView(tv(a, "Did you know?", 16, GREEN, true));
        card.addView(tv(a, fact[0], 16, BLUE, false));
        Button more = new Button(a);
        more.setText("Read the lesson: " + fact[1]);
        more.setAllCaps(false);
        more.setTextSize(13);
        more.setTextColor(BLUE);
        more.setBackgroundColor(0xFF0D1B2A);
        more.setOnClickListener(v -> InfoActivity.open(a, fact[1]));
        card.addView(more);

        card.addView(tv(a, "Small challenge", 16, GREEN, true));
        card.addView(tv(a, challenge, 16, BLUE, false));

        card.addView(tv(a, "New every day. Stored in the app. Nothing is sent or tracked.", 12, 0xFF888888, false));
        return card;
    }
}
