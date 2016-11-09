package com.ryanmearkle.dev.gathr.holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.R;

/**
 * Created by ryanm on 10/24/2016.
 */

public class GroupViewHolder extends RecyclerView.ViewHolder{
    public View itemView;

    public GroupViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setGroup(String group) {
        TextView groupName = (TextView) itemView.findViewById(R.id.groupNameText);
        groupName.setText(group);
    }
    public void setDescription(String descr) {
        TextView desc = (TextView) itemView.findViewById(R.id.groupDescText);
        desc.setText(descr);
    }

    public void setCategory(String cat) {
        TextView catName = (TextView) itemView.findViewById(R.id.groupCatText);
        catName.setText(cat);
    }




}