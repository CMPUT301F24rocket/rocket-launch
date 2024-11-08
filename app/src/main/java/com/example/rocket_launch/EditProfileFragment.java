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
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private LinearLayout facilityLayout;
    private ImageView profileImageView;
    private Button changeProfilePictureButton, deleteProfilePictureButton;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String androidID;
    private Uri imageUri;
    private Roles roles;

    private static final String TAG = "EditProfileFragment";


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

        // Initialize UI components
        nameEditText = view.findViewById(R.id.edit_user_name);
        emailEditText = view.findViewById(R.id.edit_user_email);
        phoneEditText = view.findViewById(R.id.edit_user_phone);
        facilityEditText = view.findViewById(R.id.edit_user_facility);
        profileImageView = view.findViewById(R.id.profile_image_view);
        changeProfilePictureButton = view.findViewById(R.id.change_profile_picture_button);
        deleteProfilePictureButton = view.findViewById(R.id.delete_profile_picture_button);
        facilityLayout = view.findViewById(R.id.display_edit_profile_facility);

        Button saveButton = view.findViewById(R.id.save_profile_edit_button);
        Button cancelButton = view.findViewById(R.id.cancel_profile_edit_button);
        Button editRolesButton = view.findViewById(R.id.edit_user_role_button);
        Button generateProfileButton = view.findViewById(R.id.generate_profile_picture_button);
        generateProfileButton.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Implementation in progress...", Toast.LENGTH_SHORT).show()
        );

        // Load existing user details
        loadUserDetails();

        // Set up button listeners
        saveButton.setOnClickListener(v -> updateUserDetails());
        cancelButton.setOnClickListener(v -> closeFragment());
        changeProfilePictureButton.setOnClickListener(v -> openGallery());
        deleteProfilePictureButton.setOnClickListener(v -> deleteProfilePhoto());
        editRolesButton.setOnClickListener(v -> openRolesFragment());

        return view;
    }

    // Open the gallery to select a new profile picture
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // Handle the result from the gallery
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        imageUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            String savedPath = saveImageLocally(bitmap);
                            saveImagePathToFirestore(savedPath);
                            loadProfileImage(savedPath);
                        } catch (IOException e) {
                            Log.e(TAG, "Error saving image", e);
                        }
                    }
                }
            }
    );

    // Save the image locally and return the file path
    private String saveImageLocally(Bitmap bitmap) throws IOException {
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Use a unique filename based on the current timestamp
        String fileName = "profile_" + androidID + "_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(storageDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        return imageFile.getAbsolutePath();
    }


    // Save the image path to Firestore
    private void saveImagePathToFirestore(String imagePath) {
        if (userRef != null) {
            userRef.update("profilePhotoPath", imagePath)
                    .addOnSuccessListener(aVoid -> Snackbar.make(requireView(), "Profile photo updated", Snackbar.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save image path", e));
        }
    }

    private void handleImageSelection(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
            String savedPath = saveImageLocally(bitmap); // Save the new image
            updateProfilePhotoPath(savedPath); // Update Firestore with the new path
            loadProfileImage(savedPath); // Load the new image into the ImageView
        } catch (IOException e) {
            Log.e(TAG, "Error handling image selection", e);
        }
    }

    // Load the profile picture using Glide
    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this)
                    .load(new File(imagePath))
                    .skipMemoryCache(true) // Bypass Glide's memory cache
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE) // Bypass disk cache
                    .into(profileImageView);
        } else {
            // Set default image if no path is available
            profileImageView.setImageResource(R.drawable.default_image);
        }
    }


    private void updateProfilePhotoPath(String imagePath) {
        if (userRef != null) {
            userRef.update("profilePhotoPath", imagePath)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Profile photo path updated in Firestore");
                        Snackbar.make(requireView(), "Profile photo updated", Snackbar.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update profile photo path in Firestore", e));
        }
    }

    private void deleteProfilePhoto() {
        if (userRef != null) {
            // Remove the profile photo path from Firestore
            userRef.update("profilePhotoPath", null)
                    .addOnSuccessListener(aVoid -> {
                        profileImageView.setImageResource(R.drawable.default_image);
                        Snackbar.make(requireView(), "Profile photo removed from cloud", Snackbar.LENGTH_SHORT).show();
                        Log.d(TAG, "Profile photo path set to null in Firestore");
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update Firestore", e));
        } else {
            Log.e(TAG, "User reference is null, cannot delete photo");
        }
    }




    // Load user details from Firestore
    private void loadUserDetails() {
        if (userRef != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    nameEditText.setText(documentSnapshot.getString("userName"));
                    emailEditText.setText(documentSnapshot.getString("userEmail"));
                    phoneEditText.setText(documentSnapshot.getString("userPhoneNumber"));

                    roles = documentSnapshot.get("roles", Roles.class);
                    if (roles != null && roles.isOrganizer()) {
                        facilityLayout.setVisibility(View.VISIBLE);
                        facilityEditText.setText(documentSnapshot.getString("userFacility"));
                    } else {
                        facilityLayout.setVisibility(View.GONE);
                    }

                    String profilePhotoPath = documentSnapshot.getString("profilePhotoPath");
                    if (profilePhotoPath != null) {
                        loadProfileImage(profilePhotoPath);
                    }
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error loading user data", e));
        }
    }

    // Update user details in Firestore
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
        }
    }

    // Close the fragment
    private void closeFragment() {
        UserProfileFragment userFragment = new UserProfileFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.edit_profile_fragment_container, userFragment)
                .commit();
    }

    // Open the Roles Fragment
    private void openRolesFragment() {
        SelectRolesFragment frag = new SelectRolesFragment(roles, userRef);
        frag.setOnSuccessListener(() -> loadUserDetails());
        frag.show(getParentFragmentManager(), "Edit Roles");
    }
}
