package com.example.rocket_launch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, facilityEditText;
    private User user;
    private UsersDB usersDB;
    String androidID;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usersDB = new UsersDB();
            androidID = getArguments().getString("androidID");

            //call UserDB
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

        //Initiate Text Editing parameters

        nameEditText = view.findViewById(R.id.edit_user_name);
        emailEditText = view.findViewById(R.id.edit_user_email);
        phoneEditText = view.findViewById(R.id.edit_user_phone);
        facilityEditText = view.findViewById(R.id.edit_user_facility);
        Button saveButton = view.findViewById(R.id.save_profile_edit_button);

        loadUserDetails();
        saveButton.setOnClickListener(v -> updateUserDetails());

        //Edit Profile Picture Button
        Button editProfilePictureButton = view.findViewById(R.id.edit_profile_picture_button);

        editProfilePictureButton.setOnClickListener(view1 -> {

        });

        //Edit Roles Button
        Button editRolesButton = view.findViewById(R.id.edit_user_role_button);

        editRolesButton.setOnClickListener(view1 -> {

        });
        
        return view;
    }

    //For loading user data
    private void loadUserDetails(){
        usersDB.getUser(androidID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user = documentSnapshot.toObject(User.class);
                    if (user != null){
                        nameEditText.setText(user.getUserName());
                        emailEditText.setText(user.getUserEmail());
                        phoneEditText.setText(user.getUserPhoneNumber());
                        facilityEditText.setText(user.getUserFacility());
                    }
                }
            }
        }, e -> Log.e("Edit profile Fragment", "Error loading user data", e));
    }

    //Update user data from edit text field inputs
    private void updateUserDetails(){
        if (user != null){
            user.setUserName(nameEditText.getText().toString());
            user.setUserEmail(emailEditText.getText().toString());
            user.setUserPhoneNumber(phoneEditText.getText().toString());
            user.setUserFacility(facilityEditText.getText().toString());

            //Update Firestore userDB
            usersDB.updateUser(androidID, user);
        }
    }
}

