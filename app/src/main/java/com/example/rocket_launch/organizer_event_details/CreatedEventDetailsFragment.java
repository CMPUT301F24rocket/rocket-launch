package com.example.rocket_launch.organizer_event_details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;


/**
 * fragment used to show details of an event created by an organizer
 */
public class CreatedEventDetailsFragment extends Fragment {
    private EventsDB eventsDB;
    private Event event;

    /**
     * default constructor
     */
    public CreatedEventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * constructor for if we want to pass an existing event to the fragment
     * @param event
     *  event who's details are to be displayed
     */
    public CreatedEventDetailsFragment(Event event) {
        this.event = event;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_created_event_details, container, false);

        TextView eventName = view.findViewById(R.id.organizer_event_details_name);
        eventName.setText(event.getName());
        //back button
        ImageButton backButton = view.findViewById(R.id.organizer_event_details_back_button);
        backButton.setOnClickListener(v -> closeFragment());

        //Button Options
        Button editEventButton = view.findViewById(R.id.edit_event_page_button);
        Button viewEntrantListsButton = view.findViewById(R.id.view_entrant_lists_button);
        Button viewEntrantMapButton = view.findViewById(R.id.view_entrant_map_button);
        Button viewEventQrCodeButton = view.findViewById(R.id.view_event_qr_code_button);

        //edit button
        editEventButton.setOnClickListener(v -> {
            OrganizerEditEventFragment organizerEditEventFragment = new OrganizerEditEventFragment();
            pressButton(organizerEditEventFragment);
        });

        //View entrant list button
        viewEntrantListsButton.setOnClickListener(v -> {
            OrganizerViewEntrantListsFragment organizerViewEntrantListsFragment = new OrganizerViewEntrantListsFragment();
            pressButton(organizerViewEntrantListsFragment);
        });

        //View Entrant Map button
        viewEntrantMapButton.setOnClickListener(v -> {
            OrganizerViewMapFragment organizerViewMapFragment = new OrganizerViewMapFragment();
            pressButton(organizerViewMapFragment);
        });

        //View QR Code Button
        viewEventQrCodeButton.setOnClickListener(v -> {
            OrganizerViewQrCodeFragment organizerViewQrCodeFragment = new OrganizerViewQrCodeFragment(event);
            pressButton(organizerViewQrCodeFragment);
        });

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    /**
     * navigate to the given fragment on button press
     * @param fragment
     *  fragment to navigate to
     */
    private void pressButton(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}