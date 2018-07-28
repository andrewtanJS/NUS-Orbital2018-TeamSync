package com.sync.orbital.calendarsync;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventRequestFragment extends Fragment {

    private RecyclerView mEventsRequestList;

    private DatabaseReference mEventsReqDatabase;
    private DatabaseReference mEventsDatabase;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUid;

    private View view;

    public EventRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_request, container, false);

        mEventsRequestList = (RecyclerView) view.findViewById(R.id.events_request_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUid = mAuth.getCurrentUser().getUid();

        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUid);
        mEventsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Event_req").child(mCurrentUid);

        mEventsRequestList.setHasFixedSize(true);
        mEventsRequestList.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();
    }

    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Event_req")
                .child(mCurrentUid)
                .limitToLast(50);

        FirebaseRecyclerOptions<EventReqStruct> options =
                new FirebaseRecyclerOptions.Builder<EventReqStruct>()
                        .setQuery(query, EventReqStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EventReqStruct, EventRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventRequestViewHolder holder, int position, @NonNull EventReqStruct model) {
//                holder.setName(model.name);
//                holder.setThumbImage(model.thumb_image, getApplicationContext());

                final String eventid = getRef(position).getKey();
//                Toast.makeText(getContext(), groupid, Toast.LENGTH_LONG).show();

                mEventsDatabase.child(eventid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String eventName = dataSnapshot.child("name").getValue().toString();
                        String startTime = dataSnapshot.child("startTime").getValue().toString();
                        String startDate = dataSnapshot.child("startDate").getValue().toString();
                        String endTime = dataSnapshot.child("endTime").getValue().toString();
                        String endDate = dataSnapshot.child("endDate").getValue().toString();

                        holder.setEventName(eventName);
                        holder.setStartDate(startDate);
                        holder.setStartTime(startTime);
                        holder.setEndTime(endTime);
                        holder.setEndDate(endDate);

                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEventsReqDatabase.child(eventid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mUserDatabase.child("events").child(eventid).child("name").setValue(eventName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mEventsDatabase.child(eventid).child("members").child(mCurrentUid).child("uid").setValue(mCurrentUid);
                                                Toast.makeText(getContext(), "Accept", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });


                            }
                        });

                        holder.declineButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEventsReqDatabase.child(eventid).removeValue() .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Decline", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        //    String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

          /*              holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_grp = new Intent(getContext(), GroupInfoActivity.class);
                                intent_grp.putExtra("event_id", eventid);
                                startActivity(intent_grp);
                            }
                        });*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public EventRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_event_request, parent, false);

                return new EventRequestViewHolder(view);
            }

        };

        mEventsRequestList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class EventRequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        protected Button acceptButton, declineButton;

        public EventRequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            acceptButton = (Button) mView.findViewById(R.id.event_req_accept);
            declineButton = (Button) mView.findViewById(R.id.event_req_decline);

        }

        public void setEventName(String name){
            TextView eventName = mView.findViewById(R.id.event_req_name);
            eventName.setText(name);

        }

        public void setStartTime(String start_time){
            TextView startTime = mView.findViewById(R.id.event_req_start_time);
            startTime.setText(start_time);

        }

        public void setStartDate(String start_date){
            TextView startDate = mView.findViewById(R.id.event_req_start_date);
            startDate.setText(start_date);

        }

        public void setEndTime(String end_time){
            TextView endTime = mView.findViewById(R.id.event_req_end_time);
            endTime.setText(end_time);
        }

        public void setEndDate(String end_date){
            TextView endDate = mView.findViewById(R.id.event_req_end_date);
            endDate.setText(end_date);
        }

    }
}
