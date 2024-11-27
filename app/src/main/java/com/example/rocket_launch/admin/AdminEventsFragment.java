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
 * Fragment for displaying contents of events tab for admin
 */
public class AdminEventsFragment extends Fragment {
    private RecyclerView eventsRecyclerView;
    private AdminEventsAdapter adapter;
    private EventsDB eventsDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_event_fragment, container, false);

        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new AdminEventsAdapter(new ArrayList<>());
        eventsRecyclerView.setAdapter(adapter);

        eventsDB = new EventsDB();
        loadEvents();

        return view;
    }

    private void loadEvents() {
        eventsDB.getEventsRef().get().addOnSuccessListener(querySnapshot -> {
            List<Event> events = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Event event = doc.toObject(Event.class);
                if (event != null) {
                    events.add(event);
                }
            }
            adapter.updateData(events);
        }).addOnFailureListener(e -> {
            // Handle failure if necessary
        });
    }
}