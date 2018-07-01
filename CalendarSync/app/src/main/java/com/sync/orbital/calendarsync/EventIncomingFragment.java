package com.sync.orbital.calendarsync;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventIncomingFragment extends Fragment {


    public EventIncomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_incoming, container, false);

        ArrayList<EventIncomingStruct> eventList;

        RecyclerView recyclerViewIncoming = (RecyclerView) view.findViewById(R.id.recycler_incoming);
        recyclerViewIncoming.setHasFixedSize(true);

        //Event info
        eventList = new ArrayList<>();
        eventList.add(new EventIncomingStruct("OG Meetup", "Going", "27/39", "8.00pm", "2 July"));
        eventList.add(new EventIncomingStruct("Orbital Meeting", "Not Going", "2/14", "11.30am", "3 July"));
        eventList.add(new EventIncomingStruct("Dinner Date", "Going", "2/2", "5.00pm", "3 July"));
        eventList.add(new EventIncomingStruct("Antman Movie", "Going", "5/7", "9.15pm", "9 July"));
        eventList.add(new EventIncomingStruct("Department Meeting", "Going", "46/70", "10.00am", "11 July"));
        eventList.add(new EventIncomingStruct("Dad's Birthday", "Going", "4/5", "12.00pm", "23 July"));

        //linear layout manager
        RecyclerView.LayoutManager layoutManagerIncoming = new LinearLayoutManager(this.getActivity());
        recyclerViewIncoming.setLayoutManager(layoutManagerIncoming);

        //specify adapter
        ListAdapter adapterIncoming = new ListAdapter(getActivity(), eventList);
        recyclerViewIncoming.setAdapter(adapterIncoming);


        return view;
    }

}
