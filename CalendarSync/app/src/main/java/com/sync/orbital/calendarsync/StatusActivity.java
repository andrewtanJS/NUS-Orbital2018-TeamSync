package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private EditText mStatus;
    private Button mButton;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_val = getIntent().getStringExtra("status");

        mStatus = (EditText) findViewById(R.id.account_status);
        mButton = (Button) findViewById(R.id.save_changes_button);

        mStatus.setText(status_val);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Progress
                mProgess = new ProgressDialog(StatusActivity.this);
                mProgess.setTitle("Saving changes");

                String status = mStatus.getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgess.dismiss();
                            Toast.makeText(getApplicationContext(),"Saved.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Error.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }
}
