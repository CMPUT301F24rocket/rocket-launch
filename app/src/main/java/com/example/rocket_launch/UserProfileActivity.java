package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the device's Android ID
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Bundle bundle = new Bundle();
        bundle.putString("androidID", androidId);

        // Fetch user data based on android_id
        fetchUserProfile(androidId);

        // Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setSelectedItemId(R.id.navigation_user_profile);
        bottomBarNavigation(bottomNav);

        // Set up button to open edit profile fragment
        Button editProfileButton = findViewById(R.id.edit_profile_button);
        EditProfileFragment editProfileFragment = new EditProfileFragment();

        editProfileFragment.setArguments(bundle);
        editProfileButton.setOnClickListener(view -> {
            findViewById(R.id.user_profile_body).setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.edit_profile_fragment_container, editProfileFragment)
                    .commit();
        });
    }

    private void fetchUserProfile(String androidId) {
        // Query Firestore to find the document with the specified android_id
        db.collection("user_info")
                .whereEqualTo("android_id", androidId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve user data from Firestore
                            String name = document.getString("userName");
                            String email = document.getString("userEmail");
                            String phone = document.getString("userPhoneNumber");
                            String facility = document.getString("userFacility");
                            Boolean admin = document.getBoolean("roles.admin");
                            Boolean entrant = document.getBoolean("roles.entrant");
                            Boolean organizer = document.getBoolean("roles.organizer");

                            // Update UI with retrieved data
                            TextView nameTextView = findViewById(R.id.user_name_textview);
                            TextView emailTextView = findViewById(R.id.user_email_textview);
                            TextView phoneTextView = findViewById(R.id.user_phone_textview);
                            TextView facilityTextView = findViewById(R.id.user_facility_textview);

                            nameTextView.setText(name);
                            emailTextView.setText(email);
                            phoneTextView.setText(phone);
                            facilityTextView.setText(facility);

                            // Display roles based on role values (optional)
                            if (admin != null && admin) {
                                // Show admin-specific elements if needed
                            }
                            if (entrant != null && entrant) {
                                // Show entrant-specific elements if needed
                            }
                            if (organizer != null && organizer) {
                                // Show organizer-specific elements if needed
                            }
                            break; // exit loop after the first document is found
                        }
                    } else {
                        Log.d(TAG, "No matching document found or task failed", task.getException());
                    }
                });
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
