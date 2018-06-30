package com.sync.orbital.calendarsync;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    MaterialCalendarView calendarView;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView =  view.findViewById(R.id.calendarView); // get the reference of CalendarView
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(getActivity().getApplicationContext(),
                        date.getDay() + "/" + date.getMonth() + "/" + date.getYear() ,
                                Toast.LENGTH_LONG).show();
            }
        });
        calendarView.setTopbarVisible(true);

        setHasOptionsMenu(true);

        ((MainActivity)getActivity()).setTitle("Calendar");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (calendarView != null) {
            ViewGroup parent = (ViewGroup) calendarView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_calendar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Go to settings
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return true;
    }
}
