package com.example.rocket_launch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rocket_launch.nav_fragments.UserProfileFragment;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment that contains the feedback form
 * Authors: Nathan and Chetan
 */
public class FeedbackFormFragment extends Fragment {

    private static final String TAG = "FeedbackFormFragment";

    // Feedback components
    private EditText feedbackText;
    private int selectedRating = 0;
    private Button submitFeedbackButton;
    private Button editFeedbackButton;
    private boolean isEditingFeedback = false;
    private ImageButton backButton;

    private String androidId;

    /**
     * Default constructor for FeedbackFormFragment
     * @param androidId User's androidID
     * Author: Nathan
     */
    public FeedbackFormFragment(String androidId) {
        this.androidId = androidId;
    }

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_form, container, false);

        feedbackText = view.findViewById(R.id.feedback_text);
        submitFeedbackButton = view.findViewById(R.id.submit_feedback_button);
        editFeedbackButton = view.findViewById(R.id.edit_feedback_button);
        backButton = view.findViewById(R.id.back_button);

        // Set up feedback star ratings
        setupStarRating(view);

        // Submit feedback listener
        submitFeedbackButton.setOnClickListener(v -> submitFeedback());

        // Edit feedback listener
        editFeedbackButton.setOnClickListener(v -> enableFeedbackEditing());

        loadFeedbackFromFirestore();

        //back button pressed
        backButton.setOnClickListener(v -> {
            UserProfileFragment userFragment = new UserProfileFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_frame, userFragment)
                    .commit();
        });

        return view;
    }

    /**
     * Setup the stars used for feedback form
     * @param view View that setups star rating
     * Author: Chetan
     */
    private void setupStarRating(View view) {
        for (int i = 1; i <= 5; i++) {
            int starId = getResources().getIdentifier("star" + i, "id", requireContext().getPackageName());
            ImageView star = view.findViewById(starId);
            final int starRating = i;

            star.setOnClickListener(v -> {
                if (isEditingFeedback) {
                    updateStarRating(starRating, view);
                } else {
                    Toast.makeText(requireContext(), "Click 'Edit Feedback' to modify your rating.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Updates the stars used for feedback form
     * @param rating Integer between 1 and 5
     * @param view View that setups star rating
     * Author: Chetan
     */
    private void updateStarRating(int rating, View view) {
        selectedRating = rating;
        for (int i = 1; i <= 5; i++) {
            int starId = getResources().getIdentifier("star" + i, "id", requireContext().getPackageName());
            ImageView star = view.findViewById(starId);
            star.setImageResource(i <= rating ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        }
    }


    /**
     * Submits the feedback form into firestore
     * Author: Chetan
     */
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Feedback feedbackData = new Feedback(selectedRating, feedback);

        db.collection("feedback").document(androidId)
                .set(feedbackData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    feedbackText.setEnabled(false);
                    submitFeedbackButton.setVisibility(View.GONE);
                    editFeedbackButton.setVisibility(View.VISIBLE);
                    isEditingFeedback = false;
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error submitting feedback", e));
    }

    /**
     * Enables feedback text input
     * Author: Chetan
     */
    private void enableFeedbackEditing() {
        isEditingFeedback = true;
        feedbackText.setEnabled(true);
        feedbackText.requestFocus();
        submitFeedbackButton.setVisibility(View.VISIBLE);
        editFeedbackButton.setVisibility(View.GONE);
    }

    /**
     * Loads feedback from firestore
     * Author: Chetan
     */
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
                            feedbackText.setEnabled(false);
                            submitFeedbackButton.setVisibility(View.GONE);
                            editFeedbackButton.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading feedback", e));
    }

    /**
     * Class that represents feedback object
     */
    public static class Feedback {
        private int rating;
        private String feedbackText;

        public Feedback() {}

        public Feedback(int rating, String feedbackText) {
            this.rating = rating;
            this.feedbackText = feedbackText;
        }

        public int getRating() {
            return rating;
        }

        public String getFeedbackText() {
            return feedbackText;
        }
    }
}
