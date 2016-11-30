package com.ryanmearkle.dev.gathr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanm on 10/24/2016.
 */

public class AddNFCTagDialogFragment extends DialogFragment {

    private FirebaseDatabase mFirebaseDatabase;
    private AlertDialog dialog;
    //private AddAdminDialogListener mListener;
    private String groupName;
    private List<String> userNames = new ArrayList<String>();
    private List<String> userIDs = new ArrayList<String>();
    private View view;
    private Tag tag;

    public static AddNFCTagDialogFragment newInstance(String groupName) {
        AddNFCTagDialogFragment f = new AddNFCTagDialogFragment();
        Bundle args = new Bundle();
        args.putString("GROUP", groupName);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupName = getArguments().getString("GROUP");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.fragment_dialog_add_nfc_tag, null);

        builder.setView(view)
                .setNegativeButton("Cancel", null);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference().child("groups").child(groupName).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    userNames.add(userSnapshot.getValue().toString());
                    userIDs.add(userSnapshot.getKey());
                }

                Spinner spinner = (Spinner) view.findViewById(R.id.newAdminPicker1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, userNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog = builder.create();
        return dialog;
    }



    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            //mListener = (AddAdminDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            //Log.d("text", positiveButton.getText().toString());

            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    Spinner admin1 = (Spinner) d.findViewById(R.id.newAdminPicker1);
                    List<String> newAdminIDs = new ArrayList<String>();
                    List<String> newAdminNames = new ArrayList<String>();


                    if(!admin1.getSelectedItem().toString().isEmpty()){
                        newAdminIDs.add(userIDs.get(admin1.getSelectedItemPosition()));
                        newAdminNames.add(userNames.get(admin1.getSelectedItemPosition()));
                        //mListener.onAddAdminDialogPositiveClick(newAdminIDs, newAdminNames);
                        dismiss();
                    }
                }
            });

        }
    }
}


