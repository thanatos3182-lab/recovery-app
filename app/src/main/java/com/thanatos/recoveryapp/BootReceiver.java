package com.thanatos.recoveryapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) return;
        AppointmentDb db = new AppointmentDb(c);
        for (AppointmentDb.Appt a : db.upcoming()) Reminders.schedule(c, a);
        CheckinReminder.schedule(c);
    }
}
