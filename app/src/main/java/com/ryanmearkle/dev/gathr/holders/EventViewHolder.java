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
    public void setDateTime(long dateTime){
        TextView dateTimeText = (TextView) itemView.findViewById(R.id.dateTimeText);
        String eventDateTime = String.valueOf(dateTime);
        Log.d("Set Time/Date", eventDateTime);
        String date = eventDateTime.substring(4,6)+"/"+eventDateTime.substring(6,8)+"/"+eventDateTime.substring(0,4);
        String time = eventDateTime.substring(8,10)+":"+eventDateTime.substring(10,12);
        dateTimeText.setText(date + " - " + time);
    }
    public void setLocation(String location) {
        TextView startTime = (TextView) itemView.findViewById(R.id.locationText);
        startTime.setText(location);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.setHeaderTitle("Select an Action");
        //Log.d("AAAAAAAAAAAAAAAAAAAAAAA", String.valueOf(v.getId()));
        menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
    }

}