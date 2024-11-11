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
 * fragment to show details about a registered event
 */
public class RegisteredEventDetailsFragment extends Fragment {
    String eventId;
    Event event;
    EventsDB eventsdb;
    UsersDB usersDB;
    TextView eventNameView;
    TextView eventCapacityView;
    CheckBox eventGeolocationRequired;
    TextView eventDescription;

    Button removeRegistrationButton;


    public RegisteredEventDetailsFragment() {
        // needs to be empty
    }

    public RegisteredEventDetailsFragment(Event event) {
        this.event = event;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsdb = new EventsDB();
        usersDB = new UsersDB();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_event_details, container, false);

        eventNameView = view.findViewById(R.id.view_event_name);
        eventCapacityView = view.findViewById(R.id.view_event_capacity);
        eventGeolocationRequired = view.findViewById(R.id.view_checkbox_geolocation_requirement);
        eventDescription = view.findViewById(R.id.view_event_description);

        removeRegistrationButton = view.findViewById(R.id.cancel_registration_button);
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
        eventsdb.loadEvent(event.getEventID(), new OnSuccessListener<DocumentSnapshot>() {
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
                        removeRegistrationButton.setOnClickListener(l -> {

                        });
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
        eventsdb.removeUserFromRegisteredList(eventId, androidId);

        // remove from registered events
        usersDB.removeRegisteredEvent(androidId, eventId);
        closeFragment();
    }

    /**
     * closes fragment
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}