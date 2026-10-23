package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SkillsActivity extends Activity {
    static final String[][] SKILLS = {
        {"Urge surfing",
         "Cravings rise, peak, and fall like a wave. They often pass within 20 to 30 minutes, even if you do nothing.\n\nHow:\n1. Notice the urge and say: \"this is a craving.\"\n2. Find where you feel it in your body.\n3. Breathe slowly and watch it rise, crest, and fade.\n4. Don't fight it and don't act on it.\n\nWhy it works: Cravings run on brain circuits that learn to expect reward from certain cues. Riding out an urge without acting on it weakens the link between the cue and the response over time."},
        {"Slow breathing",
         "Breathe in through your nose for 4 seconds, out through your mouth for 6 to 8 seconds. Repeat for 2 to 5 minutes.\n\nWhy it works: Longer exhales engage the parasympathetic (\"rest and digest\") side of your nervous system, which slows your heart rate and eases the body's stress response. A calmer body makes it easier for the thinking part of your brain to take the lead."},
        {"5-4-3-2-1 grounding",
         "Name 5 things you can see, 4 you can touch, 3 you can hear, 2 you can smell, and 1 you can taste.\n\nWhy it works: Focusing on sensory input pulls attention out of racing thoughts and into the present moment, which interrupts the loop of craving and rumination."},
        {"Name it to tame it",
         "Say or write exactly what you feel, as specifically as you can: \"I feel anxious and ashamed and I want to numb out.\"\n\nWhy it works: Putting feelings into words (called affect labeling) has been linked to calmer activity in the amygdala, the brain's alarm center, and more activity in prefrontal areas that help regulate emotion."},
        {"Play the tape forward",
         "Picture using, then keep the movie going. How will you feel an hour later? Tomorrow morning? Next week? Who will you have to tell?\n\nWhy it works: Cravings zoom in on the immediate reward. Deliberately imagining the whole sequence engages your prefrontal cortex, the planning and consequences part of the brain, which cravings tend to quiet down."},
        {"HALT check",
         "Ask yourself: am I Hungry, Angry, Lonely, or Tired? Fix the basic need first. Eat something, rest, or call someone.\n\nWhy it works: Hunger, poor sleep, stress, and isolation all make cravings and emotions feel stronger and self-control harder. Meeting the need takes pressure off your brain."},
        {"Move your body",
         "Walk, stretch, or do push-ups for 5 to 10 minutes. Changing your location helps too.\n\nWhy it works: Physical activity shifts your body and brain state quickly, and some studies show short bouts of exercise can reduce cravings. It also changes your surroundings, which moves you away from the cue that set off the urge."},
        {"Reach out",
         "Text or call someone safe before the urge peaks. Even \"rough moment, can you talk?\" counts.\n\nWhy it works: Social connection is one of the strongest buffers against stress and relapse. Talking with someone you trust calms your stress response and breaks the isolation that cravings feed on."},
        {"Why cravings feel so strong",
         "Substances act on the brain's reward system, especially dopamine pathways that learn which cues predict reward. Over time, places, people, moods, and times of day can trigger craving on their own, even long after stopping.\n\nThat is learned wiring, not weakness. These links fade with time, and practicing new responses builds new patterns."}
    };

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));

        TextView title = new TextView(this);
        title.setText("Coping skills");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        Button crisis = new Button(this);
        crisis.setText("In crisis? Call or text 988");
        crisis.setOnClickListener(v -> startActivity(
                new Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))));
        root.addView(crisis);

        TextView note = new TextView(this);
        note.setText("Tap a skill to read it. These tools support recovery but don't replace professional care.");
        note.setPadding(0, dp(8), 0, dp(8));
        root.addView(note);

        String[] names = new String[SKILLS.length];
        for (int i = 0; i < SKILLS.length; i++) names[i] = SKILLS[i][0];

        ListView list = new ListView(this);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        list.setOnItemClickListener((p, v, pos, id) -> new AlertDialog.Builder(this)
                .setTitle(SKILLS[pos][0])
                .setMessage(SKILLS[pos][1])
                .setPositiveButton("Close", null)
                .show());
        root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
    }
}
