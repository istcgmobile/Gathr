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
    private Map<String, Object> events;
    private Map<String, Object> resources;


    public Group(String name, String desc, String category, Map<String, String> admins, Map<String, String> users, Map<String, Object> events, Map<String, Object> resources){
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
    public Map<String, String> getUsers(){ return this.users; }
    public boolean setUsers(Map<String, String> users){
        this.users=users;
        return true;
    };

    public String getDesc(){
        return this.desc;
    }

    public String getCategory(){
        return this.category;
    }

    public Map<String, Object> getEvents(){
        return this.events;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
