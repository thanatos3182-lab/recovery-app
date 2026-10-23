package com.thanatos.recoveryapp;

import android.app.Activity;
import android.widget.TextView;

public class Banner {
    public static TextView create(Activity a) {
        TextView t = new TextView(a);
        t.setText("You’re doing great.\nOne day at a time.");
        t.setTextSize(22);
        t.setPadding(20, 40, 20, 40);
        t.setBackgroundColor(0xFF000000);
        t.setTextColor(0xFF00FF00);
        return t;
    }
}
