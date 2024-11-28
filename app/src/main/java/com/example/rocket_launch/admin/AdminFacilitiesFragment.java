package com.example.rocket_launch.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;
import com.example.rocket_launch.data.Facility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminFacilitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminFacilitiesAdapter adapter;
    private ArrayList<Facility> facilitiesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.admin_facilities_fragment, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.facilities_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add dividers between items
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Initialize adapter with the facilities list
        adapter = new AdminFacilitiesAdapter(facilitiesList);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadFacilities();

        return view;
    }

    private void loadFacilities() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user_info_dev") // Querying user_info_dev collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilitiesList.clear(); // Clear the list to avoid duplicates
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String facilityName = document.getString("userFacility");
                        String facilityAddress = document.getString("userFacilityAddress");

                        // Apply placeholders for missing data
                        if (facilityName == null || facilityName.trim().isEmpty()) {
                            facilityName = "No facility name provided";
                        }
                        if (facilityAddress == null || facilityAddress.trim().isEmpty()) {
                            facilityAddress = "No address provided";
                        }

                        // Add only if facility data exists
                        if (!facilityName.equals("No facility name provided") || !facilityAddress.equals("No address provided")) {
                            facilitiesList.add(new Facility(facilityName, facilityAddress));
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter to refresh RecyclerView
                })
                .addOnFailureListener(e -> {
                    // Log and notify the user of any errors
                    System.out.println("Error fetching facilities: " + e.getMessage());
                });
    }
}
