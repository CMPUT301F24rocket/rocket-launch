package com.example.rocket_launch.organizer_event_details;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.SelectRolesFragment;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.nav_fragments.CreateEventFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * fragment displayed to an organizer when they want to create an event
 */
public class CreateNewEventFragment extends Fragment {
    private EventsDB eventsDB;
    private String androidId;

    /**
     * default constructor
     */
    public CreateNewEventFragment(){
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventsDB = new EventsDB();
        androidId = Settings.Secure.getString((requireContext()).getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_event_fragment, container, false);

        //Initializing Buttons
        ImageButton cancelButton = view.findViewById(R.id.cancel_create_new_event_button);
        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        Button createEventButton = view.findViewById(R.id.create_event_button);

        //hide setWaitlistLimit edit text field unless they check the setWaitlist box
        view.findViewById(R.id.edit_waitlist_limit_size).setVisibility(View.INVISIBLE);

        //check box listener
        CheckBox checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        CheckBox checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);
        EditText editWaitlistLimitSize = view.findViewById(R.id.edit_waitlist_limit_size);

        checkBoxWaitlistLimit.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked){
                view.findViewById(R.id.edit_waitlist_limit_size).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.edit_waitlist_limit_size).setVisibility(View.INVISIBLE);
                editWaitlistLimitSize.setText(""); //clear any text if deselected
            }
        }));

        //If Buttons are pressed
        cancelButton.setOnClickListener(v -> closeFragment());
        addEventPosterButton.setOnClickListener(v ->{});

        //When Create Event Button is clicked
        createEventButton.setOnClickListener(v -> {
            //check if capacity is not null
            EditText capacity = view.findViewById(R.id.edit_event_capacity);
            String capacityInput = capacity.getText().toString().trim();

            if (capacityInput.isEmpty()){
                capacity.setError("Capacity cannot be empty");
            } else {
                try {
                    int capacityInt = Integer.parseInt(capacityInput);
                    createEvent(view);
                } catch (NumberFormatException e){
                    capacity.setError("Enter a valid Integer");
                }
            }
        });

        //TODO: choose event poster image
        // save event details into eventDB (should store based on user android ID as well)
        // generate QR code for event + store in eventDB

        return view;
    }

    /**
     * function used to tell db to create a new event
     * @param view
     *  the current view holding all the data
     */
    private void createEvent(View view){
        EditText editEventName = view.findViewById(R.id.edit_event_name);
        EditText editEventCapacity = view.findViewById(R.id.edit_event_capacity);
        EditText editWaitlistLimitSize = view.findViewById(R.id.edit_waitlist_limit_size);
        EditText editEventDescription = view.findViewById(R.id.edit_event_description);
        CheckBox checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        CheckBox checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);

        Event event = new Event();

        //getting input from edit fields
        String eventName = editEventName.getText().toString();
        String eventCapacity = editEventCapacity.getText().toString();
        String waitlistSizeLimit = checkBoxWaitlistLimit.isChecked() ? editWaitlistLimitSize.getText().toString() : null;;

        String eventDescription = editEventDescription.getText().toString();
        boolean geolocationRequired = checkBoxGeolocationRequired.isChecked();

        event.setName(eventName);
        event.setCapacity(Integer.parseInt(eventCapacity));
        event.setMaxWaitlistSize(waitlistSizeLimit != null ? Integer.parseInt(waitlistSizeLimit) : 0);
        event.setDescription(eventDescription);
        event.setGeolocationRequired(geolocationRequired);
        event.setWaitingList();

        eventsDB.addCreatedEvent(event, androidId, v -> {
            closeFragment();
        });


    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
