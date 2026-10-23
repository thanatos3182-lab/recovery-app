package com.thanatos.recoveryapp;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Section {
    public static LinearLayout create(Activity a, String title) {
        LinearLayout box = new LinearLayout(a);
        box.setOrientation(LinearLayout.VERTICAL);

        Button header = new Button(a);
        header.setText(title);
        header.setAllCaps(false);
        header.setBackgroundColor(0xFF000000);
        header.setTextColor(0xFF00FF00);

        LinearLayout content = new LinearLayout(a);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setVisibility(View.GONE);

        header.setOnClickListener(v -> {
            content.setVisibility(content.getVisibility() == View.VISIBLE
                    ? View.GONE : View.VISIBLE);
        });

        box.addView(header);
        box.addView(content);
        return box;
    }

    public static void addItem(Activity a, LinearLayout section, String text, Runnable action) {
        LinearLayout content = (LinearLayout) section.getChildAt(1);

        Button b = new Button(a);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(0xFF00FF00);

        b.setBackgroundColor(0xFF000000);
        b.setOnClickListener(v -> action.run());

        content.addView(b);
    }
}
