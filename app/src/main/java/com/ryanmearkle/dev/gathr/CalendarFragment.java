package com.ryanmearkle.dev.gathr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.adapters.EventAdapter;
import com.ryanmearkle.dev.gathr.models.User;


import java.util.Calendar;
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
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private User mCurrentUser;

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
        Calendar now = Calendar.getInstance();


        Calendar[] selectableDays = new Calendar[7];
        Calendar day = Calendar.getInstance();
        Calendar day2 = Calendar.getInstance();
        Calendar day3 = Calendar.getInstance();
        Calendar day4 = Calendar.getInstance();
        Calendar day5 = Calendar.getInstance();
        Calendar day6 = Calendar.getInstance();
        Calendar day7 = Calendar.getInstance();

        day.set(2016, Calendar.OCTOBER, 3);         selectableDays[0]=day;
        day2.set(2016, Calendar.OCTOBER, 10);        selectableDays[1]=day2;
        day3.set(2016, Calendar.OCTOBER, 17);        selectableDays[2]=day3;
        day4.set(2016, Calendar.OCTOBER, 24);        selectableDays[3]=day4;
        day5.set(2016, Calendar.OCTOBER, 27);        selectableDays[4]=day5;
        day6.set(2016, Calendar.OCTOBER, 31);        selectableDays[5]=day6;
        day6.set(2017, Calendar.MAY, 7);        selectableDays[6]=day7;


        //dpd.setSelectableDays(selectableDays);

        //getActivity().getFragmentManager().beginTransaction().replace(R.id.calendarView, dpd).commit();



        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = EventAdapter.newInstance(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirebaseDatabase.getReference()
                .child("users")
                .child(mFirebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User userAccount = dataSnapshot.getValue(User.class);
                        mCurrentUser = userAccount;

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        final Button button = (Button) view.findViewById(R.id.nameButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(mCurrentUser.getName(), mCurrentUser.getUid());
                }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLoadInteraction(null, null);
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


}