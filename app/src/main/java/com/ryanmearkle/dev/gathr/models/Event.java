package com.ryanmearkle.dev.gathr.models;

import android.graphics.Color;

import java.util.List;

/**
 * Created by ryanm on 9/26/2016.
 */

public class Event {

    //private String iCalUID;
    //private String id;
    private String groupID;
    private String location;
    private String startTime;
    private String date;
    private String title;
    private String desc;

    private List<String> attendance;
    private List<String> resources;


    public Event(String title, String desc, String groupID, String location, String date, String startTime){
        this.title=title;
        this.groupID=groupID;
        this.location=location;
        this.startTime=startTime;
        this.date=date;
        this.desc=desc;
    }

    public Event() {
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getDesc() {return this.desc; }
    public void setDesc(String desc){
        this.desc=desc;
    }
    public String getLocation(){
        return this.location;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public String getGroupID(){
        return this.groupID;
    }
    public void setGroupID(String groupID){
        this.groupID=groupID;
    }
    public String getStartTime(){
        return this.startTime;
    }
    public void setStartTime(String startTime){
        this.startTime=startTime;
    }
    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        this.date=date;
    }

    @Override
    public String toString(){
        return this.title;
    }

}
