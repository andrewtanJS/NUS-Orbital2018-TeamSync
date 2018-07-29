package com.sync.orbital.calendarsync;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEventNameField, startDate, startTime, endDate, endTime;
    private int startYear, startMonth, startDay, startHour, startMinute,
                endYear, endMonth, endDay, endHour, endMinute;
    private boolean sD, sT, eD, eT;
    Button buttonCreate, btnStartDate, btnEndDate, btnStartTime, btnEndTime;

    private String group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Get group_id
        group_id = getIntent().getStringExtra("group_id");

        btnStartDate = findViewById(R.id.btn_date_start);
        btnStartTime = findViewById(R.id.btn_time_start);
        btnEndDate = findViewById(R.id.btn_date_end);
        btnEndTime = findViewById(R.id.btn_time_end);

        startDate = findViewById(R.id.start_date);
        startTime = findViewById(R.id.start_time);
        endDate = findViewById(R.id.end_date);
        endTime = findViewById(R.id.end_time);

        btnStartDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnEndTime.setOnClickListener(this);

        mEventNameField = findViewById(R.id.activity_name);
        buttonCreate = findViewById(R.id.action_create_event);
        buttonCreate.setOnClickListener(CreateActivity.this);

    }

    @Override
    public boolean onSupportNavigateUp(){
        backToEventActivity();
        return true;
    }

    @Override
    public void onClick(View item){
        Calendar now = Calendar.getInstance();
        switch (item.getId()) {
            case R.id.action_create_event:
                if (sT && sD && eT && eD) {
                    addEvent();
                    backToEventActivity();
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_date_start:
                DatePickerDialog dateStartPickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                sD = true;
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
                                sT = true;
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
                                eD = true;
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
                                eT = true;
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

    private void addEvent(){
        String eventName = mEventNameField.getText().toString().trim();

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
        //Write message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //Event object to store information

        EventIncomingStruct eventNew =
                new EventIncomingStruct(eventName, "Going", "0/0",
                        startDateStr, startTimeStr,
                        endDateStr, endTimeStr);

        String eventId = mDatabase.push().getKey();
        mDatabase.child("users").child(user.getUid())
                .child("events").child(eventId).setValue(eventNew);

        Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
    }

    private void addEventGroup(){
        final String eventName = mEventNameField.getText().toString().trim();

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
        final DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("members");
        final DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference mEventReqDatabase = FirebaseDatabase.getInstance().getReference().child("Event_req");

        //Event object to store information

        EventIncomingStruct eventNew =
                new EventIncomingStruct(eventName, "Going", "0/0",
                        startDateStr, startTimeStr,
                        endDateStr, endTimeStr);

        final String eventId = mEventsDatabase.push().getKey();
        mEventsDatabase.child(eventId).setValue(eventNew);
        mEventsDatabase.child(eventId).child("members").child(currentUid).child("uid").setValue(currentUid);
        mUsersDatabase.child(currentUid).child("events").child(eventId).child("name").setValue(eventName);

        mGroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user_id = snapshot.child("uid").getValue().toString();
//                    mEventsDatabase.child(eventId).child("members").child(user_id).child("uid").setValue(user_id);
//                    mUsersDatabase.child(user_id).child("events").child(eventId).child("name").setValue(eventName);
                    if (!user_id.equals(currentUid)){
                        mEventReqDatabase.child(user_id).child(eventId).child("sent_by").setValue(currentUid);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void backToEventActivity(){
        Intent intent = new Intent(CreateActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
