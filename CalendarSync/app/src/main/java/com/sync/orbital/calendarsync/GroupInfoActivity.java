package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class GroupInfoActivity extends AppCompatActivity {

    private ImageView mGroupImage;
    private TextView mGroupName;
    private Button mGroupCreateButton;
    private Toolbar mToolbar;
    private RecyclerView mMemberList;

    private DatabaseReference mGroupDatabase;
    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private String group_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        group_id = getIntent().getStringExtra("group_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);

        mToolbar = (Toolbar) findViewById(R.id.group_info_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Groups");

        mGroupImage = (ImageView) findViewById(R.id.group_info_pic);
        mGroupName = (TextView) findViewById(R.id.group_info_name);
        mGroupCreateButton = (Button) findViewById(R.id.group_info_create_button);

        mMemberList = (RecyclerView) findViewById(R.id.group_info_member_list);
        mMemberList.setHasFixedSize(true);
        mMemberList.setLayoutManager(new LinearLayoutManager(this));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mGroupDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mGroupName.setText(name);
                if(!image.equals("default")) {
                    Picasso.get().load(image).into(mGroupImage);
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mGroupCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_grp_create = new Intent(GroupInfoActivity.this, CreateGroupActivity.class);
                intent_grp_create.putExtra("group_id", group_id);
                startActivity(intent_grp_create);
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();
    }

    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Groups")
                .child(group_id)
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

                mUsersDatabase.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(userName);
                        holder.setThumbImage(userThumb, getApplicationContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_prof = new Intent(GroupInfoActivity.this, UsersProfileActivity.class);
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
                        .inflate(R.layout.list_contacts, parent, false);

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
