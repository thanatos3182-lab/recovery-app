package com.thanatos.recoveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class MeetingsActivity extends Activity {
    static final int GREEN = 0xFF00FF00;
    static final int BLACK = 0xFF000000;
    static final String[] DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};
    static final String[] RULE_DAYS = {"SU", "MO", "TU", "WE", "TH", "FR", "SA"};

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

    void link(LinearLayout root, String label, String url) {
        root.addView(ContactsStore.margin(ContactsStore.styled(this, label,
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))))));
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

        TextView title = tv("Meetings", 28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        root.addView(tv("Meetings help most when they become a routine. Find one, then save it here so it's one tap from your calendar.", 14));

        root.addView(tv("Find a meeting", 20));
        link(root, "Find an AA meeting", "https://www.aa.org/find-aa");
        link(root, "Online AA meetings", "https://aa-intergroup.org/meetings");
        link(root, "Find an NA meeting", "https://www.na.org/meetingsearch/");
        link(root, "SMART Recovery", "https://www.smartrecovery.org/");
        link(root, "SAMHSA treatment locator", "https://findtreatment.gov/");

        root.addView(tv("My meetings", 20));
        root.addView(ContactsStore.margin(
                ContactsStore.styled(this, "Add a regular meeting", v -> startAdd())));
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        root.addView(list);

        setContentView(scroll);
        refresh();
    }

    // ---------- storage ----------

    JSONArray load() {
        try {
            return new JSONArray(getSharedPreferences("meetings", MODE_PRIVATE)
                    .getString("list", "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    void store(JSONArray a) {
        getSharedPreferences("meetings", MODE_PRIVATE)
                .edit().putString("list", a.toString()).apply();
    }

    void saveMeeting(String name, String place, int day, int h, int m) {
        try {
            JSONArray a = load();
            JSONObject o = new JSONObject();
            o.put("name", name);
            o.put("place", place);
            o.put("day", day);
            o.put("hour", h);
            o.put("min", m);
            a.put(o);
            store(a);
        } catch (Exception ignore) {
        }
    }

    void removeMeeting(int idx) {
        JSONArray old = load();
        JSONArray out = new JSONArray();
        for (int i = 0; i < old.length(); i++) {
            if (i != idx) out.put(old.opt(i));
        }
        store(out);
    }

    // ---------- add flow ----------

    void startAdd() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), 0);
        EditText n = new EditText(this);
        n.setHint("Meeting name, e.g. Tuesday night group");
        EditText p = new EditText(this);
        p.setHint("Address or link (optional)");
        form.addView(n);
        form.addView(p);
        new AlertDialog.Builder(this)
                .setTitle("Add a regular meeting")
                .setView(form)
                .setPositiveButton("Next", (d, w) -> {
                    String name = n.getText().toString().trim();
                    if (name.isEmpty()) name = "Meeting";
                    pickDay(name, p.getText().toString().trim());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    void pickDay(String name, String place) {
        new AlertDialog.Builder(this)
                .setTitle("Which day?")
                .setItems(DAYS, (d, which) -> pickTime(name, place, which + 1))
                .show();
    }

    void pickTime(String name, String place, int day) {
        new TimePickerDialog(this, (view, h, m) -> {
            saveMeeting(name, place, day, h, m);
            refresh();
        }, 19, 0, false).show();
    }

    // ---------- list ----------

    String timeText(int h, int m) {
        return String.format(Locale.US, "%d:%02d %s",
                (h % 12 == 0 ? 12 : h % 12), m, h < 12 ? "AM" : "PM");
    }

    void refresh() {
        list.removeAllViews();
        JSONArray arr = load();
        if (arr.length() == 0) {
            list.addView(tv("No saved meetings yet.", 16));
        }
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;
            final int idx = i;
            final String name = o.optString("name");
            final String place = o.optString("place");
            final int day = o.optInt("day", 1);
            final int h = o.optInt("hour", 19);
            final int m = o.optInt("min", 0);
            String label = DAYS[Math.max(1, Math.min(7, day)) - 1] + " " + timeText(h, m)
                    + "\n" + name + (place.isEmpty() ? "" : "\n" + place);
            list.addView(tv(label, 18));

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(weighted(ContactsStore.styled(this, "Add to calendar",
                    v -> addToCalendar(name, place, day, h, m))));
            if (place.startsWith("http")) {
                row.addView(weighted(ContactsStore.styled(this, "Open link",
                        v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(place))))));
            }
            row.addView(weighted(ContactsStore.styled(this, "Delete",
                    v -> new AlertDialog.Builder(this)
                            .setMessage("Delete " + name + "?")
                            .setPositiveButton("Delete", (d, w) -> {
                                removeMeeting(idx);
                                refresh();
                            })
                            .setNegativeButton("Cancel", null)
                            .show())));
            list.addView(row);
        }
    }

    void addToCalendar(String name, String place, int day, int h, int m) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int diff = (day - c.get(Calendar.DAY_OF_WEEK) + 7) % 7;
        c.add(Calendar.DAY_OF_YEAR, diff);
        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 7);
        }
        Intent i = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, name)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, place)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, c.getTimeInMillis() + 60 * 60 * 1000L)
                .putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;BYDAY=" + RULE_DAYS[day - 1]);
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "No calendar app found", Toast.LENGTH_SHORT).show();
        }
    }
}
