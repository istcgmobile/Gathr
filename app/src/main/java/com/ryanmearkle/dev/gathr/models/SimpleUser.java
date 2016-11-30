package com.ryanmearkle.dev.gathr.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class SimpleUser {

    private String mUid;
    private String mName;


    public SimpleUser(String uid, String name){
        this.mUid=uid;
        this.mName=name;
    }

    public SimpleUser(){
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", this.mUid);
        result.put("name", this.mName);
        return result;
    }



    public String getUid(){
        return this.mUid;
    }
    public void setUid(String uid){
        this.mUid = uid;
    }
    public String getName(){
        return this.mName;
    }
    public void setName(String name){
        this.mName = name;
    }
}
