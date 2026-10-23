package com.thanatos.recoveryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Nav {
    public static void add(Activity a, LinearLayout root) {
        LinearLayout row = new LinearLayout(a);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(btn(a, "Appointments", v ->
                a.startActivity(new Intent(a, AppointmentsActivity.class))));
        row.addView(btn(a, "Coping skills", v ->
                a.startActivity(new Intent(a, SkillsActivity.class))));
        row.addView(btn(a, "Crisis: 988", v ->
                a.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:988")))));
        root.addView(row);
    }

    static Button btn(Activity a, String text, View.OnClickListener l) {
        Button b = new Button(a);
        b.setText(text);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setOnClickListener(l);
        b.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        return b;
    }
}
