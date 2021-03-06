package com.sync.orbital.calendarsync;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment
    implements WeekView.EventClickListener, MonthLoader.MonthChangeListener,
               WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener{

    WeekView calendarView;
    static final int TYPE_DAY_VIEW = 1;
    static final int TYPE_THREE_DAY_VIEW = 2;
    static final int TYPE_WEEK_VIEW = 3;
    int calendarViewType = TYPE_THREE_DAY_VIEW;
    private ArrayList<EventIncomingStruct> eventList;
    String defaultCalendarTheme, calendarTheme;
    int defaultCalendarWidth, calendarWidth;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Event info
        eventList = new ArrayList<>();

        Log.i("Calendar", "getting firebase event data");
        getFirebaseEventData(new EventsCallback(){
            @Override
            public void onCallBack(EventIncomingStruct event){
                eventList.add(event);
                calendarView.notifyDatasetChanged();
            }
        });

        // Get a reference for the week view in the layout.
        calendarView = view.findViewById(R.id.weekView);

        // Getting preferences
        setPreferences();

        // refreshes the view
        calendarView.notifyDatasetChanged();

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

        calendarView.setShowNowLine(true);
        calendarView.setNowLineColor(R.color.colorPrimary);

        defaultCalendarTheme = getActivity().getResources()
                .getString(R.string.pref_default_calendar_color);

        ((MainActivity)getActivity()).setTitle("Calendar");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setPreferences();
        calendarView.notifyDatasetChanged();
    }

    private void setPreferences() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        defaultCalendarTheme = getActivity().getResources()
                .getString(R.string.pref_default_calendar_color);
        calendarTheme = preferences.getString("calendar_color",
                defaultCalendarTheme);
        defaultCalendarWidth = Integer.parseInt(getActivity().getResources()
                .getString(R.string.pref_default_calendar_width));
        calendarWidth = Integer.parseInt(preferences
                .getString("calendar_width",
                        String.valueOf(defaultCalendarWidth)));
        calendarView.setNumberOfVisibleDays(calendarWidth);
        if(calendarWidth > 3) {
            calendarView.setDayNameLength(WeekView.LENGTH_SHORT);
        } else {
            calendarView.setDayNameLength(WeekView.LENGTH_LONG);
        }
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // The list of events in the week view
        int[] colors = this.getColors(calendarTheme);
        List<WeekViewEvent> events = new ArrayList<>();
        int id = 0;
        for(EventIncomingStruct event: eventList) {
            String eventStartTime = event.getStartTime();
            String eventStartDate = event.getStartDate();
            String eventEndTime = event.getEndTime();
            String eventEndDate = event.getEndDate();
            String[] strStTime = eventStartTime.split(":");
            String[] strStDate = eventStartDate.split("/");
            String[] strEndTime = eventEndTime.split(":");
            String[] strEndDate = eventEndDate.split("/");
            Log.i("Calendar", eventStartDate);
            if (Integer.parseInt(strStDate[2]) == newYear
                    && Integer.parseInt(strStDate[1]) == newMonth) {
                WeekViewEvent wkEvent = new WeekViewEvent(id, event.getName(),
                        Integer.parseInt(strStDate[2]),
                        Integer.parseInt(strStDate[1]),
                        Integer.parseInt(strStDate[0]),
                        Integer.parseInt(strStTime[0]),
                        Integer.parseInt(strStTime[1]),
                        Integer.parseInt(strEndDate[2]),
                        Integer.parseInt(strEndDate[1]),
                        Integer.parseInt(strEndDate[0]),
                        Integer.parseInt(strEndTime[0]),
                        Integer.parseInt(strEndTime[1]));
                wkEvent.setColor(colors[id % 5]);
                events.add(wkEvent);
            }
            id++;
        }

        return events;
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
            case R.id.activity_preferences:
                intent = new Intent(getActivity(), CalendarSettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    private void getFirebaseEventUid(final EventsUidCallback eventsUidCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsUidRef = reference.child("Users")
                .child(user.getUid())
                .child("events");
        Log.i("EventUid", "adding listener");
        eventsUidRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Result will be holded Here
                for (DataSnapshot dataSnap: dataSnapshot.getChildren()){
                    eventsUidCallback.onCallBack(dataSnap.getKey());
                    Log.i("EventUid", dataSnap.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Handle error
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void getFirebaseEventData(final EventsCallback eventsCallback) {
        getFirebaseEventUid(new EventsUidCallback(){
            @Override
            public void onCallBack(String eventUid){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference eventsRef = reference.child("Events")
                        .child(eventUid);
                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Result will be holded Here
                        String name = String.valueOf(dataSnapshot.child("name").getValue());
                        String status = String.valueOf(dataSnapshot.child("status").getValue());
                        String attendees = String.valueOf(dataSnapshot.child("attendees").getValue());
                        String startDate =  String.valueOf(dataSnapshot.child("startDate").getValue());
                        String startTime =  String.valueOf(dataSnapshot.child("startTime").getValue());
                        String endDate =  String.valueOf(dataSnapshot.child("endDate").getValue());
                        String endTime =  String.valueOf(dataSnapshot.child("endTime").getValue());
                        EventIncomingStruct events =
                                new EventIncomingStruct(name, status, attendees,
                                        startDate, startTime, endDate, endTime);
                        eventsCallback.onCallBack(events);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Handle error
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
            }
        });



    }



    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        calendarView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format(Locale.US,
                "Event of %02d:%02d %s/%d",
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                time.get(Calendar.MONTH)+1,
                time.get(Calendar.DAY_OF_MONTH));
    }

    private int[] getColors(String theme) {
        switch(theme) {
            case "MATERIAL":
                return getResources().getIntArray(R.array.material);
            case "BRIGHT":
                return getResources().getIntArray(R.array.bright);
            case "PASTEL":
                return getResources().getIntArray(R.array.pastel);
            default:
                return getResources().getIntArray(R.array.gradient);
        }
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity().getApplicationContext(), event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity().getApplicationContext(),  event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(getActivity().getApplicationContext(),
                getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return calendarView;
    }
}
