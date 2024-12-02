    package com.example.rocket_launch;
    import android.app.Activity;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Typeface;
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
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.fragment.app.Fragment;
    import com.example.rocket_launch.nav_fragments.UserProfileFragment;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.material.snackbar.Snackbar;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.squareup.picasso.Picasso;
    import android.graphics.Canvas;

    /**
     * fragment for organizer edit profile
     * Author: Chetan
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
            generateProfileButton.setOnClickListener(v -> loadFixedProfilePicture());


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
         * Generates and sets a default profile name for the user
         * @param userName Gets the name of the user
         */
        private void setDefaultProfilePicture(String userName) {
            int width = 200;
            int height = 200;
            int textColor = Color.BLACK;
            float textSize = 80f;

            Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.new_image);
            Bitmap scaledBackground = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawBitmap(scaledBackground, 0, 0, null);

            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create("serif", Typeface.NORMAL));

            if (userName != null && !userName.isEmpty()) {
                String firstLetter = userName.substring(0, 1).toUpperCase();
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                float x = width / 2f;
                float y = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f);
                canvas.drawText(firstLetter, x, y, paint);
            }

            profileImageView.setImageBitmap(bitmap);

            try {
                String localPath = saveImageLocally(bitmap);
                uploadAutoGeneratedImage(localPath);
            } catch (IOException e) {
                Log.e(TAG, "Failed to save auto-generated image", e);
            }
        }

        /**
         * Uploads the auto generated default profile picture to firebase
         * @param localPath Gets the path of the image from firebase
         */
        private void uploadAutoGeneratedImage(String localPath) {
            File file = new File(localPath);
            Uri imageUri = Uri.fromFile(file);

            String fileName = "ag_images/" + System.currentTimeMillis() + ".jpg";
            ImageStorageDB.uploadImage(imageUri, fileName,
                    downloadUrl -> {
                        Log.d(TAG, "Auto-generated image uploaded successfully: " + downloadUrl);
                        saveProfilePictureUrlToFirestore(downloadUrl);
                    },
                    error -> Log.e(TAG, "Failed to upload auto-generated image", error)
            );
        }


        /**
         *  Loads pictures from firebase storage using the fixed urls
         */
        private void loadFixedProfilePicture() {
            String[] fixedImageUrls = {
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage1.png?alt=media&token=ee6fc585-b355-4d48-8b67-154f82565637",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage2.png?alt=media&token=9266bc82-ac24-4edf-96ec-4478bfba1a11",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage3.png?alt=media&token=25d2b9cc-6a8e-4628-aa86-82e8e956d95f",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage4.png?alt=media&token=d1103424-9484-496f-9bb1-f911a7e7d080",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage5.png?alt=media&token=d5f35574-b5df-44e7-b599-8e5fa4c0804c",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage6.png?alt=media&token=776215f7-3c13-4cca-9d77-2661730830ad",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage10.png?alt=media&token=92e10f80-77b8-4e26-8a5f-7e60f008602d",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage7.png?alt=media&token=dee7f8b0-9302-41cc-a401-2d324fb7f19f",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage8.png?alt=media&token=882e7cfe-a523-4a5b-94c5-71228070dcee",
                    "https://firebasestorage.googleapis.com/v0/b/rocket-launch-21699.firebasestorage.app/o/profile_pictures%2Fimage9.png?alt=media&token=8d9e548a-a3df-4c80-ba0c-25615343c04b"
            };

            int randomIndex = (int) (Math.random() * fixedImageUrls.length);
            String randomUrl = fixedImageUrls[randomIndex];
            Picasso.get()
                    .load(randomUrl)
                    .into(profileImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Loaded random image URL: " + randomUrl);
                            saveProfilePictureUrlToFirestore(randomUrl);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Failed to load image. Generating default profile picture.", e);
                            setDefaultProfilePicture(user.getUserName());
                        }
                    });
        }


        /**
         * Save the selected profile picture URL to Firestore
         *
         * @param imageUrl The URL of the selected image
         */
        private void saveProfilePictureUrlToFirestore(String imageUrl) {
            user.setProfilePhotoPath(imageUrl);
            usersDB.updateUser(androidID, user, success -> {
                Log.d(TAG, "Profile picture URL updated in Firestore.");
                Snackbar.make(requireView(), "Profile photo updated", Snackbar.LENGTH_SHORT).show();
            }, error -> Log.e(TAG, "Failed to update profile picture URL in Firestore.", error));
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
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            // Generate a unique path for the image
                            String imagePath = "images/profile_" + androidID + ".jpg";

                            // Use ImageStorageDB to upload the image
                            ImageStorageDB.uploadImage(imageUri, imagePath, downloadUrl -> {
                                // Save the download URL to Firestore
                                saveProfilePictureUrlToFirestore(downloadUrl);
                                // Load the image into the UI
                                loadProfileImage(downloadUrl);
                            }, e -> {
                                Log.e(TAG, "Failed to upload image using ImageStorageDB", e);
                                Snackbar.make(requireView(), "Failed to upload image", Snackbar.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
        );


        /**
         * Handle the image URI and save it locally as needed
         *
         * @param uri The image URI returned from the gallery
         * @return The local file path of the saved image
         */
        private String handleImageUri(Uri uri) throws IOException {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);

            // Save the bitmap locally
            return saveImageLocally(bitmap);
        }

        /**
         * Save the image locally and return the file path
         *
         * @param bitmap bitmap of image to save
         * @return String of image path
         * @throws IOException if there was an error saving
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
         *
         * @param imagePath path to image in database
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
         *
         * @param imagePath path to profile photo to update
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
                setDefaultProfilePicture(user.getUserName());
                Snackbar.make(requireView(), "Profile photo removed from cloud", Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "Profile photo path set to null in Firestore");
            }, e -> Log.e(TAG, "Failed to update Firestore", e));
        }


        /**
         * handles image selection
         *
         * @param uri resource identifier for image
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


        private void loadProfileImage(String imageUrl) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .into(profileImageView);
            } else {
                // Set default image if no URL is available
                setDefaultProfilePicture(user.getUserName());
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
                    // Load the profile picture URL from Firestore
                    String profilePhotoPath = user.getProfilePhotoPath();
                    if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                        Picasso.get()
                                .load(profilePhotoPath)
                                .into(profileImageView);
                    } else {
                        // Generate dynamic default profile picture
                        setDefaultProfilePicture(user.getUserName());
                    }
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
            } else {
                facilityLayout.setVisibility(View.GONE);
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
                    success -> {
                        Log.d(TAG, "user details updated");
                        closeFragment();
                    },
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

        /**
         * Upload image from phone to firebase
         * @param imageUri URI of image
         */
        private void uploadImageToFirebase(Uri imageUri) {
            String fileName = "profile_images/" + System.currentTimeMillis() + ".jpg";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

            // Upload the file to Firebase Storage
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Get the download URL after uploading
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("FirebaseUpload", "Image uploaded to Firebase: " + uri.toString());
                    saveProfilePictureUrlToFirestore(uri.toString()); // Save the URL in Firestore
                    loadProfileImage(uri.toString()); // Display the uploaded image
                }).addOnFailureListener(e -> Log.e("FirebaseUpload", "Failed to get Firebase URL: " + e.getMessage()));
            }).addOnFailureListener(e -> Log.e("FirebaseUpload", "Failed to upload image: " + e.getMessage()));
        }
    }
