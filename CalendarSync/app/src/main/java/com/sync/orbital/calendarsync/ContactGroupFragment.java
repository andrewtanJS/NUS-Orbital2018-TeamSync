package com.sync.orbital.calendarsync;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
public class ContactGroupFragment extends Fragment {

    private FloatingActionButton mCreateBtn;

    private RecyclerView mGroupList;

    private DatabaseReference mGroupDatabase;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;

    private String mCurrentUid;



    public ContactGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_group, container, false);

        mGroupList = (RecyclerView) view.findViewById(R.id.group_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUid = mAuth.getCurrentUser().getUid();

        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUid).child("Groups");

        mGroupList.setHasFixedSize(true);
        mGroupList.setLayoutManager(new LinearLayoutManager(getContext()));


        mCreateBtn = (FloatingActionButton) view.findViewById(R.id.group_add_button);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_create = new Intent(getContext(), GroupCreateActivity.class);
                startActivity(intent_create);
            }
        });


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
                .child("groups")
                .limitToLast(50);

        FirebaseRecyclerOptions<GroupStruct> options =
                new FirebaseRecyclerOptions.Builder<GroupStruct>()
                        .setQuery(query, GroupStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupStruct, GroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupViewHolder holder, int position, @NonNull GroupStruct model) {
//                holder.setName(model.name);
//                holder.setThumbImage(model.thumb_image, getApplicationContext());

                final String groupid = getRef(position).getKey();
//                Toast.makeText(getContext(), groupid, Toast.LENGTH_LONG).show();

                mGroupDatabase.child(groupid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                    //    String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(userName);
                    //    holder.setThumbImage(userThumb, getContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_grp = new Intent(getContext(), GroupInfoActivity.class);
                                intent_grp.putExtra("group_id", groupid);
                                startActivity(intent_grp);
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
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_group, parent, false);

                return new GroupViewHolder(view);
            }

        };

        mGroupList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GroupViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.group_name);
            userNameView.setText(name);

        }

        public void setThumbImage(String thumbImage, Context context){
            CircularImageView userImageView = mView.findViewById(R.id.group_pic);
            Picasso.get().load(thumbImage).placeholder(R.drawable.default_user).into(userImageView);
        }


    }

}
