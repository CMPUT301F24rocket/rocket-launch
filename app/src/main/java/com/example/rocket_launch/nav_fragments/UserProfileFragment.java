package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.EditProfileFragment;
import com.example.rocket_launch.R;
import com.example.rocket_launch.Roles;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";
    private FirebaseFirestore db;
    private String androidId;
    private String currentName, currentEmail, currentPhone, currentFacility, profilePhotoPath;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView facilityTextView;
    private LinearLayout facilityLayout;
    private ConstraintLayout profileBodyView;
    private ImageView profileImageView;
    private Roles roles;
    private ImageView profilePictureView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        profileBodyView = view.findViewById(R.id.user_profile_body);
        nameTextView = view.findViewById(R.id.user_name_textview);
        emailTextView = view.findViewById(R.id.user_email_textview);
        phoneTextView = view.findViewById(R.id.user_phone_textview);
        facilityTextView = view.findViewById(R.id.user_facility_textview);
        profilePictureView = view.findViewById(R.id.profile_picture_display); // Initialize ImageView
        facilityLayout = view.findViewById(R.id.display_profile_facility);

        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(v -> {
            view.findViewById(R.id.user_profile_body).setVisibility(View.GONE);
            openEditProfileFragment();
        });

        fetchUserProfile();

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
                .commit();
    }

    private void fetchUserProfile() {
        db.collection("user_info")
                .document(androidId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        currentName = document.getString("userName");
                        currentEmail = document.getString("userEmail");
                        currentPhone = document.getString("userPhoneNumber");
                        currentFacility = document.getString("userFacility");
                        String profilePhotoPath = document.getString("profilePhotoPath");

                        roles = document.get("roles", Roles.class);
                        if (roles != null && roles.isOrganizer()) {
                            facilityLayout.setVisibility(View.VISIBLE);
                            facilityTextView.setText(currentFacility);
                        }

                        nameTextView.setText(currentName);
                        emailTextView.setText(currentEmail);
                        phoneTextView.setText(currentPhone);

                        if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                            loadProfileImage(profilePhotoPath);
                        } else {
                            profilePictureView.setImageResource(R.drawable.default_image);
                        }
                    } else {
                        Log.e(TAG, "No matching document found or task failed", task.getException());
                    }
                });
    }


    private void loadProfileImage(String imagePath) {
        Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.default_image) // Use a default image if the path is null
                .into(profilePictureView);
    }




    private void updateUI() {
        // Set the user details
        nameTextView.setText(currentName);
        emailTextView.setText(currentEmail);
        phoneTextView.setText(currentPhone);

        // Display facility if user is an organizer
        if (roles != null && roles.isOrganizer()) {
            facilityLayout.setVisibility(View.VISIBLE);
            facilityTextView.setText(currentFacility);
        } else {
            facilityLayout.setVisibility(View.GONE);
        }

        // Load profile picture using Glide
        if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            Glide.with(this)
                    .load(profilePhotoPath)
                    .placeholder(R.drawable.default_image) // Optional placeholder image
                    .error(R.drawable.default_image) // Optional error image
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.default_image); // Set default image if no path is found
        }
    }
}
