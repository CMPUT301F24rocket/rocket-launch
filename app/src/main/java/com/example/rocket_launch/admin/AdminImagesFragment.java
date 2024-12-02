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

public class AdminImagesFragment extends Fragment {
    private RecyclerView imagesRecyclerView;
    private AdminImagesAdapter adapter;
    private UsersDB usersDB;
    private List<User> usersWithImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_images_fragment, container, false);

        // Initialize RecyclerView
        imagesRecyclerView = view.findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(imagesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        imagesRecyclerView.addItemDecoration(divider);

        // Initialize adapter and database reference
        usersWithImages = new ArrayList<>();
        adapter = new AdminImagesAdapter(usersWithImages, getContext());
        imagesRecyclerView.setAdapter(adapter);

        usersDB = new UsersDB();
        listenForImageChanges();

        // Set up the delete listener
        adapter.setOnImageDeleteListener(user -> showDeleteConfirmationDialog(user));

        return view;
    }

    /**
     * Listens for changes in Firestore and updates the RecyclerView in real-time.
     */
    private void listenForImageChanges() {
        usersDB.getUsersRef().addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {
                usersWithImages.clear(); // Clear existing data

                // Add users with profile photos
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null && user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                        usersWithImages.add(user);
                    }
                }

                // Notify adapter about data changes
                adapter.updateData(usersWithImages);
            }
        });
    }

    /**
     * Shows a confirmation dialog for deleting a user's image.
     *
     * @param user The user whose image is to be deleted.
     */
    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Image?")
                .setMessage("This action cannot be undone. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> deleteImage(user))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes the user's profile photo path from Firestore.
     *
     * @param user The user whose image is to be deleted.
     */
    private void deleteImage(User user) {
        usersDB.getUsersRef().document(user.getAndroidId()).update("profilePhotoPath", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Image deleted successfully for user: " + user.getAndroidId());

                    // Display a success toast
                    Toast.makeText(requireContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show();

                    // Real-time updates in Firestore will automatically refresh the list
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to delete image for user: " + user.getAndroidId(), e);

                    // Display a failure toast
                    Toast.makeText(requireContext(), "Failed to delete image. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}

