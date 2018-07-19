package com.sync.orbital.calendarsync;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventIncomingFragment extends Fragment {


    public EventIncomingFragment() {
        // Required empty public constructor
    }

    private ArrayList<EventIncomingStruct> eventList;
    private ListAdapter adapterIncoming;
    private RecyclerView recyclerViewIncoming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_incoming, container, false);

        recyclerViewIncoming = (RecyclerView) view.findViewById(R.id.recycler_incoming);
        recyclerViewIncoming.setHasFixedSize(true);

        //Event info
        eventList = new ArrayList<>();

        //linear layout manager
        RecyclerView.LayoutManager layoutManagerIncoming = new LinearLayoutManager(this.getActivity());
        recyclerViewIncoming.setLayoutManager(layoutManagerIncoming);

        //specify adapter
        adapterIncoming = new ListAdapter(getActivity(), eventList);
        recyclerViewIncoming.setAdapter(adapterIncoming);

        getFirebaseData(new EventsCallback(){
           @Override
           public void onCallBack(EventIncomingStruct event){
               eventList.add(event);
               adapterIncoming.notifyDataSetChanged();

           }
        });

        return view;
    }

    private void getFirebaseData(final EventsCallback eventsCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = reference.child("users")
                                                .child(user.getUid())
                                                .child("events");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Result will be holded Here
                for (DataSnapshot dataSnap: dataSnapshot.getChildren()){

                    String name = String.valueOf(dataSnap.child("name").getValue());
                    String status = String.valueOf(dataSnap.child("status").getValue());
                    String attendees = String.valueOf(dataSnap.child("attendees").getValue());
                    String startTime = String.valueOf(dataSnap.child("startTime").getValue());
                    String endTime = String.valueOf(dataSnap.child("endTime").getValue());
                    String date = String.valueOf(dataSnap.child("date").getValue());
                    EventIncomingStruct events =
                            new EventIncomingStruct(name, status, attendees, startTime, endTime, date);
                    eventsCallback.onCallBack(events);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Handle error
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

}
