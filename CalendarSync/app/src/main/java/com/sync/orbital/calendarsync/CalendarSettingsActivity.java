package com.sync.orbital.calendarsync;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class CalendarSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the preferences fragment as the content of the activity
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new CalendarPreferenceFragment()).commit();
    }
}
