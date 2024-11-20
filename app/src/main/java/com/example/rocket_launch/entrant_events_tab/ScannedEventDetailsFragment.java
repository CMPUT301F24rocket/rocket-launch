package com.example.rocket_launch.entrant_events_tab;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

/**
 * fragment used to show details of an event
 */
public class ScannedEventDetailsFragment extends Fragment {

    String androidId;
    String eventId;
    Event event;
    EventsDB eventsdb;
    UsersDB usersDB;
    TextView eventNameView;
    TextView eventWaitlistCapacityView;
    CheckBox eventGeolocationRequired;
    TextView eventDescription;
    LinearLayout eventCapacityLayout;

    Button joinWaitlistButton;

    /**
     * default constructor
     */
    public ScannedEventDetailsFragment() {
        // needs to be empty
    }

    /**
     * constructor for passing an eventID
     * @param eventId   //
     *  id in database for an event
     */
    public ScannedEventDetailsFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsdb = new EventsDB();
        usersDB = new UsersDB();
        androidId = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_event_details, container, false);

        eventNameView = view.findViewById(R.id.view_event_name);
        eventWaitlistCapacityView = view.findViewById(R.id.view_waitlist_capacity);
        eventGeolocationRequired = view.findViewById(R.id.view_checkbox_geolocation_requirement);
        eventDescription = view.findViewById(R.id.view_event_description);
        eventCapacityLayout = view.findViewById(R.id.waitlist_capacity_layout);

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
                        if (event.getMaxWaitlistSize() != -1) {
                            eventCapacityLayout.setVisibility(View.VISIBLE);
                            eventWaitlistCapacityView.setText(String.format(
                                    Locale.CANADA, "%d / %d", event.getWaitingList().size(),
                                    event.getCapacity()));
                        }
                        eventNameView.setText(event.getName());
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
        // -1 specifies that it the waitlist is unlimited
        if (event.getWaitingList().size() < event.getMaxWaitlistSize() || event.getMaxWaitlistSize() == -1) {
            // add to waitlist of event
            eventsdb.addUserToWaitingList(eventId, androidId);

            // add to user's joined events
            usersDB.addWaitlistedEvent(androidId, eventId);
        }
        else {
            Toast.makeText(requireContext(), "Waitlist full", Toast.LENGTH_LONG).show();
            Log.d("Firebase", "Waiting list is full");
        }
        closeFragment();
    }

    /**
     * closes fragment
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}