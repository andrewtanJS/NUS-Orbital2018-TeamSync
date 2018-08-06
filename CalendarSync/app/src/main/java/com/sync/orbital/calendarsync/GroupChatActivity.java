package com.sync.orbital.calendarsync;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupChatActivity extends AppCompatActivity {

    private ImageButton mSendButton;
    private EditText mMessageText;

    private Toolbar mToolbar;

    private DatabaseReference mGroupDatabase;

    private String mGroupId;
    private String mGroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mGroupId = getIntent().getStringExtra("group_id");
        mGroupName = getIntent().getStringExtra("group_name");


        mToolbar = (Toolbar) findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mGroupName);

        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(mGroupId);


        mMessageText = (EditText) findViewById(R.id.group_message_text);
        mSendButton = (ImageButton) findViewById(R.id.group_message_send_btn);


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageText.getText().toString();

            }
        });

    }
}
