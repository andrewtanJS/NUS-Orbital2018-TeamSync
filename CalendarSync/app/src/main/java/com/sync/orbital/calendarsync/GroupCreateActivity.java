package com.sync.orbital.calendarsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class GroupCreateActivity extends AppCompatActivity {

    private EditText mGroupName;
    private Button mCreateBtn;
    private ImageButton mImageButton;

    private Toolbar mToolbar;
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mGroupDatabase;

    //Storage Firebase
    private StorageReference mStorageRef;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    private String mCurrentUid;
    private static final int PIC_PICK = 1;

    private HashSet<String> set =new HashSet<String>();

    //For group picture
    private Uri resultUri;
    private byte[] thumb_byte;
    private boolean hasPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        mToolbar = (Toolbar) findViewById(R.id.group_create_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Group");


        mAuth = FirebaseAuth.getInstance();
        mCurrentUid = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUid);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mGroupDatabase = FirebaseDatabase.getInstance().getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mFriendsList = (RecyclerView) findViewById(R.id.group_create_friends_list);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(this));

        mGroupName = (EditText) findViewById(R.id.group_create_name);
        mCreateBtn = (Button) findViewById(R.id.group_create_button);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mDialog = new ProgressDialog(GroupCreateActivity.this);
                mDialog.setTitle("Creating...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();


                final String groupId = mGroupDatabase.push().getKey();
                String groupName = mGroupName.getText().toString();
                mGroupDatabase.child("Groups").child(groupId).child("name").setValue(groupName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                mUsersDatabase.child(mCurrentUid).child("groups").child(groupId).child("name").setValue(groupName);
                mGroupDatabase.child("Groups").child(groupId).child("members").child(mCurrentUid).child("uid").setValue(mCurrentUid).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

                if (!set.isEmpty()){
                    for (final String uid: set){
                        mGroupDatabase.child("Groups").child(groupId).child("members").child(uid).child("uid").setValue(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        mUsersDatabase.child(uid).child("groups").child(groupId).child("name").setValue(groupName);
                    }

                    int groupSize = set.size() + 1;

                    mGroupDatabase.child("Groups").child(groupId).child("size").setValue(groupSize);

                }

                //Upload picture to firebase

                mGroupDatabase.child("Groups").child(groupId).child("image").setValue("default");
                mGroupDatabase.child("Groups").child(groupId).child("thumb_image").setValue("default");

                if (hasPicture) {
                    StorageReference filepath = mStorageRef.child("group_pictures").child(groupId + ".jpg");
                    final StorageReference thumbFilepath = mStorageRef.child("group_pictures").child("thumbs").child(groupId + ".jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                mStorageRef.child("group_pictures").child(groupId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final String downloadUrl = uri.toString();

                                        UploadTask uploadTask = thumbFilepath.putBytes(thumb_byte);
                                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                                if (thumb_task.isSuccessful()) {
                                                    mStorageRef.child("group_pictures").child("thumbs").child(groupId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri thumb_uri) {
                                                            final String thumbDownloadUrl = thumb_uri.toString();
                                                            mGroupDatabase.child("Groups").child(groupId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        mGroupDatabase.child("Groups").child(groupId).child("thumb_image").setValue(thumbDownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    mDialog.dismiss();
                                                                                    Toast.makeText(getApplicationContext(), "Successful.", Toast.LENGTH_LONG).show();
                                                                                    backToMainActivity();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    });
                                                }

                                            }
                                        });

                                    }
                                });


                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    mGroupDatabase.child("Groups").child(groupId).child("image").setValue("default").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mGroupDatabase.child("Groups").child(groupId).child("thumb_image").setValue("default").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Successful.", Toast.LENGTH_LONG).show();
                                    backToMainActivity();
                                }
                            });
                        }
                    });

                }
            }
        });

        mImageButton = (ImageButton) findViewById(R.id.group_create_pic);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pic_intent = new Intent();
                pic_intent.setType("image/*");
                pic_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pic_intent, "Select Image"), PIC_PICK);
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
                .child("Friends")
                .child(mCurrentUid)
                .limitToLast(50);

        FirebaseRecyclerOptions<ContactsCheckStruct> options =
                new FirebaseRecyclerOptions.Builder<ContactsCheckStruct>()
                        .setQuery(query, ContactsCheckStruct.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ContactsCheckStruct, FriendsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull ContactsCheckStruct model) {
//                holder.setName(model.name);
//                holder.setThumbImage(model.thumb_image, getApplicationContext());

                final String userid = getRef(position).getKey();


                holder.check_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (set.contains(userid)){
                            set.remove(userid);
                            Toast.makeText(GroupCreateActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            set.add(userid);
                            Toast.makeText(GroupCreateActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                mUsersDatabase.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(userName);
                        holder.setThumbImage(userThumb, getApplicationContext());

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
                        .inflate(R.layout.list_contact_check, parent, false);

                return new FriendsViewHolder(view);
            }


        };

        mFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        protected CheckBox check_box;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            check_box = mView.findViewById(R.id.contact_check);

        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.contact_check_name);
            userNameView.setText(name);

        }

        public void setThumbImage(String thumbImage, Context context){
            CircularImageView userImageView = mView.findViewById(R.id.contact_check_pic);
            Picasso.get().load(thumbImage).placeholder(R.drawable.default_user).into(userImageView);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIC_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mDialog = new ProgressDialog(GroupCreateActivity.this);
                mDialog.setTitle("Cropping...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());


                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                thumb_byte = baos.toByteArray();

                hasPicture = true;

                mImageButton.setImageResource(R.drawable.ic_group_green_24dp);

                Toast.makeText(getApplicationContext(),"Image Chosen", Toast.LENGTH_LONG).show();
                mDialog.dismiss();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void backToMainActivity(){
        Intent intent = new Intent(GroupCreateActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
