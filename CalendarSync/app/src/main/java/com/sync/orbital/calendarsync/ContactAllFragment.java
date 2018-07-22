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
public class ContactAllFragment extends Fragment {


    public ContactAllFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_all, container, false);

        ArrayList<ContactsAllStruct> contactList;

        RecyclerView recyclerViewIncoming = (RecyclerView) view.findViewById(R.id.recycler_contacts_all);
        recyclerViewIncoming.setHasFixedSize(true);

        //Event info
        contactList = new ArrayList<>();
//        contactList.add(new ContactsAllStruct(R.drawable.baby_dory, "Ellen"));
//        contactList.add(new ContactsAllStruct(R.drawable.elon_musk, "Mark Zuckerberg"));
//        contactList.add(new ContactsAllStruct(R.drawable.eminem, "Marshall"));
//        contactList.add(new ContactsAllStruct(R.drawable.messi, "Miss Penalty"));

        //linear layout manager
        RecyclerView.LayoutManager layoutManagerIncoming = new LinearLayoutManager(this.getActivity());
        recyclerViewIncoming.setLayoutManager(layoutManagerIncoming);

        //specify adapter
 //       ListContactAllAdapter adapterIncoming = new ListContactAllAdapter(getActivity(), contactList);
  //      recyclerViewIncoming.setAdapter(adapterIncoming);


        return view;
    }

}
