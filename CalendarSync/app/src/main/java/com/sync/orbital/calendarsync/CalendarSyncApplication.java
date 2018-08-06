package com.sync.orbital.calendarsync;

import android.app.Application;

public class CalendarSyncApplication extends Application {

    private boolean isPersistent;

    private CALENDAR_THEME theme;

    public CALENDAR_THEME getCalendarTheme() {
        return theme;
    }

    public void setCalendarTheme(CALENDAR_THEME theme) {
        this.theme = theme;
    }

    public enum CALENDAR_THEME {
        MATERIAL, BRIGHT, GRADIENT, PASTEL
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public void setPersistent(boolean persistent) {
        isPersistent = persistent;
    }
}
