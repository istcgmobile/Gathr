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
import com.ryanmearkle.dev.gathr.models.Group;

/**
 * Created by ryanm on 10/24/2016.
 */

public class JoinGroupDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    private JoinGroupDialogListener mListener;
    private Group group;
    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.fragment_dialog_join_group, null);

        builder.setView(view)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null);

        dialog = builder.create();
        return dialog;
    }

    public interface JoinGroupDialogListener {
        public void onJoinGroupDialogPositiveClick(String groupName);
        public void onJoinGroupDialogNegativeClick();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (JoinGroupDialogListener) activity;
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
                    TextInputLayout til = (TextInputLayout) d.findViewById(R.id.text_input_layout3);

                    til.setErrorEnabled(false);

                    TextInputEditText nameField = (TextInputEditText) d.findViewById(R.id.groupNameSearchField);

                    String nameText = nameField.getText().toString();

                    if (nameText.isEmpty()) {
                        //til.setErrorEnabled(true);
                        til.setError("Enter a group name to lookup");
                    }
                    if(!nameText.isEmpty()){
                        mListener.onJoinGroupDialogPositiveClick(nameText);
                        dismiss();
                    }
                }
            });
        }
    }
}


