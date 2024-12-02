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

public class AdminProfilesFragment extends Fragment {
    private RecyclerView profilesRecyclerView;
    private AdminProfilesAdapter adapter;
    private UsersDB usersDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        // Set up RecyclerView
        profilesRecyclerView = view.findViewById(R.id.profiles_recycler_view);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profilesRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // Initialize adapter and attach to RecyclerView
        adapter = new AdminProfilesAdapter(new ArrayList<>());
        profilesRecyclerView.setAdapter(adapter);

        // Initialize Firestore DB and load profiles
        usersDB = new UsersDB();
        loadProfiles();

        // Set long-click listener for profile deletion
        adapter.setOnProfileDeleteListener(this::showDeleteConfirmation);

        return view;
    }

    private void loadProfiles() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                User user = doc.toObject(User.class);

                if (user != null) {
                    // Set placeholders for missing fields
                    if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                        user.setUserName("No name provided");
                    }
                    if (user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()) {
                        user.setUserEmail("No email provided");
                    }
                    users.add(user);
                }
            }
            adapter.updateData(users); // Update adapter with loaded profiles
        });
    }

    private void showDeleteConfirmation(User user, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete profile?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile(user, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProfile(User user, int position) {
        usersDB.getUsersRef().document(user.getAndroidId()).delete()
                .addOnSuccessListener(aVoid -> {
                    adapter.removeProfile(position); // Remove from RecyclerView
                    Toast.makeText(requireContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to delete profile", Toast.LENGTH_SHORT).show();
                });
    }
}
