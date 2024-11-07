package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.EditProfileFragment;
import com.example.rocket_launch.R;
import com.example.rocket_launch.Roles;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";
    private FirebaseFirestore db;
    private String androidId;
    private String currentName, currentEmail, currentPhone, currentFacility;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView facilityTextView;
    private LinearLayout facilityLayout;
    private ConstraintLayout profileBodyView;

    private Roles roles;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        profileBodyView = view.findViewById(R.id.user_profile_body);

        nameTextView = view.findViewById(R.id.user_name_textview);
        emailTextView = view.findViewById(R.id.user_email_textview);
        phoneTextView = view.findViewById(R.id.user_phone_textview);
        facilityTextView = view.findViewById(R.id.user_facility_textview);

        facilityLayout = view.findViewById(R.id.display_profile_facility);


        // Set up button to open edit profile fragment
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(v -> {
            view.findViewById(R.id.user_profile_body).setVisibility(View.GONE);
            openEditProfileFragment();
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch initial user data from Firestore
        fetchUserProfile();
    }

    private void openEditProfileFragment() {
        // Pass current user data to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("androidID", androidId);
        bundle.putString("name", currentName);
        bundle.putString("email", currentEmail);
        bundle.putString("phone", currentPhone);
        bundle.putString("facility", currentFacility);

        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.edit_profile_fragment_container, editProfileFragment)
//                .addToBackStack(null)  // Add to back stack so we can come back to this activity
                .commit();
    }

    private void fetchUserProfile() {
        db.collection("user_info")
                .whereEqualTo("android_id", androidId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve user data from Firestore
                            currentName = document.getString("userName");
                            currentEmail = document.getString("userEmail");
                            currentPhone = document.getString("userPhoneNumber");
                            currentFacility = document.getString("userFacility");

                            roles = document.get("roles", Roles.class);
                            assert roles != null;
                            if (roles.isOrganizer()) {
                                facilityLayout.setVisibility(View.VISIBLE);
                                facilityTextView.setText(currentFacility);
                            }

                            // Update UI with retrieved data
                            nameTextView.setText(currentName);
                            emailTextView.setText(currentEmail);
                            phoneTextView.setText(currentPhone);
                            break;
                        }
                    } else {
                        Log.d(TAG, "No matching document found or task failed", task.getException());
                    }
                });
    }
}
