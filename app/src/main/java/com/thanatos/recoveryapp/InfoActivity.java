package com.thanatos.recoveryapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.PrintWriter;
import java.io.StringWriter;

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
        t.setTextSize(18);
        t.setTextColor(0xFF00FF00);
        t.setBackgroundColor(0xFF000000);
        t.setPadding(20, 20, 20, 20);
        try {
            String topic = getIntent().getStringExtra("topic");
            t.setText(LessonStore.get(topic));
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            t.setText("ERROR:\n" + sw);
            t.setTextSize(12);
        }
        scroll.addView(t);
        setContentView(scroll);
    }
}
