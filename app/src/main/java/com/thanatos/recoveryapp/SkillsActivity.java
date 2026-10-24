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
         "WHAT IT IS\nA mindfulness skill for riding out a craving without acting on it. Cravings behave like waves: they rise, peak, and fall.\n\n"
         + "HOW\n1. Notice the urge and say: 'this is a craving.'\n2. Find it in your body. Where is it and what does it feel like?\n3. Breathe slowly and watch it build, crest, and fade.\n4. Don't fight it and don't act on it. Rate it 1 to 10 every few minutes.\n\n"
         + "WHY IT WORKS\nMost urges peak within minutes and often fade within 20 to 30 minutes, even if you do nothing. Each time you ride one out, you collect proof that it is survivable.\n\n"
         + "IN THE BRAIN\nCue-triggered cravings involve dopamine 'wanting' signals in the striatum and body-state signals from the insula. Acting on an urge strengthens the link between the cue and the behavior. Not acting lets the brain build a new competing memory: 'that cue, no use, and I was fine' (extinction learning). Mindfulness-based relapse prevention, which teaches this skill, reduced heavy use and cravings in some clinical trials.\n\n"
         + "MAKE IT A HABIT\nPractice on small urges when the stakes are low: an itch, the pull to check your phone, a snack you don't need. Skills you've rehearsed are easier to reach when a big craving hits."},

        {"Slow breathing",
         "WHAT IT IS\nA breathing pattern where the exhale is longer than the inhale.\n\n"
         + "HOW\n1. Sit or stand comfortably.\n2. Breathe in through your nose for 4 seconds.\n3. Breathe out slowly through your mouth for 6 to 8 seconds.\n4. Repeat for 2 to 5 minutes. If you feel dizzy, go back to normal breathing.\n\n"
         + "WHY IT WORKS\nYour body and brain talk both ways. Slow, steady breathing sends a signal that you are not in danger, which turns the stress response down.\n\n"
         + "IN THE BRAIN\nYour heart speeds up slightly on each inhale and slows on each exhale, and the slowing is carried by the vagus nerve. Long exhales and slow breathing raise heart rate variability and shift the balance toward the parasympathetic 'rest and digest' system. Lower arousal makes it easier for the prefrontal cortex to take the lead. The effect is real but moderate: it lowers intensity, it doesn't erase it.\n\n"
         + "MAKE IT A HABIT\nDo 2 minutes at the same time every day, like after brushing your teeth or before bed. Practicing while calm makes it work better when you're stressed."},

        {"5-4-3-2-1 grounding",
         "WHAT IT IS\nA way of using your senses to anchor yourself in the present moment.\n\n"
         + "HOW\nName, slowly, out loud or in your head:\n5 things you can see\n4 things you can touch\n3 things you can hear\n2 things you can smell\n1 thing you can taste\n\n"
         + "WHY IT WORKS\nCravings, anxiety, and shame pull attention into the future or the past. Sensory input only exists in the here and now, so it gives your mind somewhere else to go.\n\n"
         + "IN THE BRAIN\nAttention is limited. Deliberately aiming it at outside sensory details engages attention networks and leaves less room for the self-focused looping thought tied to the default mode network. Research on grounding itself is mostly clinical rather than lab-based, so this is a reasonable explanation, not a proven one.\n\n"
         + "MAKE IT A HABIT\nTry it once while calm so it feels familiar. Find a version you like, such as only the 5 things you can see."},

        {"Name it to tame it",
         "WHAT IT IS\nPutting exactly what you feel into words, out loud or in writing. Researchers call it affect labeling.\n\n"
         + "HOW\n1. Complete the sentence: 'I notice I'm feeling ___ because ___.'\n2. Be specific. Go beyond bad, mad, or sad: try ashamed, left out, restless, overwhelmed, disappointed, hopeful.\n3. Rate the intensity 1 to 10, wait 5 minutes, and rate it again.\n\n"
         + "WHY IT WORKS\nA vague feeling like 'I feel awful' pushes you toward fast relief. A specific label turns a flood into information you can act on.\n\n"
         + "IN THE BRAIN\nIn imaging studies, putting feelings into words lowered activity in the amygdala, the brain's alarm center, and raised activity in the right ventrolateral prefrontal cortex, which helps regulate emotion. Studies also link emotional granularity, the ability to tell feelings apart in fine detail, with better coping.\n\n"
         + "MAKE IT A HABIT\nWrite 2 or 3 specific emotion words in your journal each day. Your emotion vocabulary grows with practice."},

        {"Play the tape forward",
         "WHAT IT IS\nImagining the full story of using, not just the first moment.\n\n"
         + "HOW\n1. Picture yourself using. Then keep the movie going.\n2. How do you feel an hour later? Tomorrow morning? Next week?\n3. Who would you have to tell? What would it cost you?\n4. Then picture the other tape: the same hour if you ride it out.\n\n"
         + "WHY IT WORKS\nCravings zoom in on the immediate reward and hide everything after it. Walking through the whole sequence puts the cost back in the picture.\n\n"
         + "IN THE BRAIN\nAddiction is linked to delay discounting, a tendency to value now far more than later. Imagining specific future events (episodic future thinking) relies on the prefrontal cortex and hippocampus, and in several studies it reduced delay discounting and the pull toward immediate reward.\n\n"
         + "MAKE IT A HABIT\nWrite your own tape down when you're calm and clear-headed, so you can read it when you're not."},

        {"HALT check",
         "WHAT IT IS\nA fast self-check: am I Hungry, Angry, Lonely, or Tired?\n\n"
         + "HOW\n1. Stop and ask each of the four questions.\n2. Fix whichever one is true: eat something, deal with the anger (walk, write, talk), call someone, rest.\n3. Then check the craving again.\n\n"
         + "WHY IT WORKS\nThese states don't cause cravings by themselves, but they make everything harder to handle, including urges. Taking care of the basics takes pressure off your brain.\n\n"
         + "IN THE BRAIN\nSleep loss increases amygdala reactivity and weakens its coupling with the prefrontal cortex in imaging studies, so emotions hit harder and control is weaker. Stress hormones and social isolation also amplify threat and craving responses. Hunger drains the mental energy you'd use for self-control.\n\n"
         + "MAKE IT A HABIT\nSet a daily time to run HALT, such as midday and evening. Treat sleep and regular meals as part of your recovery plan."},

        {"Move your body",
         "WHAT IT IS\nShort bouts of physical movement to change your body and brain state.\n\n"
         + "HOW\n1. Walk, stretch, do push-ups, or dance for 5 to 10 minutes.\n2. If you can, change locations too, since leaving the cue helps.\n3. Notice how the urge feels afterward.\n\n"
         + "WHY IT WORKS\nMovement changes your state quickly, uses up stress energy, and gives your attention somewhere to go.\n\n"
         + "IN THE BRAIN\nExercise raises dopamine and endorphins and supports BDNF, a protein involved in brain plasticity. Studies show short bouts of exercise can reduce cravings, with the strongest evidence for nicotine and weaker, mixed evidence for other substances. Regular exercise also tends to improve mood, sleep, and executive function over time.\n\n"
         + "MAKE IT A HABIT\nPick a daily movement you can do on your worst day, like a 10-minute walk. Consistency matters more than intensity."},

        {"Reach out",
         "WHAT IT IS\nContacting someone safe before an urge peaks.\n\n"
         + "HOW\n1. Choose who you'd text or call (your Support contacts list is for this).\n2. Send something simple: 'rough moment, can you talk?'\n3. You don't have to explain everything. Even talking about something else counts.\n\n"
         + "WHY IT WORKS\nIsolation feeds cravings and shame. Connection interrupts both, and it gives you time while the urge passes.\n\n"
         + "IN THE BRAIN\nIn lab studies, the presence of a supportive person reduces threat responses and stress reactivity, sometimes called social buffering. Social connection is also one of the strongest predictors of staying in recovery, and people who build a recovery-supporting social circle tend to do better.\n\n"
         + "MAKE IT A HABIT\nReach out when you're doing fine, not only when you're struggling, so the habit and the relationships are already there when you need them."},

        {"Why cravings feel so strong",
         "WHAT IT IS\nThe science of why a craving can feel overwhelming, and why it isn't a weakness.\n\n"
         + "THE SHORT VERSION\nSubstances act on the brain's reward system, especially dopamine pathways that learn which cues predict reward. Over time, places, people, moods, and times of day can trigger craving on their own, even long after stopping.\n\n"
         + "IN THE BRAIN\nDopamine works more like a wanting and anticipation signal than a pleasure signal. Repeated use also shifts control from goal-directed regions toward habit circuits in the dorsal striatum, so use can feel automatic. Stress adds a second route: the amygdala and stress hormones can set off craving without any cue at all.\n\n"
         + "WHAT THIS MEANS\nThat is learned wiring, not weakness. Learned links can be weakened. Each time a cue shows up and you don't act, the brain builds a new competing association. Practicing new responses builds new patterns.\n\n"
         + "MAKE IT A HABIT\nKnow your top 3 cues and have one plan for each."},

        {"Reframe the thought",
         "WHAT IT IS\nA technique from cognitive behavioral therapy for spotting an unhelpful thought and building a more balanced one.\n\n"
         + "HOW\n1. Write the situation and the thought: 'I slipped, so I've blown it.'\n2. Name the feeling and rate it 1 to 10.\n3. Ask: what's the evidence for this thought? Against it? What would I tell a friend?\n4. Write a balanced version: 'I slipped, and I can learn from it and keep going.'\n5. Re-rate the feeling.\n\n"
         + "WHY IT WORKS\nFeelings come from how you interpret events, not just the events. Changing the interpretation changes the feeling and the urge to cope with it by using.\n\n"
         + "IN THE BRAIN\nCognitive reappraisal engages prefrontal regions (lateral and ventrolateral prefrontal cortex and the anterior cingulate) and is linked to reduced amygdala activity in imaging studies. The brain strengthens what it practices, so repeated reframing makes the balanced thought show up faster.\n\n"
         + "MAKE IT A HABIT\nDo one thought record a day, even about something small. It's a rep for the same circuit you need in hard moments."},

        {"Muscle relaxation",
         "WHAT IT IS\nProgressive muscle relaxation: tensing and releasing muscle groups to teach your body the difference between tense and relaxed.\n\n"
         + "HOW\n1. Start at your feet. Tense the muscles for about 5 seconds.\n2. Release and notice the difference for 10 to 15 seconds.\n3. Move up: calves, thighs, stomach, hands, arms, shoulders, face.\n4. Breathe slowly the whole time. Skip any area that hurts.\n\n"
         + "WHY IT WORKS\nStress shows up as tension you often don't notice. Once you can feel it, you can let it go on purpose.\n\n"
         + "IN THE BRAIN\nIt lowers sympathetic arousal and builds interoceptive awareness, the insula-based sense of your body's state. Clinical studies show it reduces anxiety and improves sleep, though it's usually studied as part of a larger program.\n\n"
         + "MAKE IT A HABIT\nDo it in bed once a week at first. A 10-minute version before sleep helps many people."},

        {"Delay and distract",
         "WHAT IT IS\nBuying time for an urge to pass by choosing a demanding activity.\n\n"
         + "HOW\n1. Tell yourself: 'I'll decide in 10 minutes.'\n2. Start something that takes real attention: a puzzle, cleaning, a game, cooking, a phone call, drawing.\n3. When the timer ends, rate the urge again. If it's still there, delay another 10.\n\n"
         + "WHY IT WORKS\nUrges rise and fall. You don't need to win the argument, you only need to outlast the peak.\n\n"
         + "IN THE BRAIN\nCraving relies partly on vivid mental imagery, and imagery competes for limited working memory. In lab studies, visually demanding tasks reduced how vivid and intense cravings felt. Delaying also gives the prefrontal cortex time to come back online after a spike in reward and stress signals.\n\n"
         + "MAKE IT A HABIT\nKeep a short list of distraction activities in your phone, ones that absorb you, so you don't have to think of one in the moment."}
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
        note.setText("Tap a skill to read how to do it, why it works, and what it does in the brain. These tools support recovery but don't replace professional care.");
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
