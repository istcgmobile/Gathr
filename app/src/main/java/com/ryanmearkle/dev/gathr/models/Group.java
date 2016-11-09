package com.ryanmearkle.dev.gathr.models;

import android.graphics.Color;

import java.util.Map;

/**
 * Created by ryanm on 9/26/2016.
 */

public class Group {

    private String name;
    private String desc;
    private String category;
    private Map<String, String> admins;
    private Map<String, String> users;
    private Map<String, String> events;
    private Map<String, String> resources;


    public Group(String name, String desc, String category, Map<String, String> admins, Map<String, String> users, Map<String, String> events, Map<String, String> resources){
        this.name=name;
        this.category=category;
        this.desc=desc;
        this.admins=admins;
        this.users=users;
        this.events=events;
        this.resources=resources;
    }

    public Group(){

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
        return this.desc;
    }

    public String getCategory(){
        return this.category;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
