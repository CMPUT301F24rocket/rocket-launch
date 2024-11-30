package com.example.rocket_launch.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    /**
     * Sets up the UI for the admin events tab.
     *
     * @param inflater Used to inflate the layout.
     * @param container The parent view group.
     * @param savedInstanceState Saved state of the fragment.
     * @return The root view for the fragment.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_event_fragment, container, false);

        // Set up the RecyclerView
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add dividers between RecyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize the adapter with an empty list
        adapter = new AdminEventsAdapter(new ArrayList<>());
        eventsRecyclerView.setAdapter(adapter);

        // Set up the database and load events
        eventsDB = new EventsDB();
        loadEvents();

        return view;
    }

    /**
     * Loads the list of events from the database and updates the RecyclerView.
     * Author: Pouyan
     */
    private void loadEvents() {
        // Fetch events from the Firestore database
        eventsDB.getEventsRef().get().addOnSuccessListener(querySnapshot -> {
            List<Event> events = new ArrayList<>();

            // Convert Firestore documents to Event objects
            for (DocumentSnapshot doc : querySnapshot) {
                Event event = doc.toObject(Event.class);
                if (event != null) {
                    events.add(event);
                }
            }

            // Update the adapter with the fetched events
            adapter.updateData(events);
        }).addOnFailureListener(e -> {
            // Handle errors here if the fetch fails
        });
    }
}
