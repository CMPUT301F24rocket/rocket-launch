package com.example.rocket_launch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class CreateNewEventFragment extends Fragment {

    public CreateNewEventFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_event_fragment, container, false);

        //Initializing Buttons
        ImageButton cancelButton = view.findViewById(R.id.cancel_create_new_event_button);
        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        Button createEventButton = view.findViewById(R.id.save_event_edits_button);

        //If Buttons are pressed
        cancelButton.setOnClickListener(v -> closeFragment());
        addEventPosterButton.setOnClickListener(v ->{});
        createEventButton.setOnClickListener(v -> {});

        //TODO: choose event poster image
        // save event details into eventDB (should store based on user android ID as well)
        // generate QR code for event + store in eventDB

        return view;
    }

    // Close the fragment and return to the Created Activities view
    private void closeFragment() {
        requireActivity().findViewById(R.id.created_events_body).setVisibility(View.VISIBLE);
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
