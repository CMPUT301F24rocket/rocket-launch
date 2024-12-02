package com.example.rocket_launch.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.data.Facility;

import java.util.ArrayList;

/**
 * Fragment for managing facilities in the admin view.
 * Author: Pouyan
 */
public class AdminFacilitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminFacilitiesAdapter adapter;
    private ArrayList<Facility> facilitiesList = new ArrayList<>();
    private UsersDB usersDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_facilities_fragment, container, false);

        recyclerView = view.findViewById(R.id.facilities_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        adapter = new AdminFacilitiesAdapter(facilitiesList, (position) -> {
            Facility facility = facilitiesList.get(position);
            showDeleteConfirmation(facility, position);
        });

        recyclerView.setAdapter(adapter);

        usersDB = new UsersDB();
        loadFacilities();

        return view;
    }

    /**
     * Loads facilities from Firestore into the RecyclerView.
     * Includes all facilities, even those missing addresses.
     * Author: Pouyan
     */
    private void loadFacilities() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            facilitiesList.clear(); // Clear the list to avoid duplicates
            querySnapshot.forEach(doc -> {
                String facilityName = doc.getString("userFacility");
                String facilityAddress = doc.getString("userFacilityAddress");

                // Check if both fields are missing; skip such facilities
                if ((facilityName == null || facilityName.trim().isEmpty()) &&
                        (facilityAddress == null || facilityAddress.trim().isEmpty())) {
                    return; // Skip this facility entirely
                }

                // Handle missing name or address with placeholders
                if (facilityName == null || facilityName.trim().isEmpty()) {
                    facilityName = "No name provided";
                }
                if (facilityAddress == null || facilityAddress.trim().isEmpty()) {
                    facilityAddress = "No address provided";
                }

                // Add the facility to the list
                facilitiesList.add(new Facility(facilityName, facilityAddress, doc.getId()));
            });
            adapter.notifyDataSetChanged(); // Notify adapter to refresh RecyclerView
        }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to load facilities.", Toast.LENGTH_SHORT).show());
    }


    /**
     * Shows a confirmation dialog before deleting a facility.
     *
     * @param facility The facility to delete.
     * @param position The position of the facility in the list.
     * Author: Pouyan
     */
    private void showDeleteConfirmation(Facility facility, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Facility?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteFacility(facility, position))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes a facility from Firestore and removes it from the list.
     *
     * @param facility The facility to delete.
     * @param position The position of the facility in the list.
     * Author: Pouyan
     */
    private void deleteFacility(Facility facility, int position) {
        usersDB.getUsersRef().document(facility.getUserId()).update("userFacility", "", "userFacilityAddress", "")
                .addOnSuccessListener(unused -> {
                    facilitiesList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to delete facility", Toast.LENGTH_SHORT).show());
    }
}
