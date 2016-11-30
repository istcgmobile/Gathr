package com.ryanmearkle.dev.gathr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;
import com.ryanmearkle.dev.gathr.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.onLoadListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends ViewFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CALENDAR";
    private static final String friendlyName = "Calendar";


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private User mCurrentUser;
    private String currentUserUID;
    private ArrayList<String> userGroups = new ArrayList<String>();
    private ArrayList<Event> userEvents = new ArrayList<Event>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onLoadListener mListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mListener.onLoadInteraction(friendlyName, TAG);
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("Hello, World!");

/*
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = EventAdapter.newInstance(getContext());
        mRecyclerView.setAdapter(mAdapter);
        */


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUserUID = mFirebaseUser.getUid();

        getGroupList(currentUserUID);



        return view;
    }

    public void viewDetail(View v){
        Intent intent = new Intent(getContext(), CalendarActivity.class);
        startActivity(intent);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLoadInteraction(null, null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mListener.enableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onLoadListener) {
            mListener = (onLoadListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getFriendlyName(){
        return friendlyName;
    }

    public String getTagName(){
        return TAG;
    }

    public interface onLoadListener {
        // TODO: Update argument type and name
        void onLoadInteraction(String name, String tag);
    }

    private void getGroupList(String currentUserUID){
        mFirebaseDatabase.getReference()
                .child("users")
                .child(currentUserUID)
                .child("groups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Datasnapshot", dataSnapshot.toString());
                        if(!dataSnapshot.exists()){

                        }
                        else {
                            mCurrentUser = dataSnapshot.getValue(User.class);
                            for(String key : mCurrentUser.getGroups().keySet()){
                                userGroups.add(key);
                            }
                            getEvents(userGroups);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

    }

    private void getEvents(ArrayList<String> userGroups){
        for(int i = 0; i <= userGroups.size(); i++){
            String group = userGroups.get(i);
            mFirebaseDatabase.getReference()
                    .child("groups")
                    .child(group)
                    .child("events")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("Datasnapshot", dataSnapshot.toString());
                            if(!dataSnapshot.exists()){

                            }
                            else {
                                for(DataSnapshot event : dataSnapshot.getChildren()){
                                    Event tempEvent = event.getValue(Event.class);
                                    userEvents.add(tempEvent);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
        }
        sortEvents();
    }

    private void sortEvents(){
        Collections.sort(userEvents, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                


                return 0;
            }
        });
    }

}