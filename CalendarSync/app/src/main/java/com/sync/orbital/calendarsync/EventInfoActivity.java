package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventInfoActivity extends AppCompatActivity {

    private TextView mEventName;
    private TextView mEventDescription;
    private TextView mStartDate, mStartTime, mEndDate, mEndTime;

    private Button mEditBtn;
    private Button mDeleteBtn;

    private Toolbar mToolbar;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mEventsDatabase;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    private int mCurrentState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        mToolbar = (Toolbar) findViewById(R.id.event_info_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Event Info");

        mAuth = FirebaseAuth.getInstance();

        final String event_id = getIntent().getStringExtra("event_id");
        final String user_id = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(event_id);

        mEventName = (TextView) findViewById(R.id.event_info_name);
        mEventDescription = (TextView) findViewById(R.id.event_info_description);
        mStartDate = (TextView) findViewById(R.id.event_info_start_date);
        mStartTime = (TextView) findViewById(R.id.event_info_start_time);
        mEndDate = (TextView) findViewById(R.id.event_info_end_date);
        mEndTime = (TextView) findViewById(R.id.event_info_end_time);


        mEventsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String eventName = dataSnapshot.child("name").getValue().toString();
                String startDate = dataSnapshot.child("startDate").getValue().toString();
                String startTime = dataSnapshot.child("startTime").getValue().toString();
                String endDate = dataSnapshot.child("endDate").getValue().toString();
                String endTime = dataSnapshot.child("endTime").getValue().toString();

                mEventName.setText(eventName);
                mStartDate.setText(startDate);
                mStartTime.setText(startTime);
                mEndDate.setText(endDate);
                mEndTime.setText(endTime);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mEditBtn = (Button) findViewById(R.id.event_info_edit_btn);
        mDeleteBtn = (Button) findViewById(R.id.event_info_delete_btn);

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(EventInfoActivity.this, EventEditActivity.class);
                String eventName = mEventName.getText().toString();
                String startTime = mStartTime.getText().toString();
                String startDate = mStartDate.getText().toString();
                String endTime = mEndTime.getText().toString();
                String endDate = mEndDate.getText().toString();

                intent_edit.putExtra("event_name", eventName);
                intent_edit.putExtra("start_time", startTime);
                intent_edit.putExtra("start_date", startDate);
                intent_edit.putExtra("end_time", endTime);
                intent_edit.putExtra("end_date", endDate);
                intent_edit.putExtra("event_id", event_id);
                startActivity(intent_edit);
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersDatabase.child("events").child(event_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mEventsDatabase.child("members").child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EventInfoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    backToEventActivity();

                                    //need to delete event if no more member
                                }
                            });
                        }
                        else {
                            Toast.makeText(EventInfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//    }

    private void backToEventActivity(){
        Intent intent = new Intent(EventInfoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
