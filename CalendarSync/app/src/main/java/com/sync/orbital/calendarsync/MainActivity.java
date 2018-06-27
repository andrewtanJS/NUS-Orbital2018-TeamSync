package com.sync.orbital.calendarsync;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView myMainNav;
    private FrameLayout myMainFrame;

    private CalendarFragment calendarFragment;
    private EventFragment eventFragment;
    private ContactFragment contactFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        myMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        calendarFragment = new CalendarFragment();
        eventFragment = new EventFragment();
        contactFragment = new ContactFragment();

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
}
