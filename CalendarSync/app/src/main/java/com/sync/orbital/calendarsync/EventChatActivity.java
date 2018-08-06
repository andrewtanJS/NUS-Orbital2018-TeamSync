package com.sync.orbital.calendarsync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.mortbay.jetty.Server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventChatActivity extends AppCompatActivity {

    private ImageButton mSendButton;
    private EditText mMessageText;
    private RecyclerView mMessageList;

    private Toolbar mToolbar;

    private DatabaseReference mEventDatabase;
    private DatabaseReference mRootDatabase;
    private FirebaseAuth mAuth;

    private String mCurrentUid;
    private String mUsername;
    private String mEventId;
    private String mEventName;

    private final List<Message> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chat);

        mEventId = getIntent().getStringExtra("event_id");
        mEventName = getIntent().getStringExtra("event_name");


        mToolbar = (Toolbar) findViewById(R.id.event_chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mEventName);


        mRootDatabase = FirebaseDatabase.getInstance().getReference();
        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(mEventId);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUid = mAuth.getCurrentUser().getUid();

        //Create chat node
        mRootDatabase.child("Chats_events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mEventId)){
                    mRootDatabase.child("Chats_events").child(mEventId).child("name").setValue(mEventName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Find user name
        mRootDatabase.child("Users").child(mCurrentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsername = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mMessageText = (EditText) findViewById(R.id.event_message_text);
        mSendButton = (ImageButton) findViewById(R.id.event_message_send_btn);
        mMessageList = (RecyclerView) findViewById(R.id.event_message_list);
        mLinearLayout = new LinearLayoutManager(this);

        mAdapter = new MessageAdapter(messagesList);

        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mAdapter);
        
        loadMessages();


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageText.getText().toString();
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                String time = s.format(new Date());

                if(!TextUtils.isEmpty(message)){
                    mRootDatabase.child("Chats_events")
                            .child(mEventId)
                            .child("messages")
                            .push()
                            .setValue(new Message(message, mUsername, mCurrentUid, time))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mMessageText.setText("");
                                }
                            });
                }

            }
        });
    }

    private void loadMessages() {
        mRootDatabase.child("Chats_events").child(mEventId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Message message = dataSnapshot.getValue(Message.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(messagesList.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
