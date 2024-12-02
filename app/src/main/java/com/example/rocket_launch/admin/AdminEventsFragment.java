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
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for managing events in the admin panel.
 * Allows the admin to view, delete, and manage events.
 * Author: Pouyan
 */
public class AdminEventsFragment extends Fragment {
    private RecyclerView eventsRecyclerView;
    private AdminEventsAdapter adapter;
    private EventsDB eventsDB; // Database handler for events
    private UsersDB usersDB;   // Database handler for users

    /**
     * Sets up the fragment's layout and initializes components.
     *
     * @param inflater  Used to inflate the XML layout.
     * @param container Parent container for this fragment.
     * @param savedInstanceState Saved instance state (if any).
     * @return The root view of the fragment.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_event_fragment, container, false);

        // Initialize RecyclerView
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add dividers between items in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set up the adapter for the RecyclerView
        adapter = new AdminEventsAdapter(new ArrayList<>());
        eventsRecyclerView.setAdapter(adapter);

        // Initialize database handlers
        eventsDB = new EventsDB();
        usersDB = new UsersDB();

        // Load events into the adapter
        loadEvents();

        // Set up long-press listener for event deletion
        adapter.setOnEventDeleteListener((event, position) -> showDeleteConfirmationDialog(event, position));

        return view;
    }

    /**
     * Shows a confirmation dialog before deleting an event.
     *
     * @param event    The event to delete.
     * @param position Position of the event in the list.
     * Author: Pouyan
     */
    private void showDeleteConfirmationDialog(Event event, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Event?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(event, position))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes an event from the database and updates the UI.
     *
     * @param event    The event to delete.
     * @param position Position of the event in the list.
     * Author: Pouyan
     */
    private void deleteEvent(Event event, int position) {
        eventsDB.deleteEvent(event.getEventID(),
                success -> {
                    // Remove the event from the list and show success message
                    adapter.removeEvent(position);
                    Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                },
                failure -> {
                    // Show failure message and log the error
                    Toast.makeText(requireContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                    Log.e("AdminEventsFragment", "Error deleting event", failure);
                });
    }

    /**
     * Loads events from the Firestore database and populates the RecyclerView.
     * Author: Pouyan
     */
    private void loadEvents() {
        eventsDB.getEventsRef().get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();

                    // Loop through Firestore documents and create Event objects
                    for (DocumentSnapshot doc : querySnapshot) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            // Handle empty or missing fields with default values
                            if (event.getName() == null || event.getName().trim().isEmpty()) {
                                event.setName("No name provided");
                            }
                            if (event.getDescription() == null || event.getDescription().trim().isEmpty()) {
                                event.setDescription("No description provided");
                            }
                            events.add(event);
                        }
                    }

                    // Update the adapter with the loaded events
                    adapter.updateData(events);
                })
                .addOnFailureListener(e -> Log.e("AdminEventsFragment", "Failed to load events", e));
    }
}
