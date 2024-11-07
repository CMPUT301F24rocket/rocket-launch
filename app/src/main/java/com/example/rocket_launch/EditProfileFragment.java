package com.example.rocket_launch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.nav_fragments.UserProfileFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditProfileFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, facilityEditText;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String androidID;
    private Uri imageUri;
    private Roles roles;

    private static final String TAG = "EditProfileFragment";

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            androidID = getArguments().getString("androidID");
            if (androidID != null) {
                userRef = db.collection("user_info").document(androidID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        // Initialize Text Editing fields
        nameEditText = view.findViewById(R.id.edit_user_name);
        emailEditText = view.findViewById(R.id.edit_user_email);
        phoneEditText = view.findViewById(R.id.edit_user_phone);
        facilityEditText = view.findViewById(R.id.edit_user_facility);
        profileImageView = view.findViewById(R.id.profile_image_view);

        Button saveButton = view.findViewById(R.id.save_profile_edit_button);
        Button cancelButton = view.findViewById(R.id.cancel_profile_edit_button);
        Button editProfilePictureButton = view.findViewById(R.id.edit_profile_picture_button);
        Button editRolesButton = view.findViewById(R.id.edit_user_role_button);

        // Load existing user details and profile picture into fields
        loadUserDetails();

        saveButton.setOnClickListener(v -> updateUserDetails());
        cancelButton.setOnClickListener(v -> closeFragment());
        editProfilePictureButton.setOnClickListener(v -> openGallery());
        editRolesButton.setOnClickListener(v -> {

            new SelectRolesFragment(roles, userRef).show(getParentFragmentManager(), "Create New User");
        });

        return view;
    }

    // Open the gallery to select a profile picture
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        imageUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            String savedPath = saveImageLocally(bitmap); // Save the selected image locally
                            saveImagePathToFirestore(savedPath); // Save the local path to Firestore
                            loadProfileImage(savedPath); // Display the saved image
                        } catch (IOException e) {
                            Log.e(TAG, "Error saving image locally", e);
                        }
                    }
                }
            }
    );

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // Save the image locally and return the file path
    private String saveImageLocally(Bitmap bitmap) throws IOException {
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, "profile_" + androidID + ".jpg");

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }

        return imageFile.getAbsolutePath();
    }

    // Save the local file path in Firestore
    private void saveImagePathToFirestore(String imagePath) {
        if (userRef != null) {
            userRef.update("profilePhotoPath", imagePath)
                    .addOnSuccessListener(aVoid -> Snackbar.make(requireView(), "Profile photo updated locally", Snackbar.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save image path to Firestore", e));
        }
    }

    // Load the profile picture from a local file path using Glide
    private void loadProfileImage(String imagePath) {
        Glide.with(this).load(new File(imagePath)).into(profileImageView);
    }

    // Load user data from Firestore and set it in the EditText fields
    private void loadUserDetails() {
        if (userRef != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    nameEditText.setText(documentSnapshot.getString("userName"));
                    emailEditText.setText(documentSnapshot.getString("userEmail"));
                    phoneEditText.setText(documentSnapshot.getString("userPhoneNumber"));
                    facilityEditText.setText(documentSnapshot.getString("userFacility"));

                    // load user roles
                    roles = documentSnapshot.get("roles", Roles.class);

                    // Load profile picture from local path if it exists
                    String profilePhotoPath = documentSnapshot.getString("profilePhotoPath");
                    if (profilePhotoPath != null) {
                        loadProfileImage(profilePhotoPath);
                    }
                } else {
                    Log.e(TAG, "User document does not exist.");
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error loading user data", e));
        } else {
            Log.e(TAG, "userRef is null.");
        }
    }

    // Update user data in Firestore
    private void updateUserDetails() {
        if (userRef != null) {
            String updatedName = nameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();
            String updatedPhone = phoneEditText.getText().toString();
            String updatedFacility = facilityEditText.getText().toString();

            userRef.update("userName", updatedName,
                            "userEmail", updatedEmail,
                            "userPhoneNumber", updatedPhone,
                            "userFacility", updatedFacility)
                    .addOnSuccessListener(aVoid -> {
                        Snackbar.make(requireView(), "Profile updated successfully", Snackbar.LENGTH_SHORT).show();
                        closeFragment();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating user data", e));
        } else {
            Log.e(TAG, "userRef is null, cannot update.");
        }
    }

    // Close the fragment and return to the main profile view
    private void closeFragment() {
        UserProfileFragment userFragment= new UserProfileFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.edit_profile_fragment_container, userFragment)
                .commit();
    }
}
