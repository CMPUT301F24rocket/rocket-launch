package com.example.rocket_launch;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rocket_launch.nav_fragments.CreateEventFragment;
import com.example.rocket_launch.nav_fragments.NotificationsFragment;
import com.example.rocket_launch.nav_fragments.UserEventsFragment;
import com.example.rocket_launch.nav_fragments.UserProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    UsersDB usersDB;

    BottomNavigationView bottomNav;

    CreateEventFragment createEvent;
    UserEventsFragment userEvents;
    UserProfileFragment userProfile;
    NotificationsFragment notifications;


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


        EventDB eventDB = new EventDB(this);
        String testEventID = "testEvent";
        Event testEvent = new Event(testEventID, "Test Event", "Testing Firestore event addition", null, null, 20, null, 3);
        eventDB.addEvent(testEventID, testEvent);

        // load fragments for navigation
        createEvent = new CreateEventFragment();
        userEvents = new UserEventsFragment();
        userProfile = new UserProfileFragment();
        notifications = new NotificationsFragment();

        // set up nav-bar
        bottomNav = findViewById(R.id.bottom_nav_view);

        usersDB = new UsersDB(); // Load user database

        //Get Android Device ID
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // get user from firebase and set up navbar
        usersDB.getUser(androidID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user;
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class); // if user exists, use class repr
                    setupNavBar(user.getRoles()); // set up navbar given the user
                } else {
                    user = new User(); // make new user if not in database
                    user.setAndroid_id(androidID); //set Android ID for new user
                    usersDB.addUser(androidID, user);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("user_info").document(androidID);  // Reference the collection
                    new SelectRolesFragment(user.getRoles(), userRef).show(getSupportFragmentManager(), "Create New User");
                }
            }
                }, e -> {
                    Log.w("Firebase", "Error getting user", e);
                });
    }


    private void setupNavBar(Roles roles) {
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_user_profile) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, userProfile)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.navigation_create_events) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, createEvent)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.navigation_notifications) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, notifications)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.navigation_user_events) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, userEvents)
                            .commit();
                    return true;
                }
                return false;
            }
        });

        Menu menu = bottomNav.getMenu();
        if (!roles.isEntrant()) {
            // if not entrant, don't show user events
            menu.findItem(R.id.navigation_user_events).setVisible(false);
        }
        if (!roles.isOrganizer()) {
            // if not organizer don't show create events
            menu.findItem(R.id.navigation_create_events).setVisible(false);
        }

        if (roles.isAdmin()) {
            // goto admin activity when implemented
            bottomNav.setSelectedItemId(R.id.navigation_home);
        }
        else if (roles.isOrganizer()) {
            bottomNav.setSelectedItemId(R.id.navigation_create_events);
        }
        else if (roles.isEntrant()) {
            bottomNav.setSelectedItemId(R.id.navigation_user_events);
        }
    }
}