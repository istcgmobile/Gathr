package com.ryanmearkle.dev.gathr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ryanmearkle.dev.gathr.holders.GroupViewHolder;
import com.ryanmearkle.dev.gathr.holders.SimpleUserViewHolder;
import com.ryanmearkle.dev.gathr.models.Group;
import com.ryanmearkle.dev.gathr.models.SimpleUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventAttendanceFragment.onLoadListener} interface
 * to handle interaction events.
 * Use the {@link EventAttendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventAttendanceFragment extends ViewFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onLoadListener mListener;
    private RecyclerView groupRV;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    public EventAttendanceFragment() {
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventAttendanceFragment newInstance(String param1, String param2) {
        EventAttendanceFragment fragment = new EventAttendanceFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_attendance, container, false);
        //Log.d("GroupListFrament", "layout inflated");
        groupRV = (RecyclerView) v.findViewById(R.id.userRV);
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<SimpleUser,SimpleUserViewHolder>(
                SimpleUser.class,
                R.layout.item_attendance_list,
                SimpleUserViewHolder.class,
                mFirebaseDatabaseReference
                        .child("groups")
                        .child(mParam1)
                        .child("events")
                        .child(mParam2)
                        .child("attendance")){

            @Override
            protected void populateViewHolder(SimpleUserViewHolder viewHolder,
                                              SimpleUser person, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.setName(person.getName());
            }


        };
        groupRV.setAdapter(mFirebaseAdapter);

        return v;
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onLoadListener {
        // TODO: Update argument type and name
        void onLoadInteraction(String name, String tag);
    }
}
