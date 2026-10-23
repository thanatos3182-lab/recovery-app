package com.thanatos.recoveryapp;

public class LessonStore {

    static final String FOOT = "\n\n-----\nThis is education, not medical care. If you are in crisis, call or text 988 (US).";

    public static String get(String topic) {
        if (topic == null) return "Topic not found.";
        String body;
        switch (topic) {

            case "Interoception":
                body = "INTEROCEPTION\n\n"
                    + "WHAT IT IS\nInteroception is your brain's sense of what is happening inside your body: heartbeat, breathing, muscle tension, gut feelings, temperature, hunger.\n\n"
                    + "IN THE BRAIN\nSignals travel up from the body through the vagus nerve and spinal pathways to the insula and anterior cingulate cortex, which build a running map of your body's state. That map is a big part of how emotions and cravings actually feel. In a well-known 2007 study, smokers whose strokes damaged the insula were much more likely to quit easily, which pointed researchers toward the insula's role in urges.\n\n"
                    + "WHY IT MATTERS IN RECOVERY\nA craving starts in the body before it becomes a thought: a tight chest, restless hands, a racing pulse. People who notice those early signals have more time to respond before the urge peaks. Substance use can dull or distort these signals, and mindfulness-based programs are designed partly to retrain them.\n\n"
                    + "TRY THIS (2 MINUTES)\n1. Pause and notice your heartbeat, breath, jaw, shoulders, and stomach.\n2. Rate your tension from 1 to 10.\n3. Describe the sensations in plain words (tight, hot, buzzing) without judging them.\n4. Practice a few times a day when you're calm, so you can recognize the early signs when you're not.";
                break;

            case "Impulse Control":
                body = "IMPULSE CONTROL\n\n"
                    + "WHAT IT IS\nImpulse control is the ability to pause between an urge and an action.\n\n"
                    + "IN THE BRAIN\nTwo systems compete. The prefrontal cortex handles planning, weighing consequences, and holding back responses. Deeper reward circuits, including the striatum, push toward whatever has paid off before. In addiction, the reward circuits become more reactive to cues while prefrontal control tends to weaken, so urges can win even when you know better. Stress, poor sleep, and hunger make this worse by temporarily reducing prefrontal function.\n\n"
                    + "WHY IT MATTERS IN RECOVERY\nThis is not a character flaw. It is a brain-state problem, and it can improve. Imaging studies suggest prefrontal function and structure often recover at least partly with sustained abstinence, though timelines vary by person and substance.\n\n"
                    + "TRY THIS\n1. Delay: tell yourself you'll decide in 10 minutes, and do something else. Urges often fade or weaken.\n2. If-then plans: decide ahead of time, 'If I feel the urge at 9pm, then I call a friend.' Pre-made plans take pressure off your prefrontal cortex.\n3. Add friction: make the old behavior harder to reach.\n4. Protect your basics: sleep, food, and low stress keep the brakes working.";
                break;

            case "Craving Neurobiology":
                body = "CRAVING NEUROBIOLOGY\n\n"
                    + "WHAT IT IS\nA craving is a strong pull toward a substance or behavior, driven by learned brain circuits.\n\n"
                    + "IN THE BRAIN\nDopamine is often misunderstood as the pleasure chemical. It is closer to a wanting and anticipation signal. With repeated use, the brain learns that certain cues predict reward: people, places, moods, times of day. Those cues alone can trigger dopamine release in the striatum and a surge of wanting, even long after stopping.\n\n"
                    + "Stress adds a second route. The amygdala and stress hormones like CRF and cortisol can set off cravings even without any cue. Over time, repeated use also shifts control from goal-directed regions toward habit circuits in the dorsal striatum, which is why use can feel automatic.\n\n"
                    + "WHY IT MATTERS IN RECOVERY\nCravings are learned, so they can be unlearned. When a cue shows up and you don't use, the brain slowly builds new, competing associations (extinction learning). That learning is context-dependent, which is why cravings can return in old places and why a plan matters.\n\n"
                    + "TRY THIS\n1. List your top 3 cues (people, places, feelings, times).\n2. For each, write one plan: avoid it, change it, or face it with support.\n3. When an urge hits, surf it: watch it rise, peak, and fall without acting. They often pass within 20 to 30 minutes.\n4. Treat stress cravings with stress tools: slow breathing, movement, reaching out.";
                break;

            case "Meditation & Self-Talk":
                body = "MEDITATION & SELF-TALK\n\n"
                    + "WHAT IT IS\nMeditation is training your attention to notice thoughts and sensations without being pulled along by them. Self-talk is the running commentary in your head.\n\n"
                    + "IN THE BRAIN\nWhen your mind wanders and ruminates, the default mode network (including the medial prefrontal cortex and posterior cingulate) is highly active. Studies of experienced meditators show less default-mode activity and stronger connections to attention and control regions. Regular practice is also linked to calmer amygdala reactivity under stress.\n\n"
                    + "In clinical trials, mindfulness-based relapse prevention reduced heavy substance use and cravings compared with usual care in some studies, with benefits often showing up months later.\n\n"
                    + "SELF-TALK\nHarsh self-criticism raises shame and stress, which are common relapse triggers. Self-compassion is linked to lower shame and better coping. Talking to yourself with your own name or 'you' instead of 'I' has been shown to reduce emotional reactivity by creating a little distance.\n\n"
                    + "TRY THIS (5 MINUTES)\n1. Sit comfortably and follow your breath at the nostrils or belly.\n2. When you notice you've drifted, silently say 'thinking' and return to the breath. The noticing is the rep that strengthens the circuit.\n3. When you catch harsh self-talk, ask: what would I say to a friend in this spot?";
                break;

            case "Emotion Labeling":
                body = "EMOTION LABELING\n\n"
                    + "WHAT IT IS\nNaming exactly what you feel, out loud or in writing.\n\n"
                    + "IN THE BRAIN\nIn brain imaging studies, putting feelings into words reduced activity in the amygdala, the brain's alarm center, and increased activity in the right ventrolateral prefrontal cortex, a region involved in regulating emotion. This happens even when people aren't trying to calm down.\n\n"
                    + "Precision matters too. People who can tell emotions apart in fine detail (called emotional granularity) tend to cope better, and one study linked low granularity to heavier drinking when upset.\n\n"
                    + "WHY IT MATTERS IN RECOVERY\nVague distress ('I feel awful') pushes toward fast relief. A specific label ('I feel embarrassed and lonely') turns a flood into information you can act on.\n\n"
                    + "TRY THIS\n1. Complete the sentence: 'I notice I'm feeling ___ because ___.'\n2. Go beyond bad, mad, sad. Try disappointed, ashamed, restless, overwhelmed, left out, hopeful.\n3. Rate the intensity 1 to 10, wait 5 minutes, and rate it again.\n4. Write 2 or 3 labels in your journal each day. It builds the skill.";
                break;

            case "Body Awareness":
                body = "BODY AWARENESS\n\n"
                    + "WHAT IT IS\nUsing your body as a tool to read and calm your stress response. Interoception is noticing; this is acting on what you notice.\n\n"
                    + "IN THE BRAIN\nStress triggers the sympathetic nervous system and stress hormones: faster heart rate, shallow breathing, tight muscles. Your body can also send calming signals back up. Slow breathing with long exhales raises vagal activity and heart rate variability, shifting you toward the parasympathetic 'rest and digest' state.\n\n"
                    + "Aerobic exercise raises dopamine, endorphins, and BDNF, a protein that supports brain plasticity, and some studies find short bouts reduce cravings. Sleep matters too: sleep loss weakens prefrontal control and amplifies reward and stress reactivity.\n\n"
                    + "WHY IT MATTERS IN RECOVERY\nWhen thinking feels useless, the body is a faster door. You can lower arousal first, then think.\n\n"
                    + "TRY THIS\n1. Breathe in for 4, out for 6 to 8, for 2 to 5 minutes.\n2. Progressive muscle relaxation: tense each muscle group for 5 seconds, then release, from feet to face.\n3. Move for 5 to 10 minutes: walk, stretch, push-ups.\n4. Press your feet into the floor and notice the support under you.\n5. Treat sleep as a recovery tool.";
                break;

            case "Positive Self-Talk":
                body = "POSITIVE SELF-TALK\n\n"
                    + "WHAT IT IS\nDeliberately choosing believable, supportive statements about yourself and your recovery.\n\n"
                    + "IN THE BRAIN\nThe brain strengthens what it practices: repeated thoughts and behaviors reinforce the circuits behind them (neuroplasticity). In imaging studies, affirming personal values activated reward and self-related regions, including the ventromedial prefrontal cortex and ventral striatum, and made people less defensive about threatening information.\n\n"
                    + "Identity matters too. Research on recovery suggests people who build a new identity and social connections around recovery tend to do better.\n\n"
                    + "WHAT TO WATCH OUT FOR\nForced positivity can backfire. Studies found that over-the-top statements like 'I'm perfect' can make people with low self-esteem feel worse, because the brain rejects what it doesn't believe. True, growth-focused statements work better.\n\n"
                    + "TRY THIS\n1. Write 3 statements you actually believe, such as 'I'm learning to handle cravings,' 'I've gotten through hard days before,' 'I can ask for help.'\n2. Add 'yet' to harsh thoughts: 'I can't handle this... yet.'\n3. Connect to a value (honesty, family, health) and write one sentence on how today's choice serves it.\n4. Read your statements in the morning and when urges hit.";
                break;

            case "Addiction & The Brain":
                body = "ADDICTION & THE BRAIN\n\n"
                    + "WHAT IT IS\nAddiction is a chronic condition involving changes to the brain's reward, stress, and self-control systems. It is a health condition, not a moral failing.\n\n"
                    + "IN THE BRAIN\nResearchers describe a three-stage cycle.\n\n"
                    + "1. Binge/intoxication: the basal ganglia, including the striatum, get a strong dopamine hit and learn to link cues with reward, building habits.\n\n"
                    + "2. Withdrawal/negative affect: the extended amygdala, the stress center, becomes more reactive while the reward system dulls, so normal pleasures feel flat and stress feels sharper.\n\n"
                    + "3. Preoccupation/anticipation: the prefrontal cortex, which handles planning and self-control, is weaker at holding back craving.\n\n"
                    + "In many studies, people with addiction show lower dopamine D2 receptor availability in the brain's reward circuitry.\n\n"
                    + "RECOVERY IS REAL\nThis is not permanent. In imaging studies of people who stayed abstinent, dopamine function, brain structure, and executive function recovered at least partially over months to years. Feeling flat or irritable early on is a normal part of a brain recalibrating, and it usually eases. Timelines vary a lot, so compare yourself to your own past, not to a schedule.\n\n"
                    + "A NOTE ON SLIPS\nRelapse is common in many chronic conditions and doesn't erase your progress. It is information: what was the trigger, and what would you do differently?\n\n"
                    + "TRY THIS\n1. Track one small win each day in your journal.\n2. Tell your care team or a trusted person how you're really doing.\n3. Treat cravings, low mood, and poor sleep as signals from a healing brain, not proof you're failing.";
                break;

            default:
                return "Lesson coming soon.";
        }
        return body + FOOT;
    }
}
