package com.example.rocket_launch.nav_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.example.rocket_launch.EditProfileFragment;
import com.example.rocket_launch.R;
import com.example.rocket_launch.Roles;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * fragment for displaying all user profile information
 */
public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";
    // user data
    private UsersDB usersDB;
    private String androidId;
    private User user;

    // user interface items
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView facilityTextView;
    private TextView facilityAddressTextView;
    private LinearLayout facilityLayout;
    private LinearLayout facilityAddressLayout;
    private ConstraintLayout profileBodyView;
    private ImageView profileImageView;
    private ImageView profilePictureView;
    // Feedback components
    private LinearLayout feedbackSection;
    private EditText feedbackText;
    private int selectedRating = 0;
    private Button submitFeedbackButton;
    private Button editFeedbackButton;
    private boolean isEditingFeedback = false; // Controls edited

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        profileBodyView = view.findViewById(R.id.user_profile_body);
        nameTextView = view.findViewById(R.id.user_name_textview);
        emailTextView = view.findViewById(R.id.user_email_textview);
        phoneTextView = view.findViewById(R.id.user_phone_textview);
        facilityTextView = view.findViewById(R.id.user_facility_textview);
        facilityAddressTextView = view.findViewById(R.id.user_facility_address_textview);
        profilePictureView = view.findViewById(R.id.profile_picture_display);
        facilityLayout = view.findViewById(R.id.display_profile_facility);
        facilityAddressLayout = view.findViewById(R.id.display_profile_facility_address);

        // Feedback UI components
        feedbackSection = view.findViewById(R.id.feedback_section);
        feedbackText = view.findViewById(R.id.feedback_text);
        submitFeedbackButton = view.findViewById(R.id.submit_feedback_button);
        editFeedbackButton = view.findViewById(R.id.edit_feedback_button);

        // Set up feedback star ratings
        setupStarRating(view);

        // Submit feedback listener
        submitFeedbackButton.setOnClickListener(v -> submitFeedback());

        // Edit feedback listener
        editFeedbackButton.setOnClickListener(v -> enableFeedbackEditing());

        // Set up "Edit Profile" button listener
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(v -> {
            // Hide the profile body and transition to EditProfileFragment
            profileBodyView.setVisibility(View.GONE);
            feedbackSection.setVisibility(View.GONE); // Hide feedback section during edit profile
            openEditProfileFragment();
        });

        fetchUserProfile();
        loadFeedbackFromFirestore();

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        usersDB = new UsersDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * opens a fragment used for editing contents of a user's profile
     */
    private void openEditProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("androidID", androidId);

        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.edit_profile_fragment_container, editProfileFragment)
                .commit();
    }

    /**
     * function that gets and displays all contents of a user's profile
     */
    private void fetchUserProfile() {
        usersDB.getUser(androidId, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                user = newUser;
                if (user.getRoles() != null && user.getRoles().isOrganizer()) {
                    facilityLayout.setVisibility(View.VISIBLE);
                    facilityAddressLayout.setVisibility(View.VISIBLE);
                    facilityTextView.setText(user.getUserFacility());
                    facilityAddressTextView.setText(user.getUserFacilityAddress());
                }

                nameTextView.setText(user.getUserName());
                emailTextView.setText(user.getUserEmail());
                phoneTextView.setText(user.getUserPhoneNumber());

                if (user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                    loadProfileImage(user.getProfilePhotoPath());
                } else {
                    setDefaultProfilePicture(user.getUserName());
                }
            }
        }, e -> Log.e(TAG, "No matching document found or task failed", e));
    }

    /**
     * function that loads a user's profile photo
     * @param imagePath
     *  path to profile photo in database
     */
    private void loadProfileImage(String imagePath) {
        Glide.with(this)
                .load(imagePath)
                .into(new com.bumptech.glide.request.target.CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        profilePictureView.setImageDrawable(resource); // Set the loaded image to the ImageView
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        // If the image fails to load, generate a default profile picture
                        setDefaultProfilePicture(user.getUserName());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle if the image resource needs to be cleared (optional)
                        setDefaultProfilePicture(user.getUserName());
                    }
                });
    }
    private void setupStarRating(View view) {
        for (int i = 1; i <= 5; i++) {
            int starId = getResources().getIdentifier("star" + i, "id", requireContext().getPackageName());
            ImageView star = view.findViewById(starId);
            final int starRating = i;

            // Add click listener for stars
            star.setOnClickListener(v -> {
                if (isEditingFeedback) { // Allow clicking stars only if editing is enabled
                    updateStarRating(starRating, view);
                } else {
                    Toast.makeText(requireContext(), "Click 'Edit Feedback' to modify your rating.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateStarRating(int rating, View view) {
        selectedRating = rating;
        for (int i = 1; i <= 5; i++) {
            int starId = getResources().getIdentifier("star" + i, "id", requireContext().getPackageName());
            ImageView star = view.findViewById(starId);
            star.setImageResource(i <= rating ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        }
    }

    private void submitFeedback() {
        String feedback = feedbackText.getText().toString().trim();
        if (selectedRating == 0) {
            Toast.makeText(requireContext(), "Please select a star rating.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (feedback.isEmpty()) {
            Toast.makeText(requireContext(), "Please provide feedback.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save feedback to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Feedback feedbackData = new Feedback(selectedRating, feedback);

        db.collection("feedback").document(androidId)
                .set(feedbackData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    feedbackText.setEnabled(false); // Disable feedback input
                    submitFeedbackButton.setVisibility(View.GONE); // Hide submit button
                    editFeedbackButton.setVisibility(View.VISIBLE); // Show edit button
                    isEditingFeedback = false; // Disable editing mode

                })
                .addOnFailureListener(e -> Log.e(TAG, "Error submitting feedback", e));
    }

    private void enableFeedbackEditing() {
        isEditingFeedback = true; // Enable editing mode
        feedbackText.setEnabled(true); // Enable feedback text input
        feedbackText.requestFocus();   // Focus on feedback input
        submitFeedbackButton.setVisibility(View.VISIBLE); // Show submit button
        editFeedbackButton.setVisibility(View.GONE); // Hide edit button
    }
    private void loadFeedbackFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("feedback").document(androidId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Feedback feedback = documentSnapshot.toObject(Feedback.class);
                        if (feedback != null) {
                            selectedRating = feedback.getRating();
                            updateStarRating(selectedRating, getView());
                            feedbackText.setText(feedback.getFeedbackText());
                            feedbackText.setEnabled(false); // Disable input initially
                            submitFeedbackButton.setVisibility(View.GONE); // Hide submit button
                            editFeedbackButton.setVisibility(View.VISIBLE); // Show edit button
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading feedback", e));
    }

    public static class Feedback {
        private int rating;
        private String feedbackText;

        // Required no-argument constructor
        public Feedback() {}

        public Feedback(int rating, String feedbackText) {
            this.rating = rating;
            this.feedbackText = feedbackText;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getFeedbackText() {
            return feedbackText;
        }

        public void setFeedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
        }
    }


    private void setDefaultProfilePicture(String userName) {
        // Default background color and text settings
        int width = 200;  // Width of the Bitmap
        int height = 200; // Height of the Bitmap
        int textColor = Color.BLACK;
        float textSize = 80f;

        // Load the background image (new_image) from resources
        Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.new_image);
        Bitmap scaledBackground = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);

        // Create a new Bitmap and Canvas to draw
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the background image
        canvas.drawBitmap(scaledBackground, 0, 0, null);

        // Set up Paint for the text
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        // Use a premium built-in font (example: sans-serif-medium)
        paint.setTypeface(Typeface.create("serif", Typeface.NORMAL));

        // Draw the first letter of the user's name
        if (userName != null && !userName.isEmpty()) {
            String firstLetter = userName.substring(0, 1).toUpperCase();
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float x = width / 2f;
            float y = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f);
            canvas.drawText(firstLetter, x, y, paint);
        }

        // Set the final Bitmap to the ImageView
        profilePictureView.setImageBitmap(bitmap);
    }


    /**
     * function used to update user interface if change occurred
     */
    private void updateUI() {
        // Set the user details
        nameTextView.setText(user.getUserName());
        emailTextView.setText(user.getUserEmail());
        phoneTextView.setText(user.getUserPhoneNumber());

        // Display facility if user is an organizer
        if (user.getRoles() != null && user.getRoles().isOrganizer()) {
            facilityLayout.setVisibility(View.VISIBLE);
            facilityTextView.setText(user.getUserFacility());

            facilityAddressLayout.setVisibility(View.VISIBLE);
            facilityAddressTextView.setText(user.getUserFacilityAddress());
        } else {
            facilityLayout.setVisibility(View.GONE);
            facilityAddressLayout.setVisibility(View.GONE);
        }

        // Load profile picture using Glide
        if (user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePhoto())
                    .placeholder(R.drawable.default_image) // Optional placeholder image
                    .error(R.drawable.default_image) // Optional error image
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.default_image); // Set default image if no path is found
        }
    }


}
