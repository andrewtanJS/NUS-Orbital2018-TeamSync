package com.sync.orbital.calendarsync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
}
