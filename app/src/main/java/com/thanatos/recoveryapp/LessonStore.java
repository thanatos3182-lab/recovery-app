package com.thanatos.recoveryapp;

public class LessonStore {

    public static String get(String topic) {

        switch (topic) {

            case "Interoception":
                return "Interoception is your brain’s ability to sense internal signals — heartbeat, breathing, tension, emotions, and stress buildup.\n\n"
                     + "It helps you catch cravings and emotional spikes early.";

            case "Impulse Control":
                return "Impulse control is the ability to pause, evaluate, and choose a better action instead of reacting automatically.\n\n"
                     + "It strengthens recovery by interrupting old habit loops.";

            case "Craving Neurobiology":
                return "Cravings come from dopamine, stress circuits, and habit loops.\n\n"
                     + "They rise and fall like waves, and can be managed with awareness.";

            case "Meditation & Self-Talk":
                return "Meditation calms the nervous system.\n\n"
                     + "Self-talk rewires emotional pathways and reduces stress signals.";

            case "Emotion Labeling":
                return "Naming emotions reduces amygdala activation and increases clarity.\n\n"
                     + "It helps you respond instead of react.";

            case "Body Awareness":
                return "Body awareness helps you detect stress, cravings, and emotions early.\n\n"
                     + "It is the foundation of emotional regulation.";

            case "Positive Self-Talk":
                return "Positive self-talk rewires negative patterns and strengthens recovery identity.\n\n"
                     + "It improves mood and resilience.";

            case "Addiction & The Brain":
                return "Addiction changes dopamine pathways, stress circuits, and habit loops.\n\n"
                     + "Recovery rewires these systems and restores balance.";

            default:
                return "No lesson found for: " + topic;
        }
    }
}
