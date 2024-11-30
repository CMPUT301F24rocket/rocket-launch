package com.example.rocket_launch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import org.osmdroid.config.Configuration;

/**
 * main activity that gets loaded on startup
 */
public class MainActivity extends AppCompatActivity {
    UsersDB usersDB;
    BottomNavigationView bottomNav;

    CreateEventFragment createEvent;
    UserEventsFragment userEvents;
    UserProfileFragment userProfile;
    NotificationsFragment notifications;


    NotificationHandler notificationHandler;


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

        View fragmentView = findViewById(R.id.fragment_frame);
        ViewCompat.setOnApplyWindowInsetsListener(fragmentView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0,0,0, 0);
            return insets;
        });

        View navBarView = findViewById((R.id.bottom_nav_view));
        ViewCompat.setOnApplyWindowInsetsListener(navBarView, (v,insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        //Referenced: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java), accessed 2024-11-24
        //Initializing osmdroid configuration
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "osmdroid_preferences",
                Context.MODE_PRIVATE
        );
        Configuration.getInstance().load(context, sharedPreferences);
        Configuration.getInstance().setUserAgentValue(context.getPackageName());

        // load fragments for navigation
        createEvent = new CreateEventFragment();
        userEvents = new UserEventsFragment();
        userProfile = new UserProfileFragment();
        notifications = new NotificationsFragment();

        // set up nav-bar
        bottomNav = findViewById(R.id.bottom_nav_view);

        usersDB = new UsersDB();

        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        usersDB.getUser(androidID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    checkUserRole(user);
                    setupNavBar(user.getRoles());

                    // Display the UserHomepageFragment
                    UserHomepageFragment frag = new UserHomepageFragment(user.getUserName(), user.getProfilePhotoPath());
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, frag) // Ensure R.id.fragment_frame is the container
                            .commit();
                } else {

                    user = new User();
                    user.setAndroidId(androidID);
                    usersDB.addUser(androidID, user);
                    SelectRolesFragment frag = new SelectRolesFragment(user.getRoles());
                    frag.setOnSuccessListener(new SelectRolesFragment.onSuccessListener() {
                        @Override
                        public void onSuccess(Roles roles) {
                            usersDB.setRoles(androidID, roles);
                            setupNavBar(roles);
                        }
                    });
                    frag.show(getSupportFragmentManager(), "Create New User");

                    // Display the StartUpFragment
                    StartUpFragment startfrag = new StartUpFragment(androidID, user, usersDB);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, startfrag) // Ensure R.id.fragment_frame is the container
                            .commit();

                }
            }
        }, e -> Log.w("Firebase", "Error getting user", e));

        // start notification handler
        notificationHandler = new NotificationHandler(MainActivity.this, androidID);
    }

    /**
     * check if the user has the admin role and navigate to AdminModeActivity if true
     * @param user
     *  user to check for admin role
     */
    private void checkUserRole(User user) {
        if (user.getRoles().isAdmin()) {
            Intent intent = new Intent(this, AdminModeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * function to properly set up navbar on load
     * @param roles
     *  a given user's roles
     */
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
    }


}