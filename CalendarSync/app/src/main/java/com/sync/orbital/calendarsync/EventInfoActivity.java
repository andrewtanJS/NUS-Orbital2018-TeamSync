package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class EventInfoActivity extends AppCompatActivity {

    private TextView mEventName;
    private TextView mEventDescription;
    private TextView mStartDate, mStartTime, mEndDate, mEndTime;

    private Button mEditBtn;
    private Button mDeleteBtn;

    private RecyclerView mMemberList;

    private Toolbar mToolbar;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mEventsDatabase;
    private DatabaseReference mAllUsersDatabase;

    private FirebaseAuth mAuth;

    private String event_id;

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

        event_id = getIntent().getStringExtra("event_id");
        final String user_id = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mAllUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(event_id);

        mEventName = (TextView) findViewById(R.id.event_info_name);
        mEventDescription = (TextView) findViewById(R.id.event_info_description);
        mStartDate = (TextView) findViewById(R.id.event_info_start_date);
        mStartTime = (TextView) findViewById(R.id.event_info_start_time);
        mEndDate = (TextView) findViewById(R.id.event_info_end_date);
        mEndTime = (TextView) findViewById(R.id.event_info_end_time);

        mMemberList = (RecyclerView) findViewById(R.id.event_info_member_list);
        mMemberList.setHasFixedSize(true);
        mMemberList.setLayoutManager(new LinearLayoutManager(this));


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

    // Member list

    @Override
    public void onStart() {
        super.onStart();
        startListening();
    }

    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Events")
                .child(event_id)
                .child("members")
                .limitToLast(50);

        FirebaseRecyclerOptions<ContactsAllStruct> options =
                new FirebaseRecyclerOptions.Builder<ContactsAllStruct>()
                        .setQuery(query, ContactsAllStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ContactsAllStruct, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull ContactsAllStruct model) {
//                holder.setName(model.name);
//                holder.setThumbImage(model.thumb_image, getApplicationContext());

                final String userid = getRef(position).getKey();

                mAllUsersDatabase.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(userName);
                        holder.setThumbImage(userThumb, getApplicationContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_prof = new Intent(EventInfoActivity.this, UsersProfileActivity.class);
                                intent_prof.putExtra("user_id", userid);
                                startActivity(intent_prof);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_contact_small, parent, false);

                return new FriendsViewHolder(view);
            }

        };

        mMemberList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.name_contacts_all);
            userNameView.setText(name);

        }

        public void setThumbImage(String thumbImage, Context context){
            CircularImageView userImageView = mView.findViewById(R.id.profile_pic_contacts);
            Picasso.get().load(thumbImage).placeholder(R.drawable.default_user).into(userImageView);
        }


    }
}
