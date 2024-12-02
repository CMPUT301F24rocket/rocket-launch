package com.example.rocket_launch.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for managing and displaying user profile images in the admin section.
 * Allows viewing and deleting profile images with real-time Firestore updates.
 * Author: Pouyan
 */
public class AdminImagesFragment extends Fragment {
    private RecyclerView imagesRecyclerView; // RecyclerView for displaying user images
    private AdminImagesAdapter adapter; // Adapter for RecyclerView
    private UsersDB usersDB; // Database reference for users
    private List<User> usersWithImages; // List of users with profile images

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater for inflating the layout.
     * @param container          The parent container.
     * @param savedInstanceState Saved state data for the fragment.
     * @return The View for the fragment's UI.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.admin_images_fragment, container, false);

        // Initialize RecyclerView
        imagesRecyclerView = view.findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add dividers between items in the RecyclerView
        DividerItemDecoration divider = new DividerItemDecoration(imagesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        imagesRecyclerView.addItemDecoration(divider);

        // Initialize adapter and database reference
        usersWithImages = new ArrayList<>();
        adapter = new AdminImagesAdapter(usersWithImages, getContext());
        imagesRecyclerView.setAdapter(adapter);

        usersDB = new UsersDB(); // Set up the Firestore database reference

        // Listen for changes to the user images in Firestore
        listenForImageChanges();

        // Set up the delete listener for images
        adapter.setOnImageDeleteListener(user -> showDeleteConfirmationDialog(user));

        return view;
    }

    /**
     * Listens for changes in Firestore and updates the RecyclerView in real-time.
     * Author: Pouyan
     */
    private void listenForImageChanges() {
        usersDB.getUsersRef().addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {
                usersWithImages.clear(); // Clear the existing list

                // Populate the list with users who have profile images
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null && user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                        usersWithImages.add(user);
                    }
                }

                // Update the adapter with the new data
                adapter.updateData(usersWithImages);
            }
        });
    }

    /**
     * Shows a confirmation dialog before deleting a user's profile image.
     *
     * @param user The user whose image is to be deleted.
     * Author: Pouyan
     */
    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Image?")
                .setMessage("This action cannot be undone. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> deleteImage(user)) // Delete if confirmed
                .setNegativeButton("No", null) // Dismiss dialog if declined
                .show();
    }

    /**
     * Deletes the user's profile photo path from Firestore.
     *
     * @param user The user whose profile image is being deleted.
     * Author: Pouyan
     */
    private void deleteImage(User user) {
        usersDB.getUsersRef().document(user.getAndroidId()).update("profilePhotoPath", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Image deleted successfully for user: " + user.getAndroidId());

                    // Display a success message
                    Toast.makeText(requireContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show();

                    // Real-time updates in Firestore will automatically refresh the list
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to delete image for user: " + user.getAndroidId(), e);

                    // Display an error message
                    Toast.makeText(requireContext(), "Failed to delete image. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
