package com.thanatos.recoveryapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
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
        scroll.setBackgroundColor(0xFF000000);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 20, 20, 20);
        scroll.addView(root);

        TextView t = new TextView(this);
        t.setTextSize(18);
        t.setTextColor(0xFF00FF00);
        t.setBackgroundColor(0xFF000000);
        root.addView(t);

        try {
            String topic = getIntent().getStringExtra("topic");
            t.setText(LessonStore.get(topic));
            String q = Uri.encode(topic + " addiction");
            root.addView(Topic.link(this, "Search research on PubMed (free full text)",
                    "https://pubmed.ncbi.nlm.nih.gov/?term=" + q + "&filter=simsearch2.ffrft"));
            root.addView(Topic.link(this, "Search Google Scholar",
                    "https://scholar.google.com/scholar?q=" + q));
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            t.setText("ERROR:\n" + sw);
            t.setTextSize(12);
        }

        setContentView(scroll);
    }
}
