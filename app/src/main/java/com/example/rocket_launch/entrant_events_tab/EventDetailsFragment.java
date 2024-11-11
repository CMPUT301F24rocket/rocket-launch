package com.example.rocket_launch.entrant_events_tab;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * fragment used to show details of an event
 */
public class EventDetailsFragment extends Fragment {

    String eventId;
    Event event;
    EventsDB eventsdb;
    UsersDB usersDB;
    TextView eventNameView;
    TextView eventCapacityView;
    CheckBox eventGeolocationRequired;
    TextView eventDescription;

    Button joinWaitlistButton;

    /**
     * default constructor
     */
    public EventDetailsFragment() {
        // needs to be empty
    }

    /**
     * constructor for passing an eventID
     * @param eventId
     *  id in database for an event
     */
    public EventDetailsFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsdb = new EventsDB();
        usersDB = new UsersDB();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        eventNameView = view.findViewById(R.id.view_event_name);
        eventCapacityView = view.findViewById(R.id.view_event_capacity);
        eventGeolocationRequired = view.findViewById(R.id.view_checkbox_geolocation_requirement);
        eventDescription = view.findViewById(R.id.view_event_description);

        joinWaitlistButton = view.findViewById(R.id.join_waitlist_button);
        view.findViewById(R.id.cancel_button).setOnClickListener(l -> {
            closeFragment();
        });
        getEvent();

        return view;
    }

    /**
     * loads an event with eventId
     */
    private void getEvent() {
        eventsdb.loadEvent(eventId, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        eventNameView.setText(event.getName());
                        eventCapacityView.setText(String.valueOf( event.getCapacity() ));
                        eventGeolocationRequired.setChecked(event.getGeolocationRequired());
                        eventDescription.setText(event.getDescription());
                        // in get event so we cant press before we have event
                        joinWaitlistButton.setOnClickListener(l -> {
                            joinWaitlist();
                        });
                    }
                }
            }
        });
    }

    /**
     * function to join waiting list and update databse accordingly
     */
    private void joinWaitlist() {
        // get user id
        String androidId = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // add to waitlist of event
        eventsdb.addUserToWaitingList(eventId, androidId);

        // add to user's joined events
        usersDB.addWaitlistedEvent(androidId, eventId);
        closeFragment();
    }

    /**
     * closes fragment
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}