package com.ryanmearkle.dev.gathr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;

import org.w3c.dom.Text;

/**
 * Created by ryanm on 10/24/2016.
 */

public class CreateGroupEventDialogFragment extends DialogFragment implements TimePickerFragment.TimePickerDialogListener{

    private AlertDialog dialog;
    private CreateGroupEventDialogListener mListener;
    private Event event;
    private View view;
    private TextView startTime;
    private String groupName;

    public static CreateGroupEventDialogFragment newInstance(String groupString) {
        CreateGroupEventDialogFragment f = new CreateGroupEventDialogFragment();
        Bundle args = new Bundle();
        args.putString("GROUP", groupString);
        f.setArguments(args);
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.fragment_dialog_create_group_event, null);
        /*
        startTime = (TextView) view.findViewById(R.id.eventTime);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);

            }
        } );
        */
        builder.setView(view)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null);



        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onTimeSet(String time) {
        startTime.setText(time);
    }

    public interface CreateGroupEventDialogListener {
        public void onCreateGroupEventDialogPositiveClick(Event event);
        public void onCreateGroupEventDialogNegativeClick();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupName = getArguments().getString("GROUP");
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CreateGroupEventDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
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

                    TextInputLayout til = (TextInputLayout) d.findViewById(R.id.text_input_layout);
                    TextInputLayout til1 = (TextInputLayout) d.findViewById(R.id.text_input_layout1);
                    TextInputLayout til2 = (TextInputLayout) d.findViewById(R.id.text_input_layout2);
                    TextInputLayout til3 = (TextInputLayout) d.findViewById(R.id.text_input_layout3);

                    til.setErrorEnabled(false);
                    til1.setErrorEnabled(false);
                    TextInputEditText nameField = (TextInputEditText) d.findViewById(R.id.eventNameField);
                    TextInputEditText descField = (TextInputEditText) d.findViewById(R.id.eventDescField);
                    TextInputEditText locField = (TextInputEditText) d.findViewById(R.id.eventLocationField);
                    TextInputEditText timeField = (TextInputEditText) d.findViewById(R.id.eventTimeField);

                    String nameText = nameField.getText().toString();
                    String descText = descField.getText().toString();
                    String locText = locField.getText().toString();
                    String timeText = timeField.getText().toString();

                    Event event = new Event(nameText, descText, groupName, locText, timeText);
                    mListener.onCreateGroupEventDialogPositiveClick(event);
                    dismiss();


                    /*
                    if (nameText.isEmpty()) {
                        //til.setErrorEnabled(true);
                        til.setError("You need to enter a name");
                    }
                    if (descText.isEmpty()) {
                        //til.setErrorEnabled(true);
                        til1.setError("You need to enter a description");
                    }
                    if(!nameText.isEmpty() && !descText.isEmpty()){

                    }
                    */
                }
            });
        }
    }
}


