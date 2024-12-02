package com.example.rocket_launch.entrant_events_tab;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

/**
 * fragment to show details about a registered event
 */
public class RegisteredEventDetailsFragment extends Fragment {
    private String eventId;
    private Event event;
    private EventsDB eventsdb;
    private UsersDB usersDB;
    private TextView eventNameView;
    private TextView eventCapacityView;
    private CheckBox eventGeolocationRequired;
    private TextView eventDescription;

    private Button removeRegistrationButton;


    /**
     * Empty constructor
     */
    public RegisteredEventDetailsFragment() {
        // needs to be empty
    }

    /**
     * Constructor that has an event parameter
     * @param event Registered event
     */
    public RegisteredEventDetailsFragment(Event event) {
        this.event = event;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsdb = new EventsDB();
        usersDB = new UsersDB();

    }

    ImageView eventPosterView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_event_details, container, false);

        eventNameView = view.findViewById(R.id.view_event_name);
        eventCapacityView = view.findViewById(R.id.view_event_capacity);
        eventGeolocationRequired = view.findViewById(R.id.view_checkbox_geolocation_requirement);
        eventDescription = view.findViewById(R.id.view_event_description);
        eventPosterView = view.findViewById(R.id.event_poster_view);

        removeRegistrationButton = view.findViewById(R.id.cancel_registration_button);
        removeRegistrationButton.setOnClickListener(l -> removeRegisterEvent());

        view.findViewById(R.id.cancel_button).setOnClickListener(l -> closeFragment());
        getEvent();

        return view;
    }


    /**
     * loads an event with eventId
     */
    private void getEvent() {
        // Set default poster to sample_poster.png initially
        eventPosterView.setImageResource(R.drawable.sample_poster);

        eventsdb.loadEvent(event.getEventID(), new OnSuccessListener<Event>() {
            @Override
            public void onSuccess(Event loadedEvent) {
                if (loadedEvent != null) {
                    event = loadedEvent;
                    eventNameView.setText(event.getName());
                    eventCapacityView.setText(String.valueOf(event.getCapacity()));
                    eventGeolocationRequired.setChecked(event.getGeolocationRequired());
                    eventDescription.setText(event.getDescription());

                    // Try loading the poster image from the URL
                    if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        Picasso.get()
                                .load(event.getPosterUrl())
                                .placeholder(R.drawable.sample_poster) // Show sample_poster while loading
                                .error(R.drawable.sample_poster) // Show sample_poster if URL fails
                                .into(eventPosterView);
                    } else {
                        // If no URL is provided, sample_poster.png remains as the default
                        eventPosterView.setImageResource(R.drawable.sample_poster);
                    }
                }
            }
        });
    }

    /**
     * function to allow user to register in an event
     */
    private void removeRegisterEvent() {
        // get user id
        String androidId = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // remove from registered events
        eventsdb.removeUserFromRegisteredList(event.getEventID(), androidId);

        // remove from registered events
        usersDB.removeRegisteredEvent(androidId, event.getEventID());
        closeFragment();
    }

    /**
     * closes fragment
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}