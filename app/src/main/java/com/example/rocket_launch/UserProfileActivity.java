package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserProfileActivity extends AppCompatActivity {

    User user;
    UsersDB usersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Get Android ID
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        usersDB = new UsersDB();

        Bundle bundle = new Bundle();
        bundle.putString("androidID", androidID);


        //Open edit profile fragment
        Button editProfileButton = findViewById(R.id.edit_profile_button);

        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.setArguments(bundle);

        editProfileButton.setOnClickListener(view ->{
            findViewById(R.id.user_profile_body).setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.edit_profile_fragment_container, editProfileFragment)
                    .commit();
        });

        //for navigation bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setSelectedItemId(R.id.navigation_user_profile);
        bottomBarNavigation(bottomNav);

    }

    private void bottomBarNavigation(BottomNavigationView bottomNav){
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                return true;
            } else if (item.getItemId() == R.id.navigation_user_events) {
                startActivity(new Intent(getApplicationContext(),UserEventsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_create_events) {
                startActivity(new Intent(getApplicationContext(),CreateEventActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_notifications) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                return true;
            } else if (item.getItemId() ==R.id.navigation_user_profile) {
                //current activity, do nothing
                return true;
            }
            return false;
        });
    }
}