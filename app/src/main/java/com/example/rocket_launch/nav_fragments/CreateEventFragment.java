package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventArrayAdapter;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.organizer_events_tab.CreateNewEventFragment;
import com.example.rocket_launch.organizer_events_tab.CreatedEventDetailsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * fragment used for displaying events created by an organizer
 */
public class CreateEventFragment extends Fragment {
    FloatingActionButton addNewEventButton;
    private EventsDB eventsDB;
    private UsersDB usersDB;
    private ListView listView;
    private EventArrayAdapter adapter;
    private ArrayList<Event> events;
    private String androidID;

    /**
     * default constructor
     */
    public CreateEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        //Setting up list view and eventDB
        listView = view.findViewById(R.id.organizer_created_events_list);
        eventsDB = new EventsDB();
        usersDB = new UsersDB();
        adapter = new EventArrayAdapter(requireContext(), events);
        listView.setAdapter(adapter);
        fetchEvents();

        //clicking on a event
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Event clickedEvent = events.get(position);

            CreatedEventDetailsFragment clickedEventDetailsFragment = new CreatedEventDetailsFragment(clickedEvent);

            openClickedEvent(clickedEventDetailsFragment);
        });

        //Set up button to open CreateNewEventFragment
        addNewEventButton = view.findViewById(R.id.add_new_event_button);
        addNewEventButton.setOnClickListener(v -> {
            openCreateNewEventFragment();
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidID = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        events = new ArrayList<>();

        //TODO:
        // - View Entrant map
    }

    /**
     * function used for opening a fragment for organizer to create an event
     */
    private void openCreateNewEventFragment(){
        CreateNewEventFragment createNewEventFragment = new CreateNewEventFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, createNewEventFragment)
                .addToBackStack("")
                .commit();
    }

    /**
     * function used for opening a fragment to display contents of an event
     * @param clickedEventDetailsFragment
     *  fragment that displays information needed to create an event
     */
    private void openClickedEvent(CreatedEventDetailsFragment clickedEventDetailsFragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, clickedEventDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * function that fetches all events created by an organizer and loads them
     */
    private void fetchEvents() {
        usersDB.getCreatedEventIds(androidID, eventTitleList -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            eventsDB.getAllEventsInList(eventTitleList, eventList -> {
                events.clear(); // Clear the old list

                for (Event event : eventList) {
                    if (event != null) {
                        events.add(event); // Add the event to the list
                        Log.d("FetchEvents", "Event: " + event.getName() + ", Poster URL: " + event.getPosterUrl());
                    }
                }

                adapter.notifyDataSetChanged(); // Notify the adapter of changes
            }, e -> {
                Log.e("FetchEvents", "Error fetching events from events_dev collection", e);
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            });
        }, e -> Log.e("FetchEvents", "Error fetching event IDs", e));
    }




}