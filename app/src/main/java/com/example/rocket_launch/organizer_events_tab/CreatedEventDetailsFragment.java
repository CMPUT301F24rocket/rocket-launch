package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;

/**
 * Fragment used to show details of an event created by an organizer.
 */
public class CreatedEventDetailsFragment extends Fragment {
    private EventsDB eventsDB;
    private Event event;

    /**
     * Default constructor.
     */
    public CreatedEventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor for if we want to pass an existing event to the fragment.
     * @param event Event whose details are to be displayed.
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
        ImageView eventPoster = view.findViewById(R.id.organizer_event_details_poster);

        // Set event name
        eventName.setText(event.getName());

        // Load the event poster
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.sample_poster) // Placeholder while loading
                    .centerCrop()
                    .into(eventPoster);
        } else {
            // Set placeholder if no poster URL is available
            eventPoster.setImageResource(R.drawable.sample_poster);
        }

        // Back button
        ImageButton backButton = view.findViewById(R.id.organizer_event_details_back_button);
        backButton.setOnClickListener(v -> closeFragment());

        // Button Options
        Button editEventButton = view.findViewById(R.id.edit_event_page_button);
        Button viewEntrantListsButton = view.findViewById(R.id.view_entrant_lists_button);
        Button viewEntrantMapButton = view.findViewById(R.id.view_entrant_map_button);
        Button viewEventQrCodeButton = view.findViewById(R.id.view_event_qr_code_button);

        // Create bundle for passing eventID
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getEventID());

        // Edit button
        editEventButton.setOnClickListener(v -> {
            OrganizerEditEventFragment organizerEditEventFragment = new OrganizerEditEventFragment();
            organizerEditEventFragment.setArguments(bundle);
            pressButton(organizerEditEventFragment);
        });

        // View entrant list button
        viewEntrantListsButton.setOnClickListener(v -> {
            OrganizerViewEntrantListsFragment organizerViewEntrantListsFragment = new OrganizerViewEntrantListsFragment();
            organizerViewEntrantListsFragment.setArguments(bundle);
            pressButton(organizerViewEntrantListsFragment);
        });

        // View Entrant Map button
        viewEntrantMapButton.setOnClickListener(v -> {
            OrganizerViewMapFragment organizerViewMapFragment = new OrganizerViewMapFragment();
            organizerViewMapFragment.setArguments(bundle);
            pressButton(organizerViewMapFragment);
        });

        // View QR Code Button
        viewEventQrCodeButton.setOnClickListener(v -> {
            OrganizerViewQrCodeFragment organizerViewQrCodeFragment = new OrganizerViewQrCodeFragment(event);
            organizerViewQrCodeFragment.setArguments(bundle);
            pressButton(organizerViewQrCodeFragment);
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle arguments if needed
        }
    }

    /**
     * Navigate to the given fragment on button press.
     * @param fragment Fragment to navigate to.
     */
    private void pressButton(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Close the fragment and return to the Created Activities view.
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
