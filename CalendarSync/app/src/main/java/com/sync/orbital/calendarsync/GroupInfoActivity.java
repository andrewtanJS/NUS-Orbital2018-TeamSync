package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupInfoActivity extends AppCompatActivity {

    private ImageView mGroupImage;
    private TextView mGroupName;
    private Button mGroupCreateButton;

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

        mGroupImage = (ImageView) findViewById(R.id.group_create_pic);
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

                mGroupName.setText(name);

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
