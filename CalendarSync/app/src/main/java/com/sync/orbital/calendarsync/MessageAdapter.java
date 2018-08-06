package com.sync.orbital.calendarsync;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private final int USER=0, NONUSER=1;

    public MessageAdapter(List<Message> mMessageList){
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType){
            case USER:
                View v1 = inflater.inflate(R.layout.my_message, parent, false);
                viewHolder = new MessageViewHolder1(v1);
                break;
            case NONUSER:
                View v2 = inflater.inflate(R.layout.their_message, parent, false);
                viewHolder = new MessageViewHolder2(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.their_message, parent, false);
                viewHolder = new MessageViewHolder2(v3);
                break;
        }
        return viewHolder;


    }

    //USER
    public class MessageViewHolder1 extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView messageTime;
        public TextView messageName;

        public MessageViewHolder1(View view){
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_body);
            messageTime = (TextView) view.findViewById(R.id.message_time);
//            messageName = (TextView) view.findViewById(R.id.message_name);
        }
    }

    //NON USER
    public class MessageViewHolder2 extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView messageTime;
        public TextView messageName;
        public CircularImageView messagePicture;

        public MessageViewHolder2(View view){
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_body);
            messageTime = (TextView) view.findViewById(R.id.message_time);
            messageName = (TextView) view.findViewById(R.id.message_name);
//            messagePicture = (CircularImageView) view.findViewById(R.id.message_avatar);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case USER:
                MessageViewHolder1 vh1 = (MessageViewHolder1) holder;
                configureViewHolder1(vh1, position);
                break;
            case NONUSER:
                MessageViewHolder2 vh2 = (MessageViewHolder2) holder;
                configureViewHolder2(vh2, position);
                break;
            default:
                MessageViewHolder2 vh3 = (MessageViewHolder2) holder;
                configureViewHolder2(vh3, position);
                break;
        }


    }

    private void configureViewHolder1(MessageViewHolder1 holder, int position) {
        Message c = mMessageList.get(position);

        holder.messageText.setText(c.getMessageText());
 //       holder.messageTime.setText(c.getMessageTime());
 //       holder.messageName.setText(c.getMessageUser());
    }

    private void configureViewHolder2(MessageViewHolder2 holder, int position) {

        Message c = mMessageList.get(position);

        String sender_id = c.getMessageUserId();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.child(sender_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.messageText.setText(c.getMessageText());
 //       holder.messageTime.setText(c.getMessageTime());
        holder.messageName.setText(c.getMessageUser());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String user_id =   mAuth.getInstance().getCurrentUser().getUid();

        Message c = mMessageList.get(position);

        String from_user_id = c.getMessageUserId();

        if (!from_user_id.equals(user_id)){
            return USER;
        }
        else {
            return NONUSER;
        }
    }
}
