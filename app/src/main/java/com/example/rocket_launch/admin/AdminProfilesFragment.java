package com.example.rocket_launch.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for managing user profiles in the admin view.
 * Allows viewing and deleting profiles.
 * Author: Pouyan
 */
public class AdminProfilesFragment extends Fragment {
    private RecyclerView profilesRecyclerView;
    private AdminProfilesAdapter adapter;
    private UsersDB usersDB;

    /**
     * Inflates the fragment layout and initializes RecyclerView and Firestore.
     *
     * @param inflater  The LayoutInflater object to inflate views.
     * @param container The parent container of the fragment.
     * @param savedInstanceState Saved instance state bundle.
     * @return The root view of the fragment.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        // Set up RecyclerView for profiles
        profilesRecyclerView = view.findViewById(R.id.profiles_recycler_view);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profilesRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // Initialize adapter with an empty list
        adapter = new AdminProfilesAdapter(new ArrayList<>());
        profilesRecyclerView.setAdapter(adapter);

        // Initialize Firestore database for users and load profiles
        usersDB = new UsersDB();
        loadProfiles();

        // Set up the long-click listener for profile deletion
        adapter.setOnProfileDeleteListener(this::showDeleteConfirmation);

        return view;
    }

    /**
     * Loads profiles from Firestore and updates the RecyclerView adapter.
     * Ensures missing fields have placeholder values.
     * Author: Pouyan
     */
    private void loadProfiles() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                User user = doc.toObject(User.class);

                if (user != null) {
                    // Add placeholders for missing data
                    if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                        user.setUserName("No name provided");
                    }
                    if (user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()) {
                        user.setUserEmail("No email provided");
                    }
                    users.add(user);
                }
            }
            // Update adapter with the loaded user list
            adapter.updateData(users);
        });
    }

    /**
     * Displays a confirmation dialog for deleting a user profile.
     *
     * @param user     The user to be deleted.
     * @param position The position of the user in the RecyclerView adapter.
     * Author: Pouyan
     */
    private void showDeleteConfirmation(User user, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete profile?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile(user, position))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes a user profile from Firestore and removes it from the RecyclerView.
     *
     * @param user     The user to be deleted.
     * @param position The position of the user in the RecyclerView adapter.
     * Author: Pouyan
     */
    private void deleteProfile(User user, int position) {
        usersDB.deleteUser(user.getAndroidId(),
                unused -> {
                    // Remove the user from the adapter and show a success message
                    adapter.removeProfile(position);
                    Toast.makeText(requireContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                }
        );
    }
}
