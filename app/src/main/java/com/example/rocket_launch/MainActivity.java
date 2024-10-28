package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //for navigation bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setSelectedItemId(R.id.navigation_home);

        //navigating to different activities using the bottom nav bar
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                //current activity, no nothing
                return true;
            } else if (item.getItemId() == R.id.navigation_user_events) {
                startActivity(new Intent(getApplicationContext(),UserEventsActivity.class));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_create_events) {
                startActivity(new Intent(getApplicationContext(),CreateEventActivity.class));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_notifications) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_user_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });



        UsersDB usersDB = new UsersDB(); // load user database
        User user = usersDB.getUser(); // try to get firebase user (returns null for now)

        // if user is null, make a new one and prompt for user information

        if (user == null) {
            user = new User();
            new NewUserFragment(user).show(getSupportFragmentManager(), "Create New User");
        }

        if (user.isEntrant()) {
            // goto entrant activity
        }
        else if (user.isOrganizer()) {
            // goto organizer activity
        }
        else if (user.isAdmin()) {
            // goto admin activity
        }
        else {
            // we were not supposed to get here
        }

    }
}