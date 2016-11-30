package com.ryanmearkle.dev.gathr;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.ryanmearkle.dev.gathr.models.Group;
import com.ryanmearkle.dev.gathr.models.SheetFab;
import com.ryanmearkle.dev.gathr.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity
        extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    CalendarFragment.onLoadListener,
                    GroupListFragment.onLoadListener,
                    View.OnClickListener,
                    GoogleApiClient.OnConnectionFailedListener,
                    CreateGroupDialogFragment.CreateGroupDialogListener,
                    JoinGroupDialogFragment.JoinGroupDialogListener
{

    private static final String TAG = "MainActivity";
    private NavigationView navigationView;
    private MaterialSheetFab msf;
    private String currentFragmentTag;
    private String currentFragmentFriendlyName;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private User mCurrentUser;
    private String mUsername = "none";
    private String mPhotoUrl = "none";
    private String mUserEmail = "none";
    private GoogleApiClient mGoogleApiClient;
    private CalendarView calendarView;
    private boolean isExpanded = false;
    private Long date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFirebase();

    }

    private void setupFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, GoogleSignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if(mFirebaseUser.getPhotoUrl()==null){
                mPhotoUrl = "";
            }else{
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            mUserEmail = mFirebaseUser.getEmail();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        //Log.d("UID", mFirebaseUser.getUid());

        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFirebaseDatabase.getReference()
                .child("users")
                .child(mFirebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Datasnapshot", dataSnapshot.toString());
                if(!dataSnapshot.exists()){
                    Map<String, Group> groups = new HashMap<String, Group>();
                    User newUser = new User(mFirebaseUser.getUid(), mFirebaseUser.getDisplayName(), mFirebaseUser.getEmail(), mPhotoUrl.toString(), groups );
                    mFirebaseDatabase.getReference()
                            .child("users")
                            .child(mFirebaseUser.getUid())
                            .setValue(newUser);
                    mCurrentUser = newUser;
                    Log.d(mCurrentUser.getUid(), newUser.getUid());
                }
                else {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    Map<String, Group> groups = mCurrentUser.getGroups();
                    for (String key : groups.keySet()) {

                    }
                    //Log.d("wat", "wat");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        setupLayout();
    }

    private void setupLayout() {

        //titleText = (TextView) findViewById(R.id.titleTextView);
        //titleText.setText("Events");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gathr");
        setSupportActionBar(toolbar);

        setupNavDrawer(toolbar);
        setupSheetFab();

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        //updateUI();
                    }
                });

        replaceFragment("GROUPS", false);
        getSupportActionBar().setTitle("Gathr");

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setVisibility(View.GONE);

        navigationView.setCheckedItem(R.id.nav_groups);
    }

    private void setupSheetFab() {
        SheetFab fab = (SheetFab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.colorAccent);
        int fabColor = getResources().getColor(R.color.colorAccent);

        // Initialize material sheet FAB
        msf = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        // Set material sheet item click listeners
        findViewById(R.id.fab_item_new_group).setOnClickListener(this);
        findViewById(R.id.fab_item_join_group).setOnClickListener(this);
        findViewById(R.id.fab_item_label).setOnClickListener(this);
        //findViewById(R.id.fab_sheet_item_nfc).setOnClickListener(this);
        //findViewById(R.id.fab_sheet_item_pin).setOnClickListener(this);
    }

    private void setupNavDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float offset) {
                if (msf.isSheetVisible()) {
                    msf.hideSheet();
                }
                super.onDrawerSlide(drawerView, offset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        TextView profileName = (TextView) headerView.findViewById(R.id.nameLabel);
        profileName.setText(mUsername);
        TextView profileEmail = (TextView) headerView.findViewById(R.id.emailLabel);
        profileEmail.setText(mUserEmail);
        CircleImageView profilePic = (CircleImageView) headerView.findViewById(R.id.profileView);
        Glide.with(MainActivity.this)
                .load(mPhotoUrl)
                .into(profilePic);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (msf.isSheetVisible()) {
            msf.hideSheet();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment("CALENDAR", true);
        } else if (id == R.id.nav_groups) {
            replaceFragment("GROUPS", true);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void replaceFragment(String TAG, boolean shouldAddBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        ViewFragment fragment = (ViewFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            switch (TAG) {
                case "CALENDAR":
                    fragment = CalendarFragment.newInstance(null, null);
                    //getSupportActionBar().setTitle("Events");
                    break;
                case "GROUPS":
                    fragment = GroupListFragment.newInstance(null, null);
                    //getSupportActionBar().setTitle("Groups");
                    break;
            }
        }
        if (shouldAddBackStack) {
            fm.beginTransaction().replace(R.id.content, fragment, TAG).addToBackStack(null).commit();
        } else {
            fm.beginTransaction().replace(R.id.content, fragment, TAG).commit();
        }
    }



    @Override
    public void onClick(View v) {
        switch (getResources().getResourceEntryName(v.getId())) {
            case "testAccountButton":
                setupTestAccount();
                break;
            case "fab_item_new_group":
                DialogFragment dialog = new CreateGroupDialogFragment();
                dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                break;
            case "fab_item_join_group":
                DialogFragment dialog1 = new JoinGroupDialogFragment();
                dialog1.show(getSupportFragmentManager(), "NoticeDialogFragment");
                break;
            case "fab_item_label":
                if (msf.isSheetVisible()) {
                    msf.hideSheet();
                }
                break;
        }
        msf.hideSheet();
    }


    @Override
    public void onLoadInteraction(String friendlyName, String tag) {
        currentFragmentFriendlyName = friendlyName;
        currentFragmentTag = tag;
        //updateUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onCreateGroupDialogPositiveClick(Group group) {

        mFirebaseDatabase.getReference()
                .child("groups")
                .child(group.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Group createdGroup = dataSnapshot.getValue(Group.class);

                            Map<String, String> admins = new HashMap<String, String>();
                            Map<String, String> users = new HashMap<String, String>();

                            admins.put(mCurrentUser.getUid(), mCurrentUser.getName());
                            users.put(mCurrentUser.getUid(), mCurrentUser.getName());
                            createdGroup.setAdmins(admins);
                            createdGroup.setUsers(users);

                            mFirebaseDatabase.getReference()
                                    .child("groups")
                                    .child(createdGroup.getName())
                                    .setValue(createdGroup);

                            Group simpleGroup = new Group(createdGroup.getName(),createdGroup.getDesc(), createdGroup.getCategory(), null, null, null, null);
                            mFirebaseDatabase.getReference()
                                    .child("users")
                                    .child(mCurrentUser.getUid())
                                    .child("groups")
                                    .child(createdGroup.getName())
                                    .setValue(simpleGroup);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
        ((GroupListFragment)getSupportFragmentManager().findFragmentByTag("GROUPS")).hideNoGroupText();
    }

    @Override
    public void onCreateGroupDialogNegativeClick() {

    }


    @Override
    public void onJoinGroupDialogPositiveClick(final String groupName) {
        mFirebaseDatabase.getReference()
                .child("groups")
                .child(groupName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Group group = dataSnapshot.getValue(Group.class);

                            mFirebaseDatabase.getReference()
                                    .child("groups")
                                    .child(group.getName())
                                    .child("users")
                                    .child(mCurrentUser.getUid())
                                    .setValue(mCurrentUser.getName());

                            Group simpleGroup = new Group(group.getName(),group.getDesc(), group.getCategory(), null, null, null, null);
                            mFirebaseDatabase.getReference()
                                    .child("users")
                                    .child(mCurrentUser.getUid())
                                    .child("groups")
                                    .child(group.getName())
                                    .setValue(simpleGroup);

                        }
                        else {
                            Log.d("Group not found!", groupName);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


    }

    @Override
    public void onJoinGroupDialogNegativeClick() {

    }

    public void setupTestAccount() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference()
                .child("users")
                .child("testaccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Datasnapshot", dataSnapshot.toString());
                        if (!dataSnapshot.exists()) {

                        } else {
                            mCurrentUser = dataSnapshot.getValue(User.class);
                            Log.d("wat", "wat");
                            setupLayout();
                            Log.d("wat", "wat1");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    public String getCurrentUserID(){
        return mCurrentUser.getUid();
    }

}
