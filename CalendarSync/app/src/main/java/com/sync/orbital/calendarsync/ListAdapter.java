package com.sync.orbital.calendarsync;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<EventIncomingStruct> mList;
    ListAdapter(Context context, ArrayList<EventIncomingStruct> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        EventIncomingStruct eventInfo = mList.get(position);
        TextView name, status, attendees, startTime, startDate;
        String timestr, datestr;

        name = holder.name;
        status = holder.status;
        attendees = holder.attendees;
        startTime = holder.time;
        startDate = holder.date;

        timestr = eventInfo.getStartTime().get(Calendar.HOUR) + ":" +
                eventInfo.getStartTime().get(Calendar.MINUTE);
        datestr = eventInfo.getStartTime().get(Calendar.DAY_OF_MONTH) + "/" +
                eventInfo.getStartTime().get(Calendar.MONTH) + "/" +
                eventInfo.getStartTime().get(Calendar.YEAR);

        name.setText(eventInfo.getName());
        status.setText((eventInfo.getStatus()));
        attendees.setText(eventInfo.getAttendees());
        startTime.setText(timestr);
        startDate.setText(datestr);

 /*       switch(position%5) {
            case 0: holder.itemView.setBackgroundColor(Color.parseColor("#fff0b3")); break;
            case 1: holder.itemView.setBackgroundColor(Color.parseColor("#ccccff")); break;
            case 2: holder.itemView.setBackgroundColor(Color.parseColor("#ccffcc")); break;
            case 3: holder.itemView.setBackgroundColor(Color.parseColor("#ccf5ff")); break;
            case 4: holder.itemView.setBackgroundColor(Color.parseColor("#ffe6cc")); break;
            default: holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        */


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, status, attendees, time, date;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.event_name);
            status = (TextView) itemView.findViewById(R.id.event_status);
            attendees = (TextView) itemView.findViewById(R.id.event_attendees);
            date = (TextView) itemView.findViewById(R.id._event_date);
            time = (TextView) itemView.findViewById(R.id.event_time);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {
        }

        public void onClick(View view){

        }
    }
}
