package com.ryanmearkle.dev.gathr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.adapters.CombinedEventAdapter;
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
    private CombinedEventAdapter mAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private CalendarView calendarView;
    private User mCurrentUser;
    private String currentUserUID;
    private ArrayList<String> userGroups;
    private ArrayList<Event> userEvents;
    private int counter;
    private View view;
    private Long date;


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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mListener.onLoadInteraction(friendlyName, TAG);
        counter = 0;
        userGroups = new ArrayList<String>();
        userEvents = new ArrayList<Event>();
        calendarView = (CalendarView) ((MainActivity)getContext()).findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month = month+1;
                String formattedMonth = "";
                String formattedDay = "";
                if((month)<10){
                    formattedMonth = "0"+month;
                }
                else if(month>=10){
                    formattedMonth = String.valueOf(month);
                }
                if((dayOfMonth)<10){
                    formattedDay = "0"+dayOfMonth;
                }
                else if(dayOfMonth>=10){
                    formattedDay = String.valueOf(dayOfMonth);
                }
                date = Long.parseLong(year+formattedMonth+formattedDay);
                updateListOnNewDate(date);
                calendarView.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Year=" + year + " Month=" + month + " Day=" + dayOfMonth, Toast.LENGTH_LONG).show();
            }
        });
        calendarView.setVisibility(View.GONE);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            /*
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = "ANONYMOUS";
                mPhotoUrl = null;
                startActivity(new Intent(this, GoogleSignInActivity.class));
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this, GroupDetailActivity.class);
                startActivity(intent);
*/
            case R.id.group_picker:
                if (calendarView.getVisibility()==View.VISIBLE){
                    //ViewCompat.animate(arrow).rotation(0).start();
                    calendarView.setVisibility(View.GONE);
                } else {
                    //ViewCompat.animate(arrow).rotation(180).start();
                    calendarView.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.date_reset:
                resetDate();
                calendarView.setVisibility(View.GONE);
                calendarView.setDate(System.currentTimeMillis());

            default:
                return super.onOptionsItemSelected(item);
        }

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
        Log.d("getGroupList", currentUserUID);
        mFirebaseDatabase.getReference()
                .child("users")
                .child(currentUserUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Datasnapshot", dataSnapshot.toString());
                        if(!dataSnapshot.exists()){

                        }
                        else {
                            Log.d("dataSnapshot", "User");

                            mCurrentUser = dataSnapshot.getValue(User.class);
                            //Log.d("Group1", String.valueOf(mCurrentUser.getGroups().containsKey("IST440")));

                            for(String key : mCurrentUser.getGroups().keySet()){
                                userGroups.add(key);
                                Log.d("group:", key);

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
        Log.d("getEvents", String.valueOf(userGroups.size()));
        final int size = userGroups.size();
        for(int i = 0; i < userGroups.size(); i++){
            String group = userGroups.get(i);
            mFirebaseDatabase.getReference()
                    .child("groups")
                    .child(group)
                    .child("events")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("getEvents-Snapshot", dataSnapshot.toString());
                            if(!dataSnapshot.exists()){

                            }
                            else {
                                for(DataSnapshot event : dataSnapshot.getChildren()){
                                    Log.d("got events!", event.toString());
                                    Event tempEvent = event.getValue(Event.class);
                                    userEvents.add(tempEvent);
                                }
                                counter++;
                                Log.d("Counter", String.valueOf(counter));
                                if(counter==size){
                                    sortEvents();
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

    }

    private void sortEvents(){
        Log.d("SortEvents", "entered");
        Collections.sort(userEvents, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return Long.compare(o1.getDateTime(), o2.getDateTime());
            }
        });
        for(int i=0; i<userEvents.size(); i++){
            Event tmpEvent = userEvents.get(i);
            Log.d("SORTEVENTS FOR LOOP", String.valueOf(tmpEvent.getDateTime()) + tmpEvent.getGroupID()+" "+tmpEvent.getTitle());
        }
        Log.d("DONE", "EVENTS SORTED");
        setupRecyclerView();
    }

    private void setupRecyclerView(){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.eventRVList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CombinedEventAdapter(getContext(), userEvents);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateListOnNewDate(long date){
        ArrayList<Event> filteredDataSet = new ArrayList<Event>();
        for(int i=0; i<userEvents.size(); i++){
            Event tmpEvent = userEvents.get(i);
            String tmpDate = String.valueOf(tmpEvent.getDateTime()).substring(0,8);
            if(tmpDate.equals(String.valueOf(date))){
                filteredDataSet.add(tmpEvent);
            }
        }
        CombinedEventAdapter mFilteredAdapter = new CombinedEventAdapter(getContext(), filteredDataSet);
        mRecyclerView.swapAdapter(mFilteredAdapter, false);
    }

    public void resetDate(){
        mRecyclerView.swapAdapter(mAdapter, false);

    }

}