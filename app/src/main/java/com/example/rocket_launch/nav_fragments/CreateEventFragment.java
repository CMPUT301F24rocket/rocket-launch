package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.CreateNewEventFragment;
import com.example.rocket_launch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateEventFragment extends Fragment {
    FloatingActionButton addNewEventButton;
    ConstraintLayout eventsBody;


    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        //Set up button to open CreateNewEventFragment
        addNewEventButton = view.findViewById(R.id.add_new_event_button);
        addNewEventButton.setOnClickListener(v -> {
            eventsBody.setVisibility(View.GONE);
            openCreateNewEventFragment();
        });
        // get event body
        eventsBody = view.findViewById(R.id.created_events_body);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Display list of events user has created
        // Click on created event in list and:
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
                .replace(R.id.create_new_event_fragment_container, createNewEventFragment)
                .addToBackStack(null)
                .commit();
    }
}