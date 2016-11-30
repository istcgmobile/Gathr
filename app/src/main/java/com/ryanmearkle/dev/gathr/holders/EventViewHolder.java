package com.ryanmearkle.dev.gathr.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.R;

/**
 * Created by ryanm on 10/24/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    public View itemView;

    public EventViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        itemView.setOnCreateContextMenuListener(this);

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
        TextView dateTime = (TextView) itemView.findViewById(R.id.dateTimeText);
        String date = dateTime.getText().toString().split("-")[0];
        Log.d("SetTime Date", date);
        dateTime.setText(date + "- " + time);
    }
    public void setDate(String date) {
        TextView dateTime = (TextView) itemView.findViewById(R.id.dateTimeText);
        String time = dateTime.getText().toString().split("-")[1];
        Log.d("SetDate Time", time);
        dateTime.setText(date + " -" + time);
    }
    public void setLocation(String location) {
        TextView startTime = (TextView) itemView.findViewById(R.id.locationText);
        startTime.setText(location);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.setHeaderTitle("Select an Action");
        Log.d("AAAAAAAAAAAAAAAAAAAAAAA", String.valueOf(v.getId()));
        menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
    }

}