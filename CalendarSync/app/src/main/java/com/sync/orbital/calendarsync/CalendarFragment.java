package com.sync.orbital.calendarsync;

import android.content.Intent;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment
    implements WeekView.EventClickListener, MonthLoader.MonthChangeListener,
               WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener{

    WeekView calendarView;
    static final int TYPE_DAY_VIEW = 1;
    static final int TYPE_WEEK_VIEW = 2;
    int calendarViewType = TYPE_WEEK_VIEW;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        // Get a reference for the week view in the layout.
        calendarView = view.findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        calendarView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        calendarView.setMonthChangeListener(this);

        // Set long press listener for events.
        calendarView.setEventLongPressListener(this);

        // Set long press listener for empty view
        calendarView.setEmptyViewLongPressListener(this);
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
        Intent intent;

        switch (item.getItemId()) {
            case R.id.day_view:
                if (calendarViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    calendarView.setNumberOfVisibleDays(1);
                    calendarViewType = TYPE_DAY_VIEW;
                    // Lets change some dimensions to best fit the view.
                    calendarView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    calendarView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    calendarView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.week_view:
                if (calendarViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    calendarView.setNumberOfVisibleDays(7);
                    calendarViewType = TYPE_WEEK_VIEW;
                    // Lets change some dimensions to best fit the view.
                    calendarView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    calendarView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    calendarView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.month_view:
                break;
            case R.id.action_settings_cal:
                intent = new Intent(getActivity(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        return events;
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity().getApplicationContext(), "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity().getApplicationContext(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(getActivity().getApplicationContext(),
                "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return calendarView;
    }
}
