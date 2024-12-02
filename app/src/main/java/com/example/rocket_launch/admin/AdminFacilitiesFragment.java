package com.example.rocket_launch.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for managing facilities in the admin section. Displays a list of facilities
 * in a RecyclerView and allows admins to delete facilities from the database.
 * Author: Pouyan
 */
public class AdminFacilitiesFragment extends Fragment {
    private RecyclerView facilitiesRecyclerView; // RecyclerView for displaying facilities
    private AdminFacilitiesAdapter adapter; // Adapter for managing facility items
    private UsersDB usersDB; // Database reference for user-related data

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_facilities_fragment, container, false);

        // Set up the RecyclerView
        facilitiesRecyclerView = view.findViewById(R.id.facilities_recycler_view);
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add dividers between RecyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(facilitiesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        facilitiesRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize the adapter with an empty list and a delete listener
        adapter = new AdminFacilitiesAdapter(new ArrayList<>(), (user, position) -> {
            showDeleteConfirmationDialog(user, position);
        });
        facilitiesRecyclerView.setAdapter(adapter);

        // Initialize UsersDB
        usersDB = new UsersDB();
        loadFacilities(); // Load facilities from the database

        return view;
    }

    /**
     * Shows a confirmation dialog before deleting a facility.
     *
     * @param user     The user (facility owner) to delete.
     * @param position The position in the adapter.
     * Author: Pouyan
     */
    private void showDeleteConfirmationDialog(User user, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Facility?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteFacility(user, position))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes the specified facility using UsersDB and removes it from the RecyclerView.
     *
     * @param user     The user (facility owner) to delete.
     * @param position The position in the adapter.
     * Author: Pouyan
     */
    private void deleteFacility(User user, int position) {
        usersDB.deleteFacility(user.getAndroidId(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                adapter.removeFacility(position); // Remove from the adapter
                Toast.makeText(requireContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads the list of facilities from the database and updates the RecyclerView.
     * Ensures facilities with missing fields are labeled appropriately.
     * Author: Pouyan
     */
    private void loadFacilities() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            List<User> facilities = new ArrayList<>(); // List to hold loaded facilities

            for (DocumentSnapshot doc : querySnapshot) {
                User user = doc.toObject(User.class);
                if (user != null) {
                    String facilityName = user.getUserFacility();
                    String facilityAddress = user.getUserFacilityAddress();

                    // Ensure missing fields are labeled
                    if (facilityName == null || facilityName.trim().isEmpty()) {
                        facilityName = "No name provided";
                    }
                    if (facilityAddress == null || facilityAddress.trim().isEmpty()) {
                        facilityAddress = "No address provided";
                    }

                    // Add only if either name or address is provided
                    if (!facilityName.equals("No name provided") || !facilityAddress.equals("No address provided")) {
                        user.setUserFacility(facilityName);
                        user.setUserFacilityAddress(facilityAddress);
                        facilities.add(user);
                    }
                }
            }

            adapter.updateData(facilities); // Update adapter with loaded facilities
        }).addOnFailureListener(e -> {
            Log.e("loadFacilities", "Failed to load facilities", e); // Log the error
        });
    }
}
