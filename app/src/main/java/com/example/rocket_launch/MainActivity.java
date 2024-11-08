package com.example.rocket_launch;

import android.content.Intent;
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

import com.example.rocket_launch.admin.AdminModeActivity;
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

        // load fragments for navigation
        createEvent = new CreateEventFragment();
        userEvents = new UserEventsFragment();
        userProfile = new UserProfileFragment();
        notifications = new NotificationsFragment();

        // set up nav-bar
        bottomNav = findViewById(R.id.bottom_nav_view);

        usersDB = new UsersDB();

        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        usersDB.getUser(androidID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user;
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    checkUserRole(user);
                    setupNavBar(user.getRoles());
                } else {
                    user = new User();
                    user.setAndroid_id(androidID);
                    usersDB.addUser(androidID, user);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("user_info").document(androidID);  // Reference the collection
                    SelectRolesFragment frag = new SelectRolesFragment(user.getRoles(), userRef);
                    frag.setOnSuccessListener(new SelectRolesFragment.onSuccessListener() {
                        @Override
                        public void onSuccess() {
                            setupNavBar(user.getRoles());
                        }
                    });
                    frag.show(getSupportFragmentManager(), "Create New User");
                }
            }
        }, e -> Log.w("Firebase", "Error getting user", e));
    }

    // check if the user has the admin role and navigate to AdminModeActivity if true
    private void checkUserRole(User user) {
        if (user.isAdmin()) {
            Intent intent = new Intent(this, AdminModeActivity.class);
            startActivity(intent);
            finish();
        }
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
            // if not organizer, don't show create events
            menu.findItem(R.id.navigation_create_events).setVisible(false);
        }

        if (roles.isAdmin()) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
        } else if (roles.isOrganizer()) {
            //bottomNav.setSelectedItemId(R.id.navigation_create_events);
        } else if (roles.isEntrant()) {
            bottomNav.setSelectedItemId(R.id.navigation_user_events);
        }
    }
}