package com.thanatos.recoveryapp;

public class LessonStore {

    public static String get(String topic) {
        switch (topic) {
        case "Interoception":
            return "Interoception is your brain’s ability to sense internal signals — heartbeat, breathing, muscle tension, stomach sensations, emotional pressure, and stress buildup.\n\n"
                + "WHY IT MATTERS:\n"
                + "Interoception is the early-warning system for emotions. Before you feel anxious, angry, overwhelmed, or triggered, your body sends signals first. People relapse when emotions feel sudden and overwhelming, but emotions never actually hit suddenly — the body signals them early.\n\n"
                + "WHAT INTEROCEPTION DOES:\n"
                + "• Detects rising anxiety\n"
                + "• Detects building anger\n"
                + "• Detects emotional pressure\n"
                + "• Detects cravings forming\n"
                + "• Detects stress spikes\n\n"
                + "THE BRAIN CIRCUITS:\n"
                + "• Insula — your internal radar\n"
                + "• Somatosensory cortex — your body map\n"
                + "• Prefrontal cortex — names the emotion and calms the reaction\n\n"
                + "WHAT IT FEELS LIKE:\n"
                + "• Tight chest\n"
                + "• Warm face\n"
                + "• Clenched jaw\n"
                + "• Sinking stomach\n"
                + "• Buzzing nerves\n"
                + "• Shallow breathing\n"
                + "• Pressure in throat\n\n"
                + "These sensations are not problems — they are data. They tell you what emotion is coming BEFORE the emotion becomes a thought or behavior.\n\n"
                + "HOW TO TRAIN INTEROCEPTION:\n"
                + "1. Body Scan (2 minutes) — Start at your feet and move upward. Notice sensations without judging them.\n\n"
                + "2. Breath Awareness — Notice speed, depth, and tension. Slower breathing increases prefrontal control.\n\n"
                + "3. Emotion Mapping — Ask: Where do I feel this? What shape is it? Rising or fading?\n\n"
                + "4. Micro Check-Ins — Every few hours: “What is my body doing right now?”\n\n"
                + "5. Progressive Relaxation — Tense → hold → release muscles to reset the nervous system.\n\n"
                + "WHY THIS HELPS RECOVERY:\n"
                + "Interoception lets you catch emotional spikes early, regulate before crisis, stop impulsive reactions, avoid overwhelm, and break old patterns. It is the foundation of emotional mastery and relapse prevention.\n\n"
                + "SUMMARY:\n"
                + "Interoception is the skill that turns chaos into clarity. When you can feel your emotions early, you can control your behavior instead of reacting automatically.";



            default:
                return "No lesson available.";
        }
    }
}
