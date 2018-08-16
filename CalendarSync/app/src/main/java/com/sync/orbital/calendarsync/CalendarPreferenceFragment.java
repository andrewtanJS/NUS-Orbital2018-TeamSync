package com.sync.orbital.calendarsync;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CalendarPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences as configured in the /res/xml/preferences.xml file
        // and displays them.
        // The preferences will be automatically saved.
        addPreferencesFromResource(R.xml.pref_calendar);
    }
}
