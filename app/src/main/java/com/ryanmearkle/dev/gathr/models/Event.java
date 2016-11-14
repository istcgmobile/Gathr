package com.ryanmearkle.dev.gathr.models;

import android.graphics.Color;

import java.util.List;

/**
 * Created by ryanm on 9/26/2016.
 */

public class Event {

    private String iCalUID;
    private String id;
    private String groupID;
    private String location;
    private String startTime;
    private String title;
    private String desc;

    private List<String> attendance;
    private List<String> resources;


    public Event(String title, String desc, String groupID, String location, String startTime){
        this.title=title;
        this.groupID=groupID;
        this.location=location;
        this.startTime=startTime;
        this.desc=desc;
    }

    public Event() {
    }

    public String getTitle(){
        return this.title;
    }
    public String getLocation(){
        return this.location;
    }
    public String getGroupID(){
        return this.groupID;
    }
    public String getStartTime(){
        return this.startTime;
    }
    public String getDesc() {return this.desc; }
}
