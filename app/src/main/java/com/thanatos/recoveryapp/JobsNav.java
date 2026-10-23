package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;

public class JobsNav {
    public static void add(Activity a, LinearLayout root) {
        Button b = new Button(a);
        b.setText("Local jobs");
        b.setAllCaps(false);
        b.setOnClickListener(v -> a.startActivity(new Intent(a, JobsActivity.class)));
        root.addView(b);
    }
}
