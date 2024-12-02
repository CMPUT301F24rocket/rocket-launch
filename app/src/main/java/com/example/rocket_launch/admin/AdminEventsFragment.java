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
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying events in the admin section.
 * Author: Pouyan
 */
public class AdminEventsFragment extends Fragment {
    private RecyclerView eventsRecyclerView;
    private AdminEventsAdapter adapter;
    private EventsDB eventsDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_event_fragment, container, false);

        // Set up the RecyclerView
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add dividers between RecyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize the adapter with an empty list
        adapter = new AdminEventsAdapter(new ArrayList<>());
        eventsRecyclerView.setAdapter(adapter);

        // Set up the database and load events
        eventsDB = new EventsDB();
        loadEvents();

        // Handle delete events
        adapter.setOnEventDeleteListener(this::showDeleteConfirmationDialog);

        return view;
    }

    /**
     * Shows a confirmation dialog before deleting an event.
     *
     * @param event The event to delete.
     */
    private void showDeleteConfirmationDialog(Event event) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Event?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes the specified event from the database.
     *
     * @param event The event to delete.
     */
    private void deleteEvent(Event event) {
        eventsDB.deleteEvent(event.getEventID(),
                success -> {
                    Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    loadEvents(); // Refresh the list
                },
                failure -> {
                    Toast.makeText(requireContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                    Log.e("AdminEventsFragment", "Error deleting event", failure);
                });
    }

    /**
     * Loads the list of events from the database and updates the RecyclerView.
     * Ensures missing names or descriptions are labeled properly.
     */
    private void loadEvents() {
        eventsDB.getEventsRef().get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            // Ensure missing fields are handled
                            if (event.getName() == null || event.getName().trim().isEmpty()) {
                                event.setName("No name provided");
                            }
                            if (event.getDescription() == null || event.getDescription().trim().isEmpty()) {
                                event.setDescription("No description provided");
                            }
                            events.add(event);
                        }
                    }

                    adapter.updateData(events);
                })
                .addOnFailureListener(e -> Log.e("AdminEventsFragment", "Failed to load events", e));
    }
}
