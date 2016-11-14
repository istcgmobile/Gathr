package com.ryanmearkle.dev.gathr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ryanmearkle.dev.gathr.holders.EventViewHolder;
import com.ryanmearkle.dev.gathr.holders.GroupViewHolder;
import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;


public class GroupEventFragment extends ViewFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView groupEventRV;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GroupEventFragment() {
        // Required empty public constructor
    }

    @Override
    public String getFriendlyName() {
        return null;
    }

    @Override
    public String getTagName() {
        return null;
    }

    // TODO: Rename and change types and number of parameters
    public static GroupEventFragment newInstance(String param1) {
        GroupEventFragment fragment = new GroupEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_event, container, false);
        groupEventRV = (RecyclerView) v.findViewById(R.id.groupEventRV);
        groupEventRV.setHasFixedSize(true);
        groupEventRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event,EventViewHolder>(
                Event.class,
                R.layout.item_event_list,
                EventViewHolder.class,
                mFirebaseDatabaseReference.child("groups").child(mParam1).child("events")){

            @Override
            protected void populateViewHolder(EventViewHolder viewHolder,
                                              Event event, final int position) {

                viewHolder.setTitle(event.getTitle());
                viewHolder.setGroup(event.getGroupID());
                viewHolder.setDescription(event.getDesc());
                viewHolder.setLocation(event.getLocation());
                viewHolder.setStartTime(event.getStartTime());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GroupDetailActivity.class);
                        intent.putExtra("EVENT", mFirebaseAdapter.getItem(position).toString());
                        startActivity(intent);
                    }
                });
            }


        };
        groupEventRV.setAdapter(mFirebaseAdapter);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getGroup(){
        return mParam1;
    }
}
