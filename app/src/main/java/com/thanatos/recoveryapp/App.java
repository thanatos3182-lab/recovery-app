package com.thanatos.recoveryapp;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.UncaughtExceptionHandler old = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try { CrashLog.save(this, e); } catch (Throwable ignore) {}
            if (old != null) old.uncaughtException(t, e);
        });
    }
}
