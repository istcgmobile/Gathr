package com.ryanmearkle.dev.gathr.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.R;

/**
 * Created by ryanm on 10/24/2016.
 */

public class SimpleUserViewHolder extends RecyclerView.ViewHolder{
    public View itemView;

    public SimpleUserViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setName(String name) {
        TextView groupName = (TextView) itemView.findViewById(R.id.personName);
        groupName.setText(name);
    }
}