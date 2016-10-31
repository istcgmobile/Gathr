package com.ryanmearkle.dev.gathr.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.R;

/**
 * Created by ryanm on 10/24/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder {
    private final TextView eventName;
    private final TextView groupName;
    private final TextView startTime;
    private final TextView desc;



    public static EventViewHolder newInstance(View itemView) {
        TextView eventName = (TextView) itemView.findViewById(R.id.eventNameText);
        TextView groupName = (TextView) itemView.findViewById(R.id.groupNameText);
        TextView startTime = (TextView) itemView.findViewById(R.id.startTimeText);
        TextView desc = (TextView) itemView.findViewById(R.id.eventDescText);

        return new EventViewHolder(itemView, eventName, groupName, startTime, desc);
    }

    private EventViewHolder(View itemView, TextView eventName, TextView groupName, TextView startTime, TextView desc) {
        super(itemView);
        this.eventName = eventName;
        this.groupName = groupName;
        this.startTime = startTime;
        this.desc = desc;
    }

    public void setTitle(String title) {
        eventName.setText(title);
    }
    public void setGroup(String group) {
        groupName.setText(group);
    }
    public void setDescription(String descr) {
        desc.setText(descr);
    }
    public void setLocation(String time, String location) {
        startTime.setText(time + " in " + location);
    }
}