package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    User user;
    UsersDB usersDB;


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


        EventDB eventDB = new EventDB();
        String testEventID = "testEvent";
        Event testEvent = new Event(testEventID, "Test Event", "Testing Firestore event addition", null, null, 20, null, 3);
        eventDB.addEvent(testEventID, testEvent);


        usersDB = new UsersDB(); // Load user database

        //Get Android Device ID
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Button get_started_button = findViewById(R.id.get_started);
        get_started_button.setOnClickListener(v -> {

            // Get Firebase user
            usersDB.getUser(androidID, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        checkUserRole(user);
                    } else {
                        user = new User();
                        user.setAndroid_id(androidID); //set Android ID for new user
                        new NewUserFragment(user, usersDB).show(getSupportFragmentManager(), "Create New User");
                        usersDB.addUser(androidID, user);
                    }
                }
                    }, e -> {
                        Log.w("Firebase", "Error getting user", e);
                    });
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setSelectedItemId(R.id.navigation_home);
        bottomBarNavigation(bottomNav);
    }

    private void checkUserRole(User user) {
        if (user.isEntrant()) {
            // Go to entrant activity
        } else if (user.isOrganizer()) {
            // Go to organizer activity
        } else if (user.isAdmin()) {
            // Go to admin activity
        }
    }

    private void bottomBarNavigation(BottomNavigationView bottomNav) {
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                return true;
            } else if (item.getItemId() == R.id.navigation_user_events) {
                startActivity(new Intent(getApplicationContext(), UserEventsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_create_events) {
                startActivity(new Intent(getApplicationContext(), CreateEventActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_notifications) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_user_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setBarNavigationDisplay(BottomNavigationView bottomNav, User user) {
        Menu menu = bottomNav.getMenu();

        // Hide menu items based on user roles
        if (!user.getRoles().isEntrant()) {
            menu.findItem(R.id.navigation_user_events).setVisible(false);
            menu.findItem(R.id.navigation_notifications).setVisible(false);
            menu.findItem(R.id.navigation_user_profile).setVisible(false);
        }

        if (!user.getRoles().isOrganizer()) {
            menu.findItem(R.id.navigation_create_events).setVisible(false);
        }
    }
}
