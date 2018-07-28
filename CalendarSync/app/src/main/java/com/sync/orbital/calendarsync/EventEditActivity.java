package com.sync.orbital.calendarsync;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EventEditActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEventNameField, startDate, startTime, endDate, endTime;
    private int startYear, startMonth, startDay, startHour, startMinute,
            endYear, endMonth, endDay, endHour, endMinute;
    private boolean sD, sT, eD, eT;
    Button buttonEdit, btnStartDate, btnEndDate, btnStartTime, btnEndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        String event_name = getIntent().getStringExtra("event_name");
        String start_time = getIntent().getStringExtra("start_time");
        String start_date = getIntent().getStringExtra("start_date");
        String end_time = getIntent().getStringExtra("end_time");
        String end_date = getIntent().getStringExtra("end_date");
        String event_id = getIntent().getStringExtra("event_id");

        buttonEdit = findViewById(R.id.action_edit_event);

        btnStartDate = findViewById(R.id.btn_date_start_edit);
        btnStartTime = findViewById(R.id.btn_time_start_edit);
        btnEndDate = findViewById(R.id.btn_date_end_edit);
        btnEndTime = findViewById(R.id.btn_time_end_edit);

        startDate = findViewById(R.id.start_date_edit);
        startTime = findViewById(R.id.start_time_edit);
        endDate = findViewById(R.id.end_date_edit);
        endTime = findViewById(R.id.end_time_edit);

        startDate.setText(start_date);
        startTime.setText(start_time);
        endDate.setText(end_date);
        endTime.setText(end_time);


        btnStartDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnEndTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
