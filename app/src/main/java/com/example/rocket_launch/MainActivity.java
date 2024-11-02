package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
<<<<<<< HEAD

=======
import com.google.firebase.firestore.DocumentSnapshot;
>>>>>>> 8128ab930ae6ae4607599e7c50a1d7f3c93a9e03

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

        usersDB = new UsersDB(); // Load user database
        String androidId = "your_device_id"; // Replace with actual device ID

        // Get Firebase user
        usersDB.getUser(androidId, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    checkUserRole(user);
                } else {
                    user = new User();
                    new NewUserFragment(user).show(getSupportFragmentManager(), "Create New User");
                }
            }
        }, e -> {
            Log.w("Firebase", "Error getting user", e);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
<<<<<<< HEAD

        //setBarNavigationDisplay(bottomNav, user);

=======
>>>>>>> 8128ab930ae6ae4607599e7c50a1d7f3c93a9e03
        bottomNav.setSelectedItemId(R.id.navigation_home);
        bottomBarNavigation(bottomNav);
<<<<<<< HEAD

        UsersDB usersDB = new UsersDB(); // load user database
        final User[] user = {usersDB.getUser()}; // try to get firebase user (returns null for now)

        // if user is null, make a new one and prompt for user information

        Button get_started_button = findViewById(R.id.get_started);
        get_started_button.setOnClickListener(v -> {
            if (user[0] == null) {
                user[0] = new User();
                new NewUserFragment(user[0]).show(getSupportFragmentManager(), "Create New User");
            }

            // User is an entrant (the user joins or signs up for events)
            if (user[0].isEntrant()) {


            } else if (user[0].isOrganizer()) {


            } else if (user[0].isAdmin()) {


            } else {
                System.out.print("Error");
            }
        });
    };
=======
    }
>>>>>>> 8128ab930ae6ae4607599e7c50a1d7f3c93a9e03

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
