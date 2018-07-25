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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
public class ContactRequestFragment extends Fragment {


    private RecyclerView mFriendsReqList;

    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUid;

    private View view;

    public ContactRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_request, container, false);

        mFriendsReqList = (RecyclerView) view.findViewById(R.id.friends_request_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUid = mAuth.getCurrentUser().getUid();

        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUid);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsReqList.setHasFixedSize(true);
        mFriendsReqList.setLayoutManager(new LinearLayoutManager(getContext()));

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
                .child("Friend_req")
                .child(mCurrentUid)
                .limitToLast(50);

        FirebaseRecyclerOptions<ContactsAllStruct> options =
                new FirebaseRecyclerOptions.Builder<ContactsAllStruct>()
                        .setQuery(query, ContactsAllStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ContactsAllStruct, FriendsReqViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsReqViewHolder holder, int position, @NonNull ContactsAllStruct model) {
//                holder.setName(model.name);
//                holder.setThumbImage(model.thumb_image, getApplicationContext());
                final String userid = getRef(position).getKey();

                mUsersDatabase.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName  = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(userName);
                        holder.setThumbImage(userThumb, getContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_prof = new Intent(getContext(), UsersProfileActivity.class);
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
            public FriendsReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_contacts, parent, false);

                return new FriendsReqViewHolder(view);
            }

        };

        mFriendsReqList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FriendsReqViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsReqViewHolder(View itemView) {
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



