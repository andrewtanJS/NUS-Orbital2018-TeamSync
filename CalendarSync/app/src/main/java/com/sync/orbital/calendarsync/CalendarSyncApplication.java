package com.sync.orbital.calendarsync;

import android.app.Application;
import android.support.v4.app.Fragment;

public class CalendarSyncApplication extends Application {

    private boolean isPersistent;

    private Fragment currentFragment;

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public void setPersistent(boolean persistent) {
        isPersistent = persistent;
    }
}
