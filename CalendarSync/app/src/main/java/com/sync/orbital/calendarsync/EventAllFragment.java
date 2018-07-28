package com.sync.orbital.calendarsync;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class EventAllFragment extends Fragment {

    private RecyclerView mEventsList;

    private DatabaseReference mEventsDatabase;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUid;

    private View view;

    public EventAllFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_all, container, false);

        mEventsList = (RecyclerView) view.findViewById(R.id.events_all_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUid = mAuth.getCurrentUser().getUid();

        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUid);

        mEventsList.setHasFixedSize(true);
        mEventsList.setLayoutManager(new LinearLayoutManager(getContext()));

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
                .child("Users")
                .child(mCurrentUid)
                .child("events")
                .limitToLast(50);

        FirebaseRecyclerOptions<EventReqStruct> options =
                new FirebaseRecyclerOptions.Builder<EventReqStruct>()
                        .setQuery(query, EventReqStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EventReqStruct, EventsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventsViewHolder holder, int position, @NonNull EventReqStruct model) {

                final String eventid = getRef(position).getKey();
//                Toast.makeText(getContext(), groupid, Toast.LENGTH_LONG).show();

                mEventsDatabase.child(eventid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String eventName = dataSnapshot.child("name").getValue().toString();
                        String startTime = dataSnapshot.child("startTime").getValue().toString();
                        String startDate = dataSnapshot.child("startDate").getValue().toString();
//                        String endTime = dataSnapshot.child("endTime").getValue().toString();
//                        String endDate = dataSnapshot.child("endDate").getValue().toString();

                        holder.setName(eventName);
                        holder.setStartDate(startDate);
                        holder.setStartTime(startTime);
//                        holder.setEndTime(endTime);
//                        holder.setEndDate(endDate);


                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_event = new Intent(getContext(), EventInfoActivity.class);
                                intent_event.putExtra("event_id", eventid);
                                startActivity(intent_event);
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
            public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_events, parent, false);

                return new EventsViewHolder(view);
            }

        };

        mEventsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        protected Button acceptButton, declineButton;

        public EventsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){
            TextView eventName = mView.findViewById(R.id.event_name);
            eventName.setText(name);

        }

        public void setStartTime(String start_time){
            TextView startTime = mView.findViewById(R.id.event_time);
            startTime.setText(start_time);

        }

        public void setStartDate(String start_date){
            TextView startDate = mView.findViewById(R.id._event_date);
            startDate.setText(start_date);

        }

 /*       public void setEndTime(String end_time){
            TextView endTime = mView.findViewById(R.id.event_req_end_time);
            endTime.setText(end_time);
        }

        public void setEndDate(String end_date){
            TextView endDate = mView.findViewById(R.id.event_req_end_date);
            endDate.setText(end_date);
        }*/

    }
}


