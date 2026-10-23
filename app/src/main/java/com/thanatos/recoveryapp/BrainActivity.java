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

        setContentView(scroll);
    }
}
