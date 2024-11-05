package com.example.rocket_launch;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    public interface OnProfileUpdatedListener {
        void onProfileUpdated();
    }

    private OnProfileUpdatedListener profileUpdatedListener;
    private EditText nameEditText, emailEditText, phoneEditText, facilityEditText;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String androidID;
    private Roles roles;
    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the activity implements the listener
        if (context instanceof OnProfileUpdatedListener) {
            profileUpdatedListener = (OnProfileUpdatedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnProfileUpdatedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Retrieve androidID from arguments
        if (getArguments() != null) {
            androidID = getArguments().getString("androidID");

            // Reference to the user's document in Firestore based on androidID
            userRef = db.collection("user_info").document(androidID);
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

        // Load existing user details into the fields
        loadUserDetails();

        // Set up edit roles button
        Button editRolesButton = view.findViewById(R.id.edit_user_role_button);

        editRolesButton.setOnClickListener(v -> {
            NewUserFragment edit_roles_frag = new NewUserFragment(roles);
            getActivity().getSupportFragmentManager().beginTransaction()
        });

        // Set up the save button
        Button saveButton = view.findViewById(R.id.save_profile_edit_button);
        saveButton.setOnClickListener(v -> updateUserDetails());

        // Set up cancel button
        Button cancelButton = view.findViewById(R.id.cancel_profile_edit_button);
        cancelButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    // Load user data from Firestore and set it in the EditText fields
    private void loadUserDetails() {
        if (userRef != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Set the fetched data in the EditText fields
                    nameEditText.setText(documentSnapshot.getString("userName"));
                    emailEditText.setText(documentSnapshot.getString("userEmail"));
                    phoneEditText.setText(documentSnapshot.getString("userPhoneNumber"));
                    facilityEditText.setText(documentSnapshot.getString("userFacility"));
                    Object doc = documentSnapshot.get("roles");
                    roles = new Roles();
                    roles.fromDB((HashMap) doc);

                } else {
                    Log.e("EditProfileFragment", "User document does not exist.");
                }
            }).addOnFailureListener(e -> Log.e("EditProfileFragment", "Error loading user data", e));
        } else {
            Log.e("EditProfileFragment", "userRef is null.");
        }
    }

    // Update user data in Firestore
    private void updateUserDetails() {
        if (userRef != null) {
            // Retrieve text from EditText fields
            String updatedName = nameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();
            String updatedPhone = phoneEditText.getText().toString();
            String updatedFacility = facilityEditText.getText().toString();

            // Prepare a map of updated values
            userRef.update("userName", updatedName,
                            "userEmail", updatedEmail,
                            "userPhoneNumber", updatedPhone,
                            "userFacility", updatedFacility)
                    .addOnSuccessListener(aVoid -> {
                        // Show success message
                        Snackbar.make(requireView(), "Profile updated successfully", Snackbar.LENGTH_SHORT).show();

                        // Notify the activity that the profile was updated
                        if (profileUpdatedListener != null) {
                            profileUpdatedListener.onProfileUpdated();
                        }

                        // Close the fragment
                        closeFragment();
                    })
                    .addOnFailureListener(e -> Log.e("EditProfileFragment", "Error updating user data", e));
        } else {
            Log.e("EditProfileFragment", "userRef is null, cannot update.");
        }
    }

    // Close the fragment and show the main profile view
    private void closeFragment() {
        requireActivity().findViewById(R.id.user_profile_body).setVisibility(View.VISIBLE); // Show the user profile view
        requireActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }
}
