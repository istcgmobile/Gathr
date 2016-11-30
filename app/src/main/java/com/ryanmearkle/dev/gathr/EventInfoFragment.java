package com.ryanmearkle.dev.gathr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;
import com.ryanmearkle.dev.gathr.models.User;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private Event event;
    private View view;

    public static EventInfoFragment newInstance(String param1, String param2) {
        EventInfoFragment fragment = new EventInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EventInfoFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Required empty public constructor
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_info, container, false);


        mFirebaseDatabase.getReference()
                .child("groups")
                .child(mParam1)
                .child("events")
                .child(mParam2)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Log.d("Datasnapshot", "Does not exist");
                        }
                        else {
                            event = dataSnapshot.getValue(Event.class);
                            setupLayout();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


        return view;
    }
    private void setupLayout(){
        TextView timeView = (TextView) view.findViewById(R.id.timeDate);
        TextView locationView = (TextView) view.findViewById(R.id.location);
        TextView descView = (TextView) view.findViewById(R.id.description);

        timeView.setText(event.getDate() + " at " + event.getStartTime());
        locationView.setText(event.getLocation());
        descView.setText(event.getDesc());
    }

}
