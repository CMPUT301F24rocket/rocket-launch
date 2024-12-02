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

import org.checkerframework.checker.regex.qual.Regex;

import java.util.Objects;

/**
 * Fragment shown on first log in to get user information
 * Author: Nathan
 *         Kaiden
 */
public class StartUpFragment extends Fragment {
    private String androidID;
    private UsersDB usersDBStartup;
    private User user;

    // UI
    private Button finishStartup;
    // Entrant
    private EditText nameEditTextStartup, emailEditTextStartup, phoneEditTextStartup;
    // Organizer
    private ConstraintLayout organizerStartupFields;
    private EditText facilityName, facilityAddress;

    private static final String TAG = "StartUpFragment";

    /**
     * Default constructor for StartUpFragment
     * @param androidID androidID for user
     * @param user Current user
     * @param usersDB UserDB instance
     * Author: Nathan
     */
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
        facilityName = view.findViewById(R.id.edit_user_facility);
        facilityAddress = view.findViewById(R.id.edit_user_facility_address);

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
     * Saves user details on startup page to firestore
     * Author: Nathan
     *         Kaiden
     */
    private void saveUserDetails() {

        String name = nameEditTextStartup.getText().toString();
        String email = emailEditTextStartup.getText().toString();
        String phone = phoneEditTextStartup.getText().toString();

        boolean validData = true;
        // validate for user roles
        if (user.getRoles().isEntrant()) {
            // Validate that user input isn't empty
            if (name.isEmpty()) {
                nameEditTextStartup.setError("Name cannot be empty");
                validData = false;
            }

            // Validate that user enters correct email
            String emailPattern = "[a-zA-Z\\d._%+-]+@[a-z\\d.-]+\\.[a-zA-Z]{2,}";
            if (email.isEmpty()) {
                emailEditTextStartup.setError("Email cannot be empty");
                validData = false;
            }

            // Checks if email matches email pattern
            if (!email.matches(emailPattern)) {
                Toast.makeText(getContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                validData = false;
            }

            // Validate that user enters correct phone pattern
            String phonePattern = "\\+?[1-9]\\d{0,3}(-\\d{1,4}){1,3}";;

            // Checks if phone number matches phone number pattern
            if (!phone.isEmpty()) {
                if (!phone.matches(phonePattern)) {
                    validData = false;
                    Toast.makeText(getContext(), "Enter a valid phone number (with dasher)", Toast.LENGTH_SHORT).show();
                }
            }
        }

        String facilityNameText = facilityName.getText().toString();
        String facilityAddressText = facilityAddress.getText().toString();

        // validate for organizer roles
        if (user.getRoles().isOrganizer()) {
            if (facilityNameText.isEmpty()) {
                facilityName.setError("facility name cannot be empty");
                validData = false;
            }

            if (facilityAddressText.isEmpty()) {
                facilityAddress.setError("facility address cannot be empty");
                validData = false;
            }
        }

        if (validData) {
            // Set username, email and phone number
            user.setUserName(name);
            user.setUserEmail(email);
            user.setUserPhoneNumber(phone);
            user.setUserFacility(facilityNameText);
            user.setUserFacilityAddress(facilityAddressText);

            usersDBStartup.updateUser(androidID, user,
                    success -> {
                        Toast.makeText(getContext(), "New user created", Toast.LENGTH_SHORT).show();

                        UserHomepageFragment frag = new UserHomepageFragment(user.getUserName(), user.getProfilePhotoPath());
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_frame, frag) // Ensure R.id.fragment_frame is the container
                                .commit();

                        Intent intent = requireActivity().getIntent();
                        requireActivity().finish();
                        startActivity(intent);
                    },
                    error -> Log.e(TAG, "failed to update user details", error));
        }
    }
}
