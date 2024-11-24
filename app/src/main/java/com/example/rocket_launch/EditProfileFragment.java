package com.example.rocket_launch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * fragment for organizer edit profile
 */
public class EditProfileFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, facilityEditText, facilityAddressEditText;
    private LinearLayout facilityLayout;
    private ImageView profileImageView;
    private Button changeProfilePictureButton, deleteProfilePictureButton;
    private UsersDB usersDB;
    private String androidID;
    private Uri imageUri;
    private User user;

    private static final String TAG = "EditProfileFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup db instance
        usersDB = new UsersDB();
        if (getArguments() != null) {
            androidID = getArguments().getString("androidID");
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
        facilityAddressEditText = view.findViewById(R.id.edit_user_facility_address);
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

        // Load and display existing user details
        loadUserDetails();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserDetails());
        cancelButton.setOnClickListener(v -> closeFragment());
        changeProfilePictureButton.setOnClickListener(v -> openGallery());
        deleteProfilePictureButton.setOnClickListener(v -> deleteProfilePhoto());
        editRolesButton.setOnClickListener(v -> openRolesFragment());

        return view;
    }

    /**
     * Open the gallery to select a new profile picture
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    /**
     * Handle the result from the gallery
     */
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                requireActivity();
                if (result.getResultCode() == Activity.RESULT_OK) {
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

    /**
     * Save the image locally and return the file path
     * @param bitmap
     *  bitmap of image to save
     * @return String of image path
     * @throws IOException
     *  if there was an error saving
     */
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


    /**
     * Save the image path to Firestore
     * @param imagePath
     *  path to image in database
     */
    private void saveImagePathToFirestore(String imagePath) {
        user.setProfilePhotoPath(imagePath);
        usersDB.updateUser(androidID, user, s -> {
            Log.d(TAG, "Profile photo path updated in Firestore");
            Snackbar.make(requireView(), "Profile photo updated", Snackbar.LENGTH_SHORT).show();
        }, e -> Log.e(TAG, "Failed to save profile photo path in Firestore", e));
    }

    /**
     * updates path to profile photo
     * @param imagePath
     *  path to profile photo to update
     */
    private void updateProfilePhotoPath(String imagePath) {
        user.setProfilePhotoPath(imagePath);
        usersDB.updateUser(androidID, user, s -> {
            Log.d(TAG, "Profile photo path updated in Firestore");
            Snackbar.make(requireView(), "Profile photo updated", Snackbar.LENGTH_SHORT).show();
        }, e -> Log.e(TAG, "Failed to update profile photo path in Firestore", e));
    }

    /**
     * deletes profile photo from database
     */
    private void deleteProfilePhoto() {
        user.setProfilePhotoPath("");
        usersDB.updateUser(androidID, user, s -> {
            profileImageView.setImageResource(R.drawable.default_image);
            Snackbar.make(requireView(), "Profile photo removed from cloud", Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "Profile photo path set to null in Firestore");
        }, e -> Log.e(TAG, "Failed to update Firestore", e));
    }


    /**
     * handles image selection
     * @param uri
     *  resource identifier for image
     */
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

    /**
     * Load the profile picture using Glide
     * @param imagePath
     *  path to an image
     */
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


    /**
     * Load user details from Firestore
     */
    private void loadUserDetails() {
        usersDB.getUser(androidID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User userData) {
                user = userData;
                refreshFragmentData();
            }
        }, e -> Log.w("Firebase", "Error loading user", e));
    }

    /**
     * updates user interface to show new data
     */
    void refreshFragmentData() {
        nameEditText.setText(user.getUserName());
        emailEditText.setText(user.getUserEmail());
        phoneEditText.setText(user.getUserPhoneNumber());
        if (user.getRoles().isOrganizer()) {
            facilityLayout.setVisibility(View.VISIBLE);
            facilityEditText.setText(user.getUserFacility());

            facilityAddressEditText.setText(user.getUserFacilityAddress());
        }
        else {
            facilityLayout.setVisibility(View.GONE);
        }
        if (user.getProfilePhotoPath() != null) {
            loadProfileImage(user.getProfilePhotoPath());
        }
    }

    /**
     * Update user details in Firestore
     */
    private void saveUserDetails() {
        user.setUserName(nameEditText.getText().toString());
        user.setUserEmail(emailEditText.getText().toString());
        user.setUserPhoneNumber(phoneEditText.getText().toString());
        user.setUserFacility(facilityEditText.getText().toString());
        user.setUserFacilityAddress(facilityAddressEditText.getText().toString());
        usersDB.updateUser(androidID, user,
                success -> {Log.d(TAG, "user details updated"); closeFragment();},
                error -> Log.e(TAG, "failed to update user details", error));
    }

    /**
     * Close the fragment
     */
    private void closeFragment() {
        UserProfileFragment userFragment = new UserProfileFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.edit_profile_fragment_container, userFragment)
                .commit();
    }

    /**
     * Open the Roles Fragment
     */
    private void openRolesFragment() {
        SelectRolesFragment frag = new SelectRolesFragment(user.getRoles());
        frag.setOnSuccessListener(new SelectRolesFragment.onSuccessListener() {
            @Override
            public void onSuccess(Roles roles) {
                user.setRoles(roles);
                refreshFragmentData();
            }
        });
        frag.show(getParentFragmentManager(), "Edit Roles");
    }
}
