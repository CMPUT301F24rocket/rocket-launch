package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

/**
 * Fragment shown on first log in to get user information
 * Author: Nathan
 */
public class StartUpFragment extends Fragment {

    private EditText nameEditTextStartup, emailEditTextStartup, phoneEditTextStartup;
    private String androidID;
    private UsersDB usersDBStartup;
    private User user;
    private Button finishStartup;
    private ConstraintLayout organizerStartupFields;

    private static final String TAG = "StartUpFragment";

    public StartUpFragment(String androidID, User user, UsersDB usersDB) {
        this.androidID = androidID;
        this.user = user;
        this.usersDBStartup = usersDB;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.startup, container, false);

        // Initialize UI components
        nameEditTextStartup = view.findViewById(R.id.edit_user_name);
        emailEditTextStartup = view.findViewById(R.id.edit_user_email);
        phoneEditTextStartup = view.findViewById(R.id.edit_user_phone);
        organizerStartupFields = view.findViewById(R.id.organizer_startup_fields);

        finishStartup = view.findViewById(R.id.startup_button);
        finishStartup.setOnClickListener(v -> saveUserDetails());

        // Conditionally render facility and facility address
        if (user.getRoles().isOrganizer()) {
            organizerStartupFields.setVisibility(View.VISIBLE);
        } else {
            organizerStartupFields.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Update user details in Firestore
     */
    private void saveUserDetails() {

        String name = nameEditTextStartup.getText().toString();
        String email = emailEditTextStartup.getText().toString();
        String phone = phoneEditTextStartup.getText().toString();

        // Validate that user input isn't empty
        boolean validData = true;
        if (name.isEmpty()) {
            nameEditTextStartup.setError("Name cannot be empty");
            validData = false;
        }
        if (email.isEmpty()) {
            emailEditTextStartup.setError("Email cannot be empty");
            validData = false;
        }

        if (validData) {
            // Set username, email and phone number
            user.setUserName(name);
            user.setUserEmail(email);
            user.setUserPhoneNumber(phone);

            if (user.getRoles().isOrganizer()) {
                EditText facilityEditText = requireView().findViewById(R.id.edit_user_facility);
                EditText facilityAddressEditText = requireView().findViewById(R.id.edit_user_facility_address);

                user.setUserFacility(facilityEditText.getText().toString());
                user.setUserFacilityAddress(facilityAddressEditText.getText().toString());
            }

            usersDBStartup.updateUser(androidID, user,
                    success -> {
                        Toast.makeText(getContext(), "New user created", Toast.LENGTH_SHORT).show();

                        UserHomepageFragment frag = new UserHomepageFragment(user.getUserName(), user.getProfilePhotoPath());
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_frame, frag) // Ensure R.id.fragment_frame is the container
                                .commit();

                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    },
                    error -> Log.e(TAG, "failed to update user details", error));
        }
    }
}
