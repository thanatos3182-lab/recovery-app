package com.thanatos.recoveryapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BrainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root);

        TextView header = new TextView(this);
        header.setText("Brain & Recovery\n\nTap a topic to learn how your brain changes during recovery.");
        header.setTextSize(20);
        header.setPadding(20, 40, 20, 40);
        header.setBackgroundColor(0xFF2196F3);
        header.setTextColor(0xFFFFFFFF);
        root.addView(header);

        root.addView(Topic.btn(this, "Interoception"));
        root.addView(Topic.btn(this, "Impulse Control"));
        root.addView(Topic.btn(this, "Craving Neurobiology"));
        root.addView(Topic.btn(this, "Meditation & Self-Talk"));
        root.addView(Topic.btn(this, "Emotion Labeling"));
        root.addView(Topic.btn(this, "Body Awareness"));
        root.addView(Topic.btn(this, "Positive Self-Talk"));
        root.addView(Topic.btn(this, "Addiction & The Brain"));
        root.addView(Topic.btn(this, "Neuroplasticity"));
        root.addView(Topic.btn(this, "The Habit Loop"));
        root.addView(Topic.btn(this, "Amygdala and Prefrontal Cortex"));
        root.addView(Topic.btn(this, "Dopamine and Reward"));
        root.addView(Topic.btn(this, "Stress and the HPA Axis"));
        root.addView(Topic.btn(this, "Cognitive Reframing"));
        root.addView(Topic.btn(this, "Rumination and the Default Mode Network"));
        root.addView(Topic.btn(this, "Sleep and Exercise"));
        root.addView(Topic.btn(this, "Fear Extinction"));
        root.addView(Topic.btn(this, "Window of Tolerance"));

        TextView more = new TextView(this);
        more.setText("Go deeper");
        more.setTextSize(20);
        more.setTextColor(0xFF00FF00);
        more.setPadding(20, 40, 20, 10);
        root.addView(more);

        root.addView(Topic.link(this, "NIDA: The Science of Addiction",
                "https://nida.nih.gov/publications/drugs-brains-behavior-science-addiction"));
        root.addView(Topic.link(this, "NIAAA: alcohol and the brain",
                "https://www.niaaa.nih.gov/"));
        root.addView(Topic.link(this, "NIMH: mental health science",
                "https://www.nimh.nih.gov/health"));
        root.addView(Topic.link(this, "BrainFacts.org (Society for Neuroscience)",
                "https://www.brainfacts.org/"));
        root.addView(Topic.link(this, "PubMed: search all research",
                "https://pubmed.ncbi.nlm.nih.gov/"));

        setContentView(scroll);
    }
}
