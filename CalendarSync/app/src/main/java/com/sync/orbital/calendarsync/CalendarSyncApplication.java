package com.sync.orbital.calendarsync;

import android.app.Application;

public class CalendarSyncApplication extends Application {

    private boolean isPersistent;

    public boolean isPersistent() {
        return isPersistent;
    }

    public void setPersistent(boolean persistent) {
        isPersistent = persistent;
    }
}
