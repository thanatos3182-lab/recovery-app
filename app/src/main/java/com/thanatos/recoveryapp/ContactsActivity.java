package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContactsActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    LinearLayout list;

    int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    TextView tv(String text, int sp) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(sp);
        t.setTextColor(GREEN);
        t.setPadding(0, dp(10), 0, dp(6));
        return t;
    }

    Button weighted(Button b) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(4), dp(2), dp(4), dp(8));
        b.setLayoutParams(lp);
        return b;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(root);

        TextView title = tv("Support contacts", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("People you can reach in a hard moment. Saved on this phone only.", 14));

        root.addView(ContactsStore.margin(
                ContactsStore.styled(this, "Add contact", v -> showAdd())));

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        root.addView(list);

        setContentView(scroll);
        refresh();
    }

    void refresh() {
        list.removeAllViews();
        JSONArray arr = ContactsStore.load(this);
        if (arr.length() == 0) {
            list.addView(tv("No contacts yet. Add a sponsor, a friend, and your counselor.", 16));
        }
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;
            final int idx = i;
            final String name = o.optString("name");
            final String number = o.optString("number");
            String role = o.optString("role");
            list.addView(tv(name + (role.isEmpty() ? "" : " (" + role + ")") + "\n" + number, 18));
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(weighted(ContactsStore.styled(this, "Call",
                    v -> ContactsStore.call(this, number))));
            row.addView(weighted(ContactsStore.styled(this, "Text",
                    v -> ContactsStore.text(this, number))));
            row.addView(weighted(ContactsStore.styled(this, "Delete",
                    v -> confirmDelete(idx, name))));
            list.addView(row);
        }
    }

    void confirmDelete(int idx, String name) {
        new AlertDialog.Builder(this)
                .setMessage("Delete " + name + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    ContactsStore.remove(this, idx);
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void showAdd() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), 0);
        EditText n = new EditText(this);
        n.setHint("Name");
        EditText p = new EditText(this);
        p.setHint("Phone number");
        p.setInputType(InputType.TYPE_CLASS_PHONE);
        EditText r = new EditText(this);
        r.setHint("Role (sponsor, friend, counselor)");
        form.addView(n);
        form.addView(p);
        form.addView(r);
        new AlertDialog.Builder(this)
                .setTitle("Add contact")
                .setView(form)
                .setPositiveButton("Save", (d, w) -> {
                    String name = n.getText().toString().trim();
                    String num = p.getText().toString().trim();
                    if (name.isEmpty() || num.isEmpty()) {
                        Toast.makeText(this, "Name and number needed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ContactsStore.add(this, name, num, r.getText().toString().trim());
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
