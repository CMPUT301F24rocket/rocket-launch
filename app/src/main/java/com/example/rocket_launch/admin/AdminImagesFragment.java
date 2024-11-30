package com.example.rocket_launch.admin;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying user profile images in the admin panel.
 * Handles loading user data with profile photos and displaying it in a RecyclerView.
 * Author: Pouyan
 */
public class AdminImagesFragment extends Fragment {
    private RecyclerView imagesRecyclerView;
    private AdminImagesAdapter adapter;
    private UsersDB usersDB;

    private static final int STORAGE_PERMISSION_CODE = 101; // Permission request code for storage access

    /**
     * Sets up the fragment's UI and initializes required components.
     *
     * @param inflater  LayoutInflater to inflate the layout.
     * @param container The parent container for this fragment.
     * @param savedInstanceState Saved instance state, if available.
     * @return The view for this fragment.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_images_fragment, container, false);

        // Request storage permission if not already granted
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        // Set up RecyclerView with a linear layout and dividers
        imagesRecyclerView = view.findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(imagesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        imagesRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set up adapter for RecyclerView
        adapter = new AdminImagesAdapter(new ArrayList<>(), getContext());
        imagesRecyclerView.setAdapter(adapter);

        // Initialize Firestore database reference
        usersDB = new UsersDB();

        // Load profile images
        loadImages();

        return view;
    }

    /**
     * Loads user profile images from Firestore and updates the RecyclerView.
     * Only users with valid profile photo URLs are included.
     * Author: Pouyan
     */
    private void loadImages() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            List<User> usersWithImages = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                User user = doc.toObject(User.class);
                // Include only users with valid profile photo paths
                if (user != null && user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                    usersWithImages.add(user);
                }
            }
            // Update adapter with the loaded data
            adapter.updateData(usersWithImages);
        }).addOnFailureListener(e -> {
            // Log errors if loading fails
            Log.e("Firestore", "Failed to load users: " + e.getMessage());
        });
    }

    /**
     * Reloads profile images when the fragment is resumed.
     * This ensures the RecyclerView stays updated.
     * Author: Pouyan
     */
    @Override
    public void onResume() {
        super.onResume();
        loadImages();
    }
}
