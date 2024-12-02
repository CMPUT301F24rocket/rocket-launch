package com.example.rocket_launch.admin;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rocket_launch.MainActivity;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity displayed for admin users, providing a tabbed interface for managing app data.
 * Author: Pouyan
 */
public class AdminModeActivity extends AppCompatActivity {
    private UsersDB usersDB; // Database handler for user data
    private User user; // Holds the currently logged-in user
    private ImageButton mainActivityButton; // Button to navigate back to the main activity

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     * Author: Pouyan
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize database and user objects
        usersDB = new UsersDB();
        user = new User();

        // Set up UI components
        TabLayout tabLayout = findViewById(R.id.admin_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.admin_view_pager);
        mainActivityButton = findViewById(R.id.back_to_main_activity_button);

        // Fetch the user information
        getUser();

        // Set up button to navigate back to the main activity
        mainActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the admin activity
        });

        // Set up the ViewPager adapter for admin tabs
        AdminModePagerAdapter adapter = new AdminModePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach the TabLayout to the ViewPager with labels for each tab
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Events");
                    break;
                case 1:
                    tab.setText("Profiles");
                    break;
                case 2:
                    tab.setText("Images");
                    break;
                case 3:
                    tab.setText("QR Data");
                    break;
                case 4:
                    tab.setText("Facilities");
                    break;
            }
        }).attach();
    }

    /**
     * Retrieves the logged-in user's information from Firestore.
     * Author: Pouyan
     */
    private void getUser() {
        // Get the device's unique Android ID
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch the user object from Firestore using the Android ID
        usersDB.getUser(androidID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                if (newUser != null) {
                    user = newUser;

                    // Show or hide the back button based on user roles
                    if (user.getRoles().isEntrant() || user.getRoles().isOrganizer()) {
                        mainActivityButton.setVisibility(View.VISIBLE);
                    } else {
                        mainActivityButton.setVisibility(View.GONE);
                    }
                }
            }
        }, e -> Log.w("Firebase", "Error getting user", e)); // Log any errors encountered during retrieval
    }
}
