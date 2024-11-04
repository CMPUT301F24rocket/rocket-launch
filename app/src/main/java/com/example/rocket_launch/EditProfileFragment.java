package com.example.rocket_launch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

public class EditProfileFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, facilityEditText;
    private User user;
    private UsersDB usersDB;
    private String androidID;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UsersDB instance
        usersDB = new UsersDB();

        if (getArguments() != null) {
            androidID = getArguments().getString("androidID");

            // Fetch user data from Firestore
            usersDB.getUser(androidID, documentSnapshot -> {
                user = documentSnapshot.toObject(User.class);
            }, e -> Log.e("FirestoreError", "Error getting user data", e));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        // Initialize Text Editing fields
        nameEditText = view.findViewById(R.id.edit_user_name);
        emailEditText = view.findViewById(R.id.edit_user_email);
        phoneEditText = view.findViewById(R.id.edit_user_phone);
        facilityEditText = view.findViewById(R.id.edit_user_facility);

        // Set up the save button
        Button saveButton = view.findViewById(R.id.save_profile_edit_button);
        saveButton.setOnClickListener(v -> {
            Log.d("EditProfileFragment", "Save button clicked!");
            updateUserDetails();

            requireActivity().findViewById(R.id.user_profile_body).setVisibility(View.VISIBLE); //get user profile activity back
            //close this fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });

        //Set up cancel button
        Button cancelButton = view.findViewById(R.id.cancel_profile_edit_button);
        cancelButton.setOnClickListener(v -> {
            requireActivity().findViewById(R.id.user_profile_body).setVisibility(View.VISIBLE); //get user profile activity back
            //close this fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });

        // Load existing user details into the fields
        loadUserDetails();

        // Initialize Edit Profile Picture and Roles buttons (optional functionality)
        Button editProfilePictureButton = view.findViewById(R.id.edit_profile_picture_button);
        editProfilePictureButton.setOnClickListener(view1 -> {
            // Add code to handle profile picture editing
        });

        Button editRolesButton = view.findViewById(R.id.edit_user_role_button);
        editRolesButton.setOnClickListener(view1 -> {
            // Add code to handle role editing
        });

        return view;
    }

    // Load user data from Firestore
    private void loadUserDetails() {
        if (androidID != null && usersDB != null) {
            usersDB.getUser(androidID, documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        nameEditText.setText(user.getUserName());
                        emailEditText.setText(user.getUserEmail());
                        phoneEditText.setText(user.getUserPhoneNumber());
                        facilityEditText.setText(user.getUserFacility());
                    }
                }
            }, e -> Log.e("EditProfileFragment", "Error loading user data", e));
        } else {
            Log.e("EditProfileFragment", "androidID or usersDB is null.");
        }
    }

    // Update user data in Firestore
    private void updateUserDetails() {
        if (user != null) {
            // Update user object with input from EditText fields
            user.setUserName(nameEditText.getText().toString());
            user.setUserEmail(emailEditText.getText().toString());
            user.setUserPhoneNumber(phoneEditText.getText().toString());
            user.setUserFacility(facilityEditText.getText().toString());

            // Update Firestore document
            usersDB.updateUser(androidID, user);


            // Provide feedback to the user
            Snackbar.make(requireView(), "Profile updated successfully", Snackbar.LENGTH_SHORT).show();
        } else {
            Log.e("EditProfileFragment", "User object is null, cannot update.");
        }
    }
}
