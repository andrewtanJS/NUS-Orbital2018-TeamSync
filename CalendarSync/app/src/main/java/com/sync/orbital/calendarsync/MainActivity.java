package com.sync.orbital.calendarsync;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView myMainNav;
    private FrameLayout myMainFrame;

    private CalendarFragment calendarFragment;
    private EventFragment eventFragment;
    private ContactFragment contactFragment;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        myMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        myMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        calendarFragment = new CalendarFragment();
        eventFragment = new EventFragment();
        contactFragment = new ContactFragment();
        loginFragment = new LoginFragment();

        setFragment(calendarFragment);

        myMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.nav_cal :
                        setFragment(calendarFragment);
                        return true;
                    case R.id.nav_event :
                        setFragment(eventFragment);
                        return true;
                    case R.id.nav_cont :
                        setFragment(contactFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });


    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Go to settings
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return true;
    }
}
