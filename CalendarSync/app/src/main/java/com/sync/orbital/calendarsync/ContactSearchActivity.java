package com.sync.orbital.calendarsync;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ContactSearchActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView mResultList;
    private Toolbar mToolbar;

    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    private String mCurrentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);

        mToolbar = (Toolbar) findViewById(R.id.contacts_search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mSearchField = (EditText) findViewById(R.id.search_name);
        mSearchBtn = (ImageButton) findViewById(R.id.search_name_button);

        mResultList = (RecyclerView) findViewById(R.id.search_name_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        mCurrentUid = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);
            }
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void firebaseUserSearch(String searchText) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .orderByChild("name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .limitToLast(50);

        FirebaseRecyclerOptions<ContactsAllStruct> options =
                new FirebaseRecyclerOptions.Builder<ContactsAllStruct>()
                        .setQuery(query, ContactsAllStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ContactsAllStruct, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull ContactsAllStruct model) {
                holder.setName(model.name);
                holder.setThumbImage(model.thumb_image, getApplicationContext());

                final String userid = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_prof = new Intent(ContactSearchActivity.this, UsersProfileActivity.class);
                        intent_prof.putExtra("user_id", userid);
                        startActivity(intent_prof);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_contacts, parent, false);

                return new UsersViewHolder(view);
            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
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
