package com.thanatos.recoveryapp;

import java.util.Locale;

public class CoachContent {

    public static final String[] FEELINGS = {
        "Stressed", "Anxious", "Angry", "Lonely", "Bored",
        "Sad", "Tired", "Ashamed", "Triggered", "Other"
    };

    public static final String[] ALL_SKILLS = {
        "Urge surfing", "Slow breathing", "5-4-3-2-1 grounding",
        "Move your body", "Play the tape forward", "HALT check", "Reach out"
    };

    public static final String SLIP =
        "Thank you for being honest. A slip doesn't erase your days or your progress, "
        + "and shame makes the next hour harder, not easier. Let's make the next hour safer: "
        + "put distance between you and anything left, drink some water, and tell someone you trust or your care team.";

    static String norm(String s) {
        return s.toLowerCase(Locale.US).replace('\u2019', '\'');
    }

    static boolean hasAny(String t, String... words) {
        for (String w : words) if (t.contains(w)) return true;
        return false;
    }

    public static boolean isCrisis(String text) {
        String t = norm(text);
        return hasAny(t, "kill myself", "suicid", "end my life", "want to die", "wanna die",
                "don't want to be here", "hurt myself", "self harm", "self-harm",
                "overdose", "took too much", "better off dead");
    }

    public static boolean isSlip(String text) {
        String t = norm(text);
        return hasAny(t, "relapse", "slipped", "used again", "already used",
                "already drank", "drank again", "gave in", "i caved");
    }

    public static String matchFeeling(String text) {
        String t = norm(text);
        if (hasAny(t, "stress", "overwhelm", "pressure")) return "Stressed";
        if (hasAny(t, "anxi", "nervous", "panic", "worried", "worry", "scared")) return "Anxious";
        if (hasAny(t, "angry", "anger", "mad ", "furious", "pissed", "rage", "frustrat")) return "Angry";
        if (hasAny(t, "lonely", "alone", "isolat")) return "Lonely";
        if (hasAny(t, "bored", "boring", "nothing to do")) return "Bored";
        if (hasAny(t, "asham", "guilt", "shame", "failure", "embarrass")) return "Ashamed";
        if (hasAny(t, "sad", "depress", "empty", "hopeless", "feel down", "feeling down", "cry")) return "Sad";
        if (hasAny(t, "tired", "exhaust", "sleep", "drained")) return "Tired";
        if (hasAny(t, "trigger", "party", "old friends", "smell", "drove past", "walked past")) return "Triggered";
        return null;
    }

    public static String reply(String feeling) {
        switch (feeling) {
            case "Stressed":
                return "Stress is one of the strongest craving triggers. Your body wants relief, and that's normal. Let's lower the pressure first.";
            case "Anxious":
                return "Anxiety and cravings feed each other, and your body is on high alert. Let's calm the alarm first.";
            case "Angry":
                return "Anger pumps up your body and makes quick relief look tempting. Let's burn off that energy safely.";
            case "Lonely":
                return "Loneliness is a real trigger, and reaching for connection is one of the strongest moves you can make right now.";
            case "Bored":
                return "Boredom leaves a gap that cravings love to fill. Let's fill it on purpose.";
            case "Sad":
                return "Feeling low makes cravings louder. You're not weak for feeling this, and you don't have to carry it alone.";
            case "Tired":
                return "Being tired lowers your brain's brakes, so cravings hit harder. Let's be gentle with you.";
            case "Ashamed":
                return "Shame is heavy, and it often fuels cravings. You're here fighting for yourself, and that matters more than any feeling right now.";
            case "Triggered":
                return "Something set this off, and that's what triggers do. Noticing it is the first step to getting some distance from it.";
            default:
                return "Thanks for telling me. You don't have to figure it all out. We just need to get through the next few minutes.";
        }
    }

    public static String[] skillsFor(String feeling) {
        switch (feeling) {
            case "Stressed":  return new String[]{"Slow breathing", "Move your body", "5-4-3-2-1 grounding"};
            case "Anxious":   return new String[]{"Slow breathing", "5-4-3-2-1 grounding", "Urge surfing"};
            case "Angry":     return new String[]{"Move your body", "Slow breathing", "Urge surfing"};
            case "Lonely":    return new String[]{"Reach out", "Urge surfing", "HALT check"};
            case "Bored":     return new String[]{"Move your body", "Reach out", "Play the tape forward"};
            case "Sad":       return new String[]{"Reach out", "Slow breathing", "HALT check"};
            case "Tired":     return new String[]{"HALT check", "Slow breathing", "Play the tape forward"};
            case "Ashamed":   return new String[]{"Reach out", "Urge surfing", "Slow breathing"};
            case "Triggered": return new String[]{"Play the tape forward", "Urge surfing", "Move your body"};
            default:          return new String[]{"Urge surfing", "Slow breathing", "Reach out"};
        }
    }

    public static String[] steps(String skill) {
        switch (skill) {
            case "Urge surfing":
                return new String[]{
                    "Say it in your head: 'This is a craving. It's a wave, not a command.'",
                    "Find where you feel it in your body. Chest? Stomach? Hands? Jaw?",
                    "Breathe slowly and watch it. Notice it rise, crest, and start to fall.",
                    "Don't fight it and don't act on it. Just watch, like a surfer on a wave.",
                    "If it spikes, that's normal. Keep breathing. Waves peak and pass."};
            case "Slow breathing":
                return new String[]{
                    "Sit or stand comfortably. Put one hand on your belly.",
                    "Breathe in through your nose for 4 seconds.",
                    "Breathe out slowly through your mouth for 6 to 8 seconds.",
                    "Repeat for about 2 minutes. Long exhales help calm your nervous system."};
            case "5-4-3-2-1 grounding":
                return new String[]{
                    "Look around. Name 5 things you can see.",
                    "Name 4 things you can touch. Actually touch them.",
                    "Name 3 things you can hear.",
                    "Name 2 things you can smell.",
                    "Name 1 thing you can taste. Then take one slow breath."};
            case "Move your body":
                return new String[]{
                    "Stand up. Change rooms or step outside if you can.",
                    "Walk, stretch, or do push-ups or squats for 5 to 10 minutes.",
                    "Notice your breathing and heartbeat change. You're burning off stress energy."};
            case "Play the tape forward":
                return new String[]{
                    "Picture giving in. See it clearly.",
                    "Keep the movie going. How do you feel an hour later?",
                    "Now tomorrow morning. Then next week.",
                    "Who would you have to tell? What would it cost you?",
                    "Now picture the other ending: you rode this out. How does tomorrow feel then?"};
            case "HALT check":
                return new String[]{
                    "Hungry? If yes, eat something real.",
                    "Angry? Name who or what it's about.",
                    "Lonely? Text or call someone, even briefly.",
                    "Tired? Rest, even 20 minutes lying down with your eyes closed.",
                    "Fix the basic need first. Cravings often shrink when it's handled."};
            case "Reach out":
                return new String[]{
                    "Pick one safe person: a friend, family member, sponsor, or counselor.",
                    "Send or say: 'Rough moment. Can you talk or stay on text for a bit?'",
                    "You don't have to explain everything. Just not being alone helps.",
                    "No one available? Call or text 988, or the SAMHSA helpline at 1-800-662-4357. Both are free and 24/7."};
            default:
                return new String[]{"Take one slow breath. You're doing something good for yourself."};
        }
    }
}
