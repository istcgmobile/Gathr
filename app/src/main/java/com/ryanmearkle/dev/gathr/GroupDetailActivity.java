package com.ryanmearkle.dev.gathr;

import android.net.Uri;
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

import com.google.common.eventbus.EventBus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ryanmearkle.dev.gathr.models.Event;
import com.ryanmearkle.dev.gathr.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailActivity extends AppCompatActivity implements GroupEventFragment.OnFragmentInteractionListener, GroupResourceFragment.OnFragmentInteractionListener, CreateGroupEventDialogFragment.CreateGroupEventDialogListener,
        AddAdminDialogFragment.AddAdminDialogListener{


    private FirebaseDatabase mFirebaseDatabase;
    public SectionsPagerAdapter mSectionsPagerAdapter;
    private FloatingActionButton fab;
    private ViewPager mViewPager;
    public String groupString;
    private Group group;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                groupString= null;
            } else {
                groupString= extras.getString("GROUP");
            }
        } else {
            groupString= (String) savedInstanceState.getSerializable("GROUP");
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupString);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPosition==0) {
                    DialogFragment dialog = CreateGroupEventDialogFragment.newInstance(groupString);
                    dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                }
                else{
                    Log.d("Resource Fragment", "FAB Click");
                }
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),groupString);

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
                        if(dataSnapshot.exists()){
                            group = dataSnapshot.getValue(Group.class);
                        }
                        else {
                            Log.d("Group not found!", groupString);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Uh-oh", "Failed to read value.", error.toException());
                    }
                });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_detail, menu);
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
        if (id == android.R.id.home) {
            //Log.d("test","test");
            //NavUtils.navigateUpFromSameTask(this);
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private Map<String, String> getGroupUserList(){
        final Map<String, String> users = new HashMap<String, String>();

        mFirebaseDatabase.getReference().child("groups").child(groupString).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
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
    public void onCreateGroupEventDialogNegativeClick() {

    }

    @Override
    public void onAddAdminDialogPositiveClick(List<String> adminIDs, List<String> adminNames) {
        for(int i=0; i<adminIDs.size(); i++) {
            mFirebaseDatabase.getReference()
                    .child("groups")
                    .child(groupString)
                    .child("admins")
                    .child(adminIDs.get(i))
                    .setValue(adminNames.get(i));
        }

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
