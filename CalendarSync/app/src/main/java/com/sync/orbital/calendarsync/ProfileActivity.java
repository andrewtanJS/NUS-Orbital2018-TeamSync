package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircularImageView mProfilePicture;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusButton;
    private Button mPicButton;

    private static final int PIC_PICK = 1;

    //Storage Firebase
    private StorageReference mStorageRef;

    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfilePicture = (CircularImageView) findViewById(R.id.settings_profile_pic);
        mName = (TextView) findViewById(R.id.settings_user_name);
        mStatus = (TextView) findViewById(R.id.settings_profile_status);

        mStatusButton = (Button) findViewById(R.id.settings_status_button);
        mPicButton = (Button) findViewById(R.id.settings_picture_button);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();


        String currentUid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(image).into(mProfilePicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent status_intent = new Intent(ProfileActivity.this, StatusActivity.class);
                String status = mStatus.getText().toString();
                status_intent.putExtra("status", status);
                startActivity(status_intent);
            }
        });

        mPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent pic_intent = new Intent();
                pic_intent.setType("image/*");
                pic_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pic_intent, "Select Image"), PIC_PICK);


              /*  CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PIC_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mDialog = new ProgressDialog(ProfileActivity.this);
                mDialog.setTitle("Uploading...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                Uri resultUri = result.getUri();
                final String currentUid = mCurrentUser.getUid();

                StorageReference filepath = mStorageRef.child("profile_pictures").child(currentUid + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            mStorageRef.child("profile_pictures").child(currentUid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();

                                    mUserDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                mDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Success.", Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });

                                }
                            });

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Error.", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
