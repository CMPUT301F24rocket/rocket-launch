package com.example.rocket_launch.entrant_events_tab;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

/**
 * fragment to show details about a waitlisted event
 */
public class WaitlistedEventDetailsFragment extends Fragment {
    String eventId;
    Event event;
    EventsDB eventsdb;
    UsersDB usersDB;
    TextView eventNameView;
    TextView eventWaitlistCapacityView;
    CheckBox eventGeolocationRequired;
    TextView eventDescription;
    LinearLayout eventCapacityLayout;
    boolean locationRequired;

    Button cancelWaitlistButton;

    public WaitlistedEventDetailsFragment() {
        // needs to be empty
    }

    WaitlistedEventDetailsFragment(Event event) {
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
        View view = inflater.inflate(R.layout.fragment_waitlisted_event_details, container, false);

        eventNameView = view.findViewById(R.id.view_event_name);
        eventWaitlistCapacityView = view.findViewById(R.id.view_waitlist_capacity);
        eventGeolocationRequired = view.findViewById(R.id.view_checkbox_geolocation_requirement);
        eventDescription = view.findViewById(R.id.view_event_description);
        eventCapacityLayout = view.findViewById(R.id.waitlist_capacity_layout);

        cancelWaitlistButton = view.findViewById(R.id.cancel_waitlist_button);
        cancelWaitlistButton.setOnClickListener(l -> {
        leaveWaitlist();
        });

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
                        if (event.getMaxWaitlistSize() != -1) {
                            eventCapacityLayout.setVisibility(View.VISIBLE);
                            eventWaitlistCapacityView.setText(String.format(
                                    Locale.CANADA, "%d / %d", event.getWaitingList().size(),
                                    event.getCapacity()));
                        }
                        eventGeolocationRequired.setChecked(event.getGeolocationRequired());
                        locationRequired = event.getGeolocationRequired();
                        eventDescription.setText(event.getDescription());
                        // in get event so we cant press before we have event
                        cancelWaitlistButton.setOnClickListener(l -> {
                            leaveWaitlist();
                        });
                    }
                }
            }
        });
    }

    /**
     * function to leave waiting list and update databse accordingly
     */
    private void leaveWaitlist() {
        // get user id
        String androidId = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (locationRequired == true){ //remove location data from list as well
            eventsdb.removeFromEntrantLocationDataList(event.getEventID(), androidId);
            eventsdb.removeUserFromWaitingList(event.getEventID(), androidId);
            usersDB.removeWaitlistedEvent(androidId, event.getEventID());

        } else{
            // remove from waitlist of event
            eventsdb.removeUserFromWaitingList(event.getEventID(), androidId);
            // remove from user's joined events
            usersDB.removeWaitlistedEvent(androidId, event.getEventID());
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