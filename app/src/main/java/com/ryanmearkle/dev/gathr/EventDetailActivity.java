package com.ryanmearkle.dev.gathr;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;
import com.ryanmearkle.dev.gathr.models.SimpleUser;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity implements GroupEventFragment.OnFragmentInteractionListener, GroupResourceFragment.OnFragmentInteractionListener
{


    private FirebaseDatabase mFirebaseDatabase;
    public SectionsPagerAdapter mSectionsPagerAdapter;
    private FloatingActionButton fab;
    private ViewPager mViewPager;
    public String groupName;
    public String eventString;
    public String currentUserID;
    private Event event;
    private int mPosition;
    private boolean isAdmin = false;
    private NFCManager nfcMger;
    private NdefMessage message = null;
    private ProgressDialog dialog;
    private Tag currentTag;
    private String nfcMessage;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Log.d("Extras", "null");
                eventString = "empty";
                groupName = "empty";
            } else {
                Log.d("Extras", "NOT null");
                eventString = extras.getString("EVENT");
                groupName = extras.getString("GROUP");
                //Log.d("VALUES", eventString);
                //Log.d("VALES", groupName);
            }
        } else {
            Log.d("Extras", "SAVED");

            groupName = (String) savedInstanceState.getSerializable("GROUP");
            eventString = (String) savedInstanceState.getSerializable("EVENT");
        }
        nfcMger = new NFCManager(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(eventString);
        //Log.d("TAG", eventString);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPosition == 0) {
                    DialogFragment dialog = CreateGroupEventDialogFragment.newInstance(eventString);
                    dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                } else {
                    Log.d("Resource Fragment", "FAB Click");
                }
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.





        mFirebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null)
        {
            Log.d("NOPE", action);
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d("NFC", "NDEF");
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    //Log.d("NFC", "A message");
                }
                NdefRecord[] records = messages[0].getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            String text = readText(ndefRecord);
                            String[] groupAndEvent = text.split("&", 2);
                            eventString = groupAndEvent[1];
                            getSupportActionBar().setTitle(eventString);
                            groupName = groupAndEvent[0];
                            FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                            SimpleUser simpleUser = new SimpleUser(mCurrentUser.getUid(), mCurrentUser.getDisplayName());
                            mFirebaseDatabase.getReference()
                                    .child("groups")
                                    .child(groupName)
                                    .child("events")
                                    .child(eventString)
                                    .child("attendance")
                                    .child(mCurrentUser.getUid())
                                    .setValue(simpleUser);
                            Log.d(groupName,eventString);
                            setupLayout();
                            Toast toast = Toast.makeText(this, "Your attendance at "+groupName+" event "+eventString+" has been logged", Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if( v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        } catch (UnsupportedEncodingException e) {
                            Log.e("NFC", "Unsupported Encoding", e);
                        }
                    }
                }
            }
        }
        else{
            setupLayout();
        }



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupLayout(){

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), groupName, eventString);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // fire event if the "My Site" page is being scrolled so the fragment can
                // animate its fab to match
                mPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            nfcMger.verifyNFC();
            //nfcMger.enableDispatch();

            Intent nfcIntent = new Intent(this, getClass());
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
            IntentFilter[] intentFiltersArray = new IntentFilter[]{};
            String[][] techList = new String[][]{{Ndef.class.getName()}, {NdefFormatable.class.getName()}};
            NfcAdapter nfcAdpt = NfcAdapter.getDefaultAdapter(this);
            nfcAdpt.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        } catch (NFCManager.NFCNotSupported nfcnsup) {
            Log.d("NFC", "NFC not supported");
        } catch (NFCManager.NFCNotEnabled nfcnEn) {
            Log.d("NFC", "NFC not enabled");
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        nfcMger.disableDispatch();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_group_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private Map<String, String> getGroupUserList() {
        final Map<String, String> users = new HashMap<String, String>();

        mFirebaseDatabase.getReference().child("groups").child(eventString).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    users.put(userSnapshot.getKey(), userSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return users;
    }








    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("GroupDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        String groupString;
        String eventName;

        public SectionsPagerAdapter(FragmentManager fm, String groupName, String eventString) {
            super(fm);
            groupString = groupName;
            eventName = eventString;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EventInfoFragment.newInstance(groupString, eventName);
                case 1:
                    return EventAttendanceFragment.newInstance(groupString, eventName);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Attendance";
            }
            return null;
        }
    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //this.getSupportActionBar().setTitle("NFC TAG LAUNCH");
        //Log.d("NFC", intent.getAction());
        //nfcMessage = intent.getAction();

    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }
}
