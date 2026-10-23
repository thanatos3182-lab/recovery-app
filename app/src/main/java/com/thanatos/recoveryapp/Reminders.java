package com.thanatos.recoveryapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Reminders {
    static PendingIntent pi(Context c, AppointmentDb.Appt a) {
        Intent i = new Intent(c, ReminderReceiver.class);
        i.putExtra("title", a.title);
        i.putExtra("time", a.time);
        return PendingIntent.getBroadcast(c, (int) a.id, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void schedule(Context c, AppointmentDb.Appt a) {
        long now = System.currentTimeMillis();
        long at = a.time - 3600000L;
        if (at <= now) at = a.time;
        if (at <= now) return;
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi(c, a));
    }

    public static void cancel(Context c, AppointmentDb.Appt a) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi(c, a));
    }
}
