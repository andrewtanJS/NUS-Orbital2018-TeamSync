package com.sync.orbital.calendarsync;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Time;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEventNameField;
    private EditText mDateField;
    private EditText mStartTimeField;
    private EditText mEndTimeField;
    Button buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mEventNameField = findViewById(R.id.activity_name);
        mDateField = findViewById(R.id.action_pick_date);
        mStartTimeField = findViewById(R.id.action_pick_start_time);
        mEndTimeField = findViewById(R.id.action_pick_end_time);
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
        switch (item.getId()){
            case R.id.action_create_event:
                addEvent();
                backToEventActivity();
        }
    }

    private void addEvent(){
        String eventName = mEventNameField.getText().toString().trim();
        String date = mDateField.getText().toString().trim();
        String startTime= mStartTimeField.getText().toString().trim();
        String endTime= mEndTimeField.getText().toString().trim();

        //Get Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Write message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //Event object to store information

        EventIncomingStruct eventNew =
                new EventIncomingStruct(eventName, "Going", "0/0", startTime, endTime, date);

        String eventId = mDatabase.push().getKey();
        mDatabase.child("users").child(user.getUid())
                .child("events").child(eventId).setValue(eventNew);

        Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
    }

    private void backToEventActivity(){
        Intent intent = new Intent(CreateActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
