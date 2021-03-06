package com.sync.orbital.calendarsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView myMainNav;
    private FrameLayout myMainFrame;

    private CalendarFragment calendarFragment;
    private EventFragment eventFragment;
    private ContactFragment contactFragment;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!((CalendarSyncApplication) this.getApplication()).isPersistent()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            ((CalendarSyncApplication) this.getApplication()).setPersistent(true);
        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        myMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        myMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        eventFragment = new EventFragment();
        contactFragment = new ContactFragment();
        calendarFragment = new CalendarFragment();

        myMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_cal :
                        ((CalendarSyncApplication) getApplication())
                                .setCurrentFragment(calendarFragment);
                        setFragment(calendarFragment);
                        return true;
                    case R.id.nav_event :
                        ((CalendarSyncApplication) getApplication())
                                .setCurrentFragment(eventFragment);
                        setFragment(eventFragment);
                        return true;
                    case R.id.nav_cont :
                        ((CalendarSyncApplication) getApplication())
                                .setCurrentFragment(contactFragment);
                        setFragment(contactFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            setFragment(calendarFragment);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (((CalendarSyncApplication) getApplication())
                .getCurrentFragment() != null) {
            setFragment(((CalendarSyncApplication) getApplication())
                    .getCurrentFragment());
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.action_items, menu);
//        return true;
//    }


}
