package com.ryanmearkle.dev.gathr;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;

import org.w3c.dom.Text;

/**
 * Created by ryanm on 10/24/2016.
 */

public class CreateGroupEventDialogFragment extends DialogFragment implements TimePickerFragment.TimePickerDialogListener, DatePickerFragment.DatePickerDialogListener{

    private AlertDialog dialog;
    private CreateGroupEventDialogListener mListener;
    private Event event;
    private View view;
    private TextView startTime;
    private String groupName;
    private Button timeButton;
    private Button dateButton;
    private ToggleButton nfcButton;
    private NFCManager nfcMger;
    private NdefMessage message = null;
    private boolean useNFC = false;
    private ProgressDialog nfcDialog;
    private Tag currentTag;
    private String date;
    private String time;

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

        nfcMger = new NFCManager(getActivity());
        /*
        startTime = (TextView) view.findViewById(R.id.eventTime);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);

            }
        } );
        */
        timeButton = (Button) view.findViewById(R.id.timeButton);
        dateButton = (Button) view.findViewById(R.id.dateButton);
        ToggleButton toggle = (ToggleButton) view.findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    useNFC = true;
                } else {
                    useNFC = false;
                }
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setTargetFragment(CreateGroupEventDialogFragment.this, 0);
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(CreateGroupEventDialogFragment.this, 0);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });


        builder.setView(view)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null);



        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker v, int hour, int minute) {
        String formattedHour = "";
        String formattedMinute = "";
        String amPM = "AM";

        if(hour<10){
            formattedHour = "0"+hour;
        }
        else if (hour>=10){
            formattedHour = String.valueOf(hour);
        }
        if(minute<10){
            formattedMinute = "0"+minute;
        }
        else if(minute>=10){
            formattedMinute = String.valueOf(minute);
        }
        time = formattedHour+formattedMinute;
        if(hour>=12){
            amPM = "PM";
        }
        else if (hour<12 || hour>0){
            amPM = "AM";
        }
        timeButton.setText(hour+":"+formattedMinute+amPM);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month = month+1;
        String formattedMonth = "";
        String formattedDay = "";
        if((month)<10){
            formattedMonth = "0"+month;
        }
        else if(month>=10){
            formattedMonth = String.valueOf(month);
        }
        if((day)<10){
            formattedDay = "0"+day;
        }
        else if(day>=10){
            formattedDay = String.valueOf(day);
        }
        date = year+formattedMonth+formattedDay;
        dateButton.setText(month+"/"+day+"/"+year);
    }

    public interface CreateGroupEventDialogListener {
        public void onCreateGroupEventDialogPositiveClick(Event event);
        public void hideNoGroupText();
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

    public void writeToTag(Context context, Intent intent) {
        //("Nfc", "New intent");
        // It is the time to write the tag
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (message != null) {
            nfcMger.writeTag(currentTag, message);
            dialog.dismiss();
            nfcDialog.dismiss();
            Toast.makeText(context, "The NFC tag has been linked!", Toast.LENGTH_SHORT).show();

        }
        else {
            // Handle intent

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


                    TextInputLayout til = (TextInputLayout) d.findViewById(R.id.text_input_layout);
                    TextInputLayout til1 = (TextInputLayout) d.findViewById(R.id.text_input_layout1);
                    TextInputLayout til2 = (TextInputLayout) d.findViewById(R.id.text_input_layout2);
                    //TextInputLayout til3 = (TextInputLayout) d.findViewById(R.id.text_input_layout3);

                    til.setErrorEnabled(false);
                    til1.setErrorEnabled(false);
                    til2.setErrorEnabled(false);
                    TextInputEditText nameField = (TextInputEditText) d.findViewById(R.id.eventNameField);
                    TextInputEditText descField = (TextInputEditText) d.findViewById(R.id.eventDescField);
                    TextInputEditText locField = (TextInputEditText) d.findViewById(R.id.eventLocationField);


                    String nameText = nameField.getText().toString();
                    String descText = descField.getText().toString();
                    String locText = locField.getText().toString();
                    //String timeText = timeButton.getText().toString();
                    //String dateText = dateButton.getText().toString();

                    if (useNFC){
                        message = nfcMger.createTextMessage(groupName + "&" + nameText);
                        if (message != null) {
                            nfcDialog = new ProgressDialog(getContext());
                            nfcDialog.setMessage("Tap the NFC tag to link with " + groupName + " event \"" + nameText + "\"");
                            nfcDialog.show();
                        }
                    }

                    Event event = new Event(nameText, descText, groupName, locText, Long.parseLong(date+time));
                    mListener.hideNoGroupText();
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


