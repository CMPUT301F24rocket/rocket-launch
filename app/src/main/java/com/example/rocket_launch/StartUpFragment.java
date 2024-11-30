package com.example.rocket_launch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;

public class StartUpFragment extends Fragment {

    private EditText nameEditTextStartup, emailEditTextStartup, phoneEditTextStartup;
    private String androidID;
    private UsersDB usersDBStartup;
    private User user;
    private Button finishStartup;

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

        finishStartup = view.findViewById(R.id.startup_button);
        finishStartup.setOnClickListener(v -> {saveUserDetails();});
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
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set username, email and phone number
        user.setUserName(name);
        user.setUserEmail(email);
        user.setUserPhoneNumber(phone);
        usersDBStartup.updateUser(androidID, user,
                success -> {
                    Toast.makeText(getContext(), "New user created", Toast.LENGTH_SHORT).show();

                    UserHomepageFragment frag = new UserHomepageFragment(user.getUserName(), user.getProfilePhotoPath());
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, frag) // Ensure R.id.fragment_frame is the container
                            .commit();
                },
                error -> Log.e(TAG, "failed to update user details", error));
    }

}


