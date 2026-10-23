package com.thanatos.recoveryapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class InfoActivity extends Activity {

    public static void open(Activity a, String topic) {
        a.startActivity(new android.content.Intent(a, InfoActivity.class)
                .putExtra("topic", topic));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        TextView t = new TextView(this);

        String topic = getIntent().getStringExtra("topic");
        t.setText(LessonStore.get(topic));
        t.setTextSize(18);
        t.setPadding(20, 20, 20, 20);

        scroll.addView(t);
        setContentView(scroll);
    }
}
