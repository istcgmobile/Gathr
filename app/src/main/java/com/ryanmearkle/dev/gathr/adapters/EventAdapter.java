package com.ryanmearkle.dev.gathr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ryanmearkle.dev.gathr.R;
import com.ryanmearkle.dev.gathr.holders.EventViewHolder;
import com.ryanmearkle.dev.gathr.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanm on 10/24/2016.
 */

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private static final int SIZE = 5;
    private final List<Event> items;

    public static EventAdapter newInstance(Context context) {
        List<Event> items = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Event event = new Event("Weekly Meeting", "This is our standard weekly meeting to cover new information for the group. Please attend if you are available. This is extra text to test truncation of the description, the full description can be viewed when clicked.",
            "Group Name", "Event Location", "6:00 PM");
            items.add(event);
        }
        return new EventAdapter(items);
    }

    private EventAdapter(List<Event> items) {
        this.items = items;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list, parent, false);
        return EventViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = items.get(position);
        holder.setTitle(event.getTitle());
        holder.setGroup(event.getClub());
        holder.setDescription(event.getDesc());
        holder.setLocation(event.getDate(), event.getLocation());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}