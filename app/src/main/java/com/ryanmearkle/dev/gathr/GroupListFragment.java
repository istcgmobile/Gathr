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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.holders.GroupViewHolder;
import com.ryanmearkle.dev.gathr.models.Group;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupListFragment.onLoadListener} interface
 * to handle interaction events.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends ViewFragment {
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
    private View v;

    public GroupListFragment() {
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
    public static GroupListFragment newInstance(String param1, String param2) {
        GroupListFragment fragment = new GroupListFragment();
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
        v = inflater.inflate(R.layout.fragment_group_list, container, false);
        Log.d("GroupListFrament", "layout inflated");
        groupRV = (RecyclerView) v.findViewById(R.id.groupRV);
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("groups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            hideNoGroupText();
                        }
                        else{
                            showNoGroupText();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Uh-oh", "Failed to read value.", error.toException());
                    }
                });

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Group,GroupViewHolder>(
                Group.class,
                R.layout.item_group_list,
                GroupViewHolder.class,
                mFirebaseDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("groups")){

            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder,
                                              Group group, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.setCategory(group.getCategory());
                viewHolder.setGroup(group.getName());
                viewHolder.setDescription(group.getDesc());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GroupDetailActivity.class);
                        intent.putExtra("GROUP", mFirebaseAdapter.getItem(position).toString());
                        intent.putExtra("USER", ((MainActivity)getContext()).getCurrentUserID());
                        startActivity(intent);
                    }
                });
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

    public void hideNoGroupText(){
        TextView noGroupText = (TextView) v.findViewById(R.id.noGroupText);
        noGroupText.setVisibility(View.GONE);
    }
    public void showNoGroupText(){
        TextView noGroupText = (TextView) v.findViewById(R.id.noGroupText);
        noGroupText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onLoadListener) {
            mListener = (onLoadListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoadListener");
        }
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
