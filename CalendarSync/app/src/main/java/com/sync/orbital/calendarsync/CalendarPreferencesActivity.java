package com.sync.orbital.calendarsync;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;

public class CalendarPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_material:
                if (checked)
                    ((CalendarSyncApplication) this.getApplication())
                            .setCalendarTheme(CalendarSyncApplication.CALENDAR_THEME.MATERIAL);
                    break;
            case R.id.radio_pastel:
                if (checked)
                    ((CalendarSyncApplication) this.getApplication())
                            .setCalendarTheme(CalendarSyncApplication.CALENDAR_THEME.PASTEL);
                break;
            case R.id.radio_bright:
                if (checked)
                    ((CalendarSyncApplication) this.getApplication())
                            .setCalendarTheme(CalendarSyncApplication.CALENDAR_THEME.BRIGHT);
                break;
            case R.id.radio_gradient:
                if (checked)
                    ((CalendarSyncApplication) this.getApplication())
                            .setCalendarTheme(CalendarSyncApplication.CALENDAR_THEME.GRADIENT);
                break;
        }
    }

}
