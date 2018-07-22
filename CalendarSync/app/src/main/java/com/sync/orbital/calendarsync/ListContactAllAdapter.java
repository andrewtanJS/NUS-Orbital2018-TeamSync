package com.sync.orbital.calendarsync;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListContactAllAdapter extends RecyclerView.Adapter<ListContactAllAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ContactsAllStruct> mList;
    ListContactAllAdapter(Context context, ArrayList<ContactsAllStruct> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ContactsAllStruct contactsInfo = mList.get(position);
        ImageView picture;
        TextView name;

        picture = holder.picture;
        name = holder.name;

 //       picture.setImageResource(contactsInfo.getImage());
        name.setText(contactsInfo.getName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView picture;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.profile_pic_contacts);
            name = (TextView) itemView.findViewById(R.id.name_contacts_all);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {
        }

        public void onClick(View view){

        }
    }
}
