package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GroupInfoActivity extends AppCompatActivity {

    private ImageView mGroupImage;
    private TextView mGroupName;
    private Button mGroupCreateButton;
    private Toolbar mToolbar;

    private DatabaseReference mGroupDatabase;
    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        final String group_id = getIntent().getStringExtra("group_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);

        mToolbar = (Toolbar) findViewById(R.id.group_info_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Groups");

        mGroupImage = (ImageView) findViewById(R.id.group_info_pic);
        mGroupName = (TextView) findViewById(R.id.group_info_name);
        mGroupCreateButton = (Button) findViewById(R.id.group_info_create_button);

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
}
