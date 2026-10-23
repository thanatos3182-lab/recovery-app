package com.thanatos.recoveryapp;

import android.app.Activity;
import android.widget.Button;

public class Topic {
    public static Button btn(Activity a, String title) {
        Button b = new Button(a);
        b.setText(title);
        b.setAllCaps(false);
        b.setTextColor(0xFF00FF00);

        b.setBackgroundColor(0xFF000000); // light blue
        b.setOnClickListener(v -> InfoActivity.open(a, title));
        return b;
    }
}
