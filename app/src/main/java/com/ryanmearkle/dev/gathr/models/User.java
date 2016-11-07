package com.ryanmearkle.dev.gathr.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class User {

    private String mUid;
    private String mName;
    private String mEmail;
    private String mPhotoURL;
    private Map<String, String> mGroups = new HashMap<String, String>();


    public User(String uid, String name, String email, String photoURL, Map<String, String> groups){
        this.mUid=uid;
        this.mName=name;
        this.mEmail=email;
        this.mPhotoURL=photoURL;
        this.mGroups=groups;
    }

    public  User(){
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", this.mUid);
        result.put("name", this.mName);
        result.put("email", this.mEmail);
        result.put("photoURL", this.mPhotoURL);
        result.put("groups", this.mGroups);

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
    public String getEmail(){
        return this.mEmail;
    }
    public void setEmail(String email){
        this.mEmail = email;
    }
    public String getPhotoURL(){
        return this.mPhotoURL;
    }
    public void setPhotoURL(String photoURL){
        this.mPhotoURL = photoURL;
    }
    public Map<String, String> getGroups(){ return this.mGroups; }
    public void setGroups(Map<String, String> groups){
        this.mGroups = groups;
    }
}
