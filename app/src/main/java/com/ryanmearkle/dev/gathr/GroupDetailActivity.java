package com.ryanmearkle.dev.gathr;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.eventbus.EventBus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;
import com.ryanmearkle.dev.gathr.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailActivity extends AppCompatActivity implements GroupEventFragment.OnFragmentInteractionListener, GroupResourceFragment.OnFragmentInteractionListener, CreateGroupEventDialogFragment.CreateGroupEventDialogListener,
        AddAdminDialogFragment.AddAdminDialogListener, TimePickerFragment.TimePickerDialogListener {


    private FirebaseDatabase mFirebaseDatabase;
    public SectionsPagerAdapter mSectionsPagerAdapter;
    private FloatingActionButton fab;
    private ViewPager mViewPager;
    public String groupString;
    public String currentUserID;
    private Group group;
    private int mPosition;
    private boolean isAdmin = false;
    private NFCManager nfcMger;
    private NdefMessage message = null;
    private ProgressDialog dialog;
    private DialogFragment cgeDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                groupString = null;
                currentUserID = null;
            } else {
                groupString = extras.getString("GROUP");
                currentUserID = extras.getString("USER");
            }
        } else {
            groupString = (String) savedInstanceState.getSerializable("GROUP");
            currentUserID = (String) savedInstanceState.getSerializable("USER");
        }
        nfcMger = new NFCManager(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference()
                .child("groups")
                .child(groupString)
                .child("admins")
                .child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            fab.setVisibility(View.VISIBLE);

                            isAdmin = true;
                            //Log.d("Admin status", "FULL CONTROL");
                        } else {

                            //Log.d("Admin status", "PLEB");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupString);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPosition == 0) {
                    cgeDialog = CreateGroupEventDialogFragment.newInstance(groupString);
                    cgeDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                } else {
                    Log.d("Resource Fragment", "FAB Click");
                }
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), groupString);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        mFirebaseDatabase.getReference()
                .child("groups")
                .child(groupString)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            group = dataSnapshot.getValue(Group.class);
                        } else {
                            Log.d("Group not found!", groupString);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Uh-oh", "Failed to read value.", error.toException());
                    }
                });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isAdmin) {
            MenuItem item = menu.findItem(R.id.add_admin);
            item.setVisible(true);
            MenuItem item1 = menu.findItem(R.id.add_nfc_tag);
            item1.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_admin) {
            DialogFragment dialog1 = AddAdminDialogFragment.newInstance(groupString);
            dialog1.show(getSupportFragmentManager(), "NoticeDialogFragment");
            return true;
        }
        if (id == R.id.add_nfc_tag) {
            message = nfcMger.createTextMessage(groupString);
            if (message != null) {
                dialog = new ProgressDialog(GroupDetailActivity.this);
                dialog.setMessage("Tap the NFC tag to link with " + groupString);
                dialog.show();
            }
            //DialogFragment dialog2 = AddNFCTagDialogFragment.newInstance(groupString);
            //dialog2.show(getSupportFragmentManager(), "NoticeDialogFragment");
            return true;
        }
        if (id == android.R.id.home) {
            //Log.d("test","test");
            //NavUtils.navigateUpFromSameTask(this);
            this.onBackPressed();
            return true;
        }
        if (id == R.id.action_chat) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://groupme.com/join_group/26790357/apiZS1"));
            startActivity(browserIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private Map<String, String> getGroupUserList() {
        final Map<String, String> users = new HashMap<String, String>();

        mFirebaseDatabase.getReference().child("groups").child(groupString).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onCreateGroupEventDialogPositiveClick(Event event) {
        mFirebaseDatabase.getReference()
                .child("groups")
                .child(groupString)
                .child("events")
                .child(event.getTitle())
                .setValue(event);
    }

    @Override
    public void hideNoGroupText() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        if (mViewPager.getCurrentItem() == 0 && page != null) {
            ((GroupEventFragment) page).hideNoGroupText();
        }
    }

    @Override
    public void onAddAdminDialogPositiveClick(List<String> adminIDs, List<String> adminNames) {
        for (int i = 0; i < adminIDs.size(); i++) {
            mFirebaseDatabase.getReference()
                    .child("groups")
                    .child(groupString)
                    .child("admins")
                    .child(adminIDs.get(i))
                    .setValue(adminNames.get(i));
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        if (cgeDialog != null){
            ((CreateGroupEventDialogFragment)cgeDialog).writeToTag(this, intent);
        }
    }


    @Override
    public void onTimeSet(String time) {

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

        public SectionsPagerAdapter(FragmentManager fm, String groupName) {
            super(fm);
            groupString = groupName;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GroupEventFragment.newInstance(groupString);
                case 1:
                    return GroupResourceFragment.newInstance(groupString);
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
                    return "Events";
                case 1:
                    return "Resources";
            }
            return null;
        }
    }
}
