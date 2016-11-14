package com.ryanmearkle.dev.gathr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.models.Group;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryanm on 10/24/2016.
 */

public class CreateGroupDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    private FirebaseDatabase mFirebaseDatabase;
    private CreateGroupDialogListener mListener;
    private Group group;
    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.fragment_dialog_create_group, null);

        builder.setView(view)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        Spinner spinner = (Spinner) view.findViewById(R.id.groupCatSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialog = builder.create();
        return dialog;
    }

    public interface CreateGroupDialogListener {
        public void onCreateGroupDialogPositiveClick(Group group);
        public void onCreateGroupDialogNegativeClick();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CreateGroupDialogListener) activity;
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
                    final TextInputLayout til = (TextInputLayout) d.findViewById(R.id.text_input_layout);
                    TextInputLayout til1 = (TextInputLayout) d.findViewById(R.id.text_input_layout1);
                    til.setErrorEnabled(false);
                    til1.setErrorEnabled(false);
                    TextInputEditText nameField = (TextInputEditText) d.findViewById(R.id.groupNameField);
                    TextInputEditText descField = (TextInputEditText) d.findViewById(R.id.groupDescField);
                    String nameText = nameField.getText().toString();
                    String descText = descField.getText().toString();

                    Spinner catSpinner = (Spinner) d.findViewById(R.id.groupCatSpinner);
                    String category = catSpinner.getSelectedItem().toString();

                    if (nameText.isEmpty()) {
                        //til.setErrorEnabled(true);
                        til.setError("You need to enter a name");
                    }
                    if (descText.isEmpty()) {
                        //til.setErrorEnabled(true);
                        til1.setError("You need to enter a description");
                    }
                    if(!nameText.isEmpty() && !descText.isEmpty()){


                        final Group group = new Group(nameText, descText, category, null, null, null, null);

                        Map<String, String> admins = new HashMap<String, String>();
                        Map<String, String> users = new HashMap<String, String>();

                        mFirebaseDatabase.getReference()
                                .child("groups")
                                .child(group.getName())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            til.setErrorEnabled(false);
                                            til.setError("A group with this name already exists.");
                                        } else {

                                            mFirebaseDatabase.getReference()
                                                    .child("groups")
                                                    .child(group.getName())
                                                    .setValue(group);
                                            mListener.onCreateGroupDialogPositiveClick(group);
                                            dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        //Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });



                    }
                }
            });
        }
    }
}


