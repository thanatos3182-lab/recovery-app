package com.thanatos.recoveryapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    int count = 0;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(48, 96, 48, 48);
        TextView tv = new TextView(this);
        tv.setText("It works. Taps: 0");
        tv.setTextSize(24);
        Button btn = new Button(this);
        btn.setText("Tap me");
        btn.setOnClickListener(v -> {
            count++;
            tv.setText("It works. Taps: " + count);
        });
        box.addView(tv);
        box.addView(btn);
        setContentView(box);
    }
}
