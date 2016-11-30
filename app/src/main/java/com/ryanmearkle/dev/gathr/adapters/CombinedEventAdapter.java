package com.ryanmearkle.dev.gathr.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.EventDetailActivity;
import com.ryanmearkle.dev.gathr.R;
import com.ryanmearkle.dev.gathr.holders.EventViewHolder;
import com.ryanmearkle.dev.gathr.models.Event;

import java.util.ArrayList;

public class CombinedEventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private ArrayList<Event> ogEventList;
    private ArrayList<Event> eventList;
    private ArrayList<Event> filteredEventList;

    private Context context;
    private String groupName;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    // Provide a suitable constructor (depends on the kind of dataset)
    public CombinedEventAdapter(Context context, ArrayList<Event> eventList) {
        this.context=context;
        //this.groupName=groupName;
        this.eventList=eventList;
        this.ogEventList=eventList;
        this.filteredEventList=new ArrayList<Event>();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list, parent, false);

        EventViewHolder vh = new EventViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, final int position) {
        viewHolder.setTitle(eventList.get(position).getTitle());
        viewHolder.setGroup(eventList.get(position).getGroupID());
        viewHolder.setDescription(eventList.get(position).getDesc());
        viewHolder.setLocation(eventList.get(position).getLocation());
        viewHolder.setDateTime(eventList.get(position).getDateTime());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("GROUP", eventList.get(position).getGroupID());
                intent.putExtra("EVENT", eventList.get(position).getTitle());
                context.startActivity(intent);
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return eventList.size();
    }



}
