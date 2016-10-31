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
    private String name;
    private String description;

    private List<String> attendance;
    private List<String> resources;


    public Event(String title, String description, String groupID, String location, String startTime){
        this.name=title;
        this.groupID=groupID;
        this.location=location;
        this.startTime=startTime;
        this.description=description;
    }

    public Event() {
    }

    public String getTitle(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public String getClub(){
        return this.groupID;
    }
    public String getDate(){
        return this.startTime;
    }
    public String getDesc() {return this.description; }
}
