package com.ryanmearkle.dev.gathr.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.R;

/**
 * Created by ryanm on 10/24/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder {
    public View itemView;

    public EventViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setTitle(String title) {
        TextView eventName = (TextView) itemView.findViewById(R.id.eventNameText);
        eventName.setText(title);
    }
    public void setGroup(String group) {
        TextView groupName = (TextView) itemView.findViewById(R.id.groupNameText);
        groupName.setText(group);
    }
    public void setDescription(String descr) {
        TextView desc = (TextView) itemView.findViewById(R.id.eventDescText);
        desc.setText(descr);
    }
    public void setStartTime(String time){
        TextView startTime = (TextView) itemView.findViewById(R.id.startTimeText);
        startTime.setText(startTime.getText().toString().replace(startTime.getText().subSequence(0,6) , time));
    }
    public void setLocation(String location) {
        TextView startTime = (TextView) itemView.findViewById(R.id.startTimeText);
        startTime.setText(startTime.getText().toString().replace(startTime.getText().toString().substring(9), location));
    }
}