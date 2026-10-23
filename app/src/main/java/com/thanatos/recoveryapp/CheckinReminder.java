package com.thanatos.recoveryapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;

public class CheckinReminder {
    static SharedPreferences p(Context c) {
        return c.getSharedPreferences("checkin", Context.MODE_PRIVATE);
    }

    public static boolean enabled(Context c) { return p(c).getBoolean("on", false); }
    public static int hour(Context c) { return p(c).getInt("hour", 20); }
    public static int minute(Context c) { return p(c).getInt("min", 0); }

    public static void set(Context c, int h, int m) {
        p(c).edit().putBoolean("on", true).putInt("hour", h).putInt("min", m).apply();
        schedule(c);
    }

    public static void off(Context c) {
        p(c).edit().putBoolean("on", false).apply();
        cancel(c);
    }

    static PendingIntent pi(Context c) {
        Intent i = new Intent(c, CheckinReceiver.class);
        return PendingIntent.getBroadcast(c, 9001, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void schedule(Context c) {
        if (!enabled(c)) return;
        Calendar t = Calendar.getInstance();
        t.set(Calendar.HOUR_OF_DAY, hour(c));
        t.set(Calendar.MINUTE, minute(c));
        t.set(Calendar.SECOND, 0);
        t.set(Calendar.MILLISECOND, 0);
        if (t.getTimeInMillis() <= System.currentTimeMillis()) {
            t.add(Calendar.DAY_OF_YEAR, 1);
        }
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t.getTimeInMillis(), pi(c));
    }

    public static void cancel(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi(c));
    }
}
