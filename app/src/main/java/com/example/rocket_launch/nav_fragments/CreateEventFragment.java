package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.CreateNewEventFragment;
import com.example.rocket_launch.organizer_event_details.CreatedEventDetailsFragment;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventArrayAdapter;
import com.example.rocket_launch.EventDB;
import com.example.rocket_launch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CreateEventFragment extends Fragment {
    FloatingActionButton addNewEventButton;
    private EventDB eventDB;
    private ListView listView;
    private EventArrayAdapter adapter;
    private ArrayList<Event> events = new ArrayList<>();

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        //Setting up list view and eventDB
        listView = view.findViewById(R.id.organizer_created_events_list);
        eventDB = new EventDB(requireContext());

        adapter = new EventArrayAdapter(requireContext(), events);
        listView.setAdapter(adapter);
        fetchEvents();

        //clicking on a event
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Event clickedEvent = events.get(position);
            String eventId = clickedEvent.getEventID();

            CreatedEventDetailsFragment clickedEventDetailsFragment = new CreatedEventDetailsFragment();
            Bundle bundle = new Bundle();

            bundle.putString("eventID", eventId);
            clickedEventDetailsFragment.setArguments(bundle);

            if (eventId == null){
                Log.e("Event Click", "EventID is null for event at position: " + position);
            } else{
                Log.d("Event Click", "EventID is not null at position" + position);
            }

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

        //TODO: Display list of events user has created CHECK
        // Click on created event in list and: CHECK
        // Menu where they can
        // - Edit Event Details
        // - View Entrant list
        // - View Entrant map
        // - See Event QR code

    }

    private void openCreateNewEventFragment(){
        CreateNewEventFragment createNewEventFragment = new CreateNewEventFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, createNewEventFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openClickedEvent(CreatedEventDetailsFragment clickedEventDetailsFragment){

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, clickedEventDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchEvents(){
        eventDB.getAllOrganizerEvents(new OnSuccessListener<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                //update list adapter data with fetched events
                CreateEventFragment.this.events.clear();
                CreateEventFragment.this.events.addAll(events);
                adapter.notifyDataSetChanged();
            }
        }, new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to load events into list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}