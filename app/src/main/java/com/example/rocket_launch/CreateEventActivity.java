package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_event_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //for navigation bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setSelectedItemId(R.id.navigation_create_events);
        bottomBarNavigation(bottomNav);

        //Set up button to open CreateNewEventFragment
        FloatingActionButton addNewEventButton = findViewById(R.id.add_new_event_button);
        addNewEventButton.setOnClickListener(view -> {
            findViewById(R.id.created_events_body).setVisibility(View.GONE);
            openCreateNewEventFragment();
        });

        //TODO: Display list of events user has created
        // Click on created event in list and:
        // Menu where they can
        // - Edit Event Details
        // - View Entrant list
        // - View Entrant map
        // - See Event QR code

    }

    private void openCreateNewEventFragment(){
        CreateNewEventFragment createNewEventFragment = new CreateNewEventFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.create_new_event_fragment_container, createNewEventFragment)
                .addToBackStack(null)
                .commit();
    }

    private void bottomBarNavigation(BottomNavigationView bottomNav){
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                return true;
            } else if (item.getItemId() == R.id.navigation_user_events) {
                startActivity(new Intent(getApplicationContext(),UserEventsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_create_events) {
                //current activity, do nothing
                return true;
            } else if (item.getItemId() ==R.id.navigation_notifications) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_user_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            }
            return false;
        });
    }
}