package com.ryanmearkle.dev.gathr.models;

import android.graphics.Color;

import java.util.Map;

/**
 * Created by ryanm on 9/26/2016.
 */

public class Group {

    private String name;
    private String description;
    private String category;
    private Map<String, String> admins;
    private Map<String, String> users;
    private Map<String, String> events;
    private Map<String, String> resources;


    public Group(String name, String description, String category, Map<String, String> admins, Map<String, String> users, Map<String, String> events, Map<String, String> resources){
        this.name=name;
        this.category=category;
        this.description=description;
        this.admins=admins;
        this.users=users;
        this.events=events;
        this.resources=resources;
    }

    public String getName(){
        return this.name;
    }
    public Map<String, String> getAdmins(){ return this.admins; }
    public boolean setAdmins(Map<String, String> admins){
        this.admins=admins;
        return true;
    };
    public String getDesc(){
        return this.description;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
