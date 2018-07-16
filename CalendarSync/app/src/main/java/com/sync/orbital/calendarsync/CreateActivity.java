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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEventNameField;
    private EditText mDateField;
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
        String dateTime = mDateField.getText().toString().trim();

        //Get Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Write message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("events");
        //Event object to store information

        EventIncomingStruct eventNew = new EventIncomingStruct(eventName, "Going", "0/0", "8.00pm", "2 July");

        String eventId = mDatabase.push().getKey();
        mDatabase.child(eventId).setValue(eventNew);

        Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
    }

    private void backToEventActivity(){
        Intent intent = new Intent(CreateActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
