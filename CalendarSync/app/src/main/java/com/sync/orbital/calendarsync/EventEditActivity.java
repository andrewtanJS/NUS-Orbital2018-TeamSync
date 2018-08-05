package com.sync.orbital.calendarsync;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class EventEditActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mEventName, startDate, startTime, endDate, endTime;
    private int startYear, startMonth, startDay, startHour, startMinute,
            endYear, endMonth, endDay, endHour, endMinute;
    Button btnEdit, btnStartDate, btnEndDate, btnStartTime, btnEndTime;

    private String event_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        String event_name = getIntent().getStringExtra("event_name");
        String start_time = getIntent().getStringExtra("start_time");
        String start_date = getIntent().getStringExtra("start_date");
        String end_time = getIntent().getStringExtra("end_time");
        String end_date = getIntent().getStringExtra("end_date");
        event_id = getIntent().getStringExtra("event_id");

        btnEdit = findViewById(R.id.action_create_event);

        mEventName = findViewById(R.id.activity_name);
        btnStartDate = findViewById(R.id.btn_date_start);
        btnStartTime = findViewById(R.id.btn_time_start);
        btnEndDate = findViewById(R.id.btn_date_end);
        btnEndTime = findViewById(R.id.btn_time_end);

        startDate = findViewById(R.id.start_date);
        startTime = findViewById(R.id.start_time);
        endDate = findViewById(R.id.end_date);
        endTime = findViewById(R.id.end_time);

        mEventName.setText(event_name);
        startDate.setText(start_date);
        startTime.setText(start_time);
        endDate.setText(end_date);
        endTime.setText(end_time);

        // Initializing all fields
        String[] startTimeArr = start_time.split(":");
        String[] startDateArr = start_date.split("/");
        String[] endTimeArr = end_time.split(":");
        String[] endDateArr = end_date.split("/");

        startHour = Integer.parseInt(startTimeArr[0]);
        startMinute = Integer.parseInt(startTimeArr[1]);
        startYear = Integer.parseInt(startDateArr[0]);
        startMonth = Integer.parseInt(startDateArr[1]);
        startDay = Integer.parseInt(startDateArr[2]);

        endHour = Integer.parseInt(endTimeArr[0]);
        endMinute = Integer.parseInt(endTimeArr[1]);
        endYear = Integer.parseInt(endDateArr[0]);
        endMonth = Integer.parseInt(endDateArr[1]);
        endDay = Integer.parseInt(endDateArr[2]);

        

        btnEdit.setOnClickListener(this);
        btnStartDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnEndTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View item) {
        Calendar now = Calendar.getInstance();
        switch (item.getId()) {
            case R.id.action_create_event:
                editEvent();
                backToEventActivity();
                break;
            case R.id.btn_date_start:
                DatePickerDialog dateStartPickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                startYear = year;
                                startMonth = monthOfYear + 1;
                                startDay = dayOfMonth;
                            }
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                dateStartPickerDialog.updateDate(now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DATE));
                dateStartPickerDialog.show();
                break;
            case R.id.btn_time_start:
                TimePickerDialog timeStartPickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (minute < 10) {
                                    startTime.setText(hourOfDay + ":0" + minute);
                                } else {
                                    startTime.setText(hourOfDay + ":" + minute);
                                }
                                startHour = hourOfDay;
                                startMinute = minute;
                            }
                        }, startHour, startMinute, false);
                timeStartPickerDialog.show();
                break;
            case R.id.btn_date_end:
                DatePickerDialog dateEndPickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                endDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                endYear = year;
                                endMonth = monthOfYear + 1;
                                endDay = dayOfMonth;
                            }
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                dateEndPickerDialog.show();
                break;
            case R.id.btn_time_end:
                TimePickerDialog timeEndPickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (minute < 10) {
                                    endTime.setText(hourOfDay + ":0" + minute);
                                } else {
                                    endTime.setText(hourOfDay + ":" + minute);
                                }
                                endHour = hourOfDay;
                                endMinute = minute;
                            }
                        }, endHour, endMinute, false);
                timeEndPickerDialog.show();
                break;
            default:
                break;
        }
    }

    private void editEvent(){
        final String eventName = mEventName.getText().toString().trim();

        String startDateStr = String.format(Locale.US, "%02d/%02d/%04d",
                startDay,
                startMonth,
                startYear);
        String startTimeStr = String.format(Locale.US, "%02d:%02d",
                startHour,
                startMinute);
        String endDateStr = String.format(Locale.US, "%02d/%02d/%04d",
                endDay,
                endMonth,
                endYear);
        String endTimeStr = String.format(Locale.US, "%02d:%02d",
                endHour,
                endMinute);

        //Get Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUid = user.getUid();

        final DatabaseReference mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        //Event object to store information

        EventIncomingStruct eventNew =
                new EventIncomingStruct(eventName, "Going", "0/0",
                        startDateStr, startTimeStr,
                        endDateStr, endTimeStr);

        mEventsDatabase.child(event_id).setValue(eventNew);

    }

    private void backToEventActivity(){
        Intent intent = new Intent(EventEditActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
