package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

public class Topic {
    public static Button btn(Activity a, String title) {
        Button b = new Button(a);
        b.setText(title);
        b.setAllCaps(false);
        b.setTextColor(0xFF00FF00);
        b.setBackgroundColor(0xFF000000);
        b.setOnClickListener(v -> InfoActivity.open(a, title));
        return b;
    }

    public static Button link(Activity a, String label, String url) {
        Button b = new Button(a);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(0xFF5AB4FF);
        b.setBackgroundColor(0xFF0D1B2A);
        b.setOnClickListener(v ->
                a.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
        return b;
    }
}
