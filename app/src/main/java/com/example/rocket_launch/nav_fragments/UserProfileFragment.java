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
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * fragment for displaying all user profile information
 */
public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";
    // user data
    private UsersDB usersDB;
    private String androidId;
    private User user;

    // user interface items
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView facilityTextView;
    private LinearLayout facilityLayout;
    private ConstraintLayout profileBodyView;
    private ImageView profileImageView;
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
        usersDB = new UsersDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * opens a fragment used for editing contents of a user's profile
     */
    private void openEditProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("androidID", androidId);

        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.edit_profile_fragment_container, editProfileFragment)
                .commit();
    }

    /**
     * function that gets and displays all contents of a user's profile
     */
    private void fetchUserProfile() {
        usersDB.getUser(androidId, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                user = newUser;
                if (user.getRoles() != null && user.getRoles().isOrganizer()) {
                    facilityLayout.setVisibility(View.VISIBLE);
                    facilityTextView.setText(user.getUserFacility());
                }

                nameTextView.setText(user.getUserName());
                emailTextView.setText(user.getUserEmail());
                phoneTextView.setText(user.getUserPhoneNumber());

                if (user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                    loadProfileImage(user.getProfilePhotoPath());
                } else {
                    profilePictureView.setImageResource(R.drawable.default_image);
                }
            }
        }, e -> Log.e(TAG, "No matching document found or task failed", e));
    }

    /**
     * function that loads a user's profile photo
     * @param imagePath
     *  path to profile photo in database
     */
    private void loadProfileImage(String imagePath) {
        Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.default_image) // Use a default image if the path is null
                .into(profilePictureView);
    }


    /**
     * function used to update user interface if change occurred
     */
    private void updateUI() {
        // Set the user details
        nameTextView.setText(user.getUserName());
        emailTextView.setText(user.getUserEmail());
        phoneTextView.setText(user.getUserPhoneNumber());

        // Display facility if user is an organizer
        if (user.getRoles() != null && user.getRoles().isOrganizer()) {
            facilityLayout.setVisibility(View.VISIBLE);
            facilityTextView.setText(user.getUserFacility());
        } else {
            facilityLayout.setVisibility(View.GONE);
        }

        // Load profile picture using Glide
        if (user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePhoto())
                    .placeholder(R.drawable.default_image) // Optional placeholder image
                    .error(R.drawable.default_image) // Optional error image
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.default_image); // Set default image if no path is found
        }
    }
}
