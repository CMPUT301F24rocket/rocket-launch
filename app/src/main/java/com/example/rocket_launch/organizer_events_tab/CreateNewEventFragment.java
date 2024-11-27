package com.example.rocket_launch.organizer_events_tab;

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

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.NotificationHelper;
import com.example.rocket_launch.R;


/**
 * fragment displayed to an organizer when they want to create an event
 */
public class CreateNewEventFragment extends Fragment {
    private EventsDB eventsDB;
    private String androidId;

    private EditText editEventName;
    private EditText editEventCapacity;
    private EditText editWaitlistLimitSize;
    private EditText editEventDescription;
    private CheckBox checkBoxGeolocationRequired;
    private CheckBox checkBoxWaitlistLimit;


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

        editEventName = view.findViewById(R.id.edit_event_name);
        editEventCapacity = view.findViewById(R.id.edit_event_capacity);
        editWaitlistLimitSize = view.findViewById(R.id.edit_waitlist_limit_size);
        editEventDescription = view.findViewById(R.id.edit_event_description);
        checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);

        //hide setWaitlistLimit edit text field unless they check the setWaitlist box
        editWaitlistLimitSize.setVisibility(View.INVISIBLE);

        //check box listener
        checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);
        editWaitlistLimitSize = view.findViewById(R.id.edit_waitlist_limit_size);

        checkBoxWaitlistLimit.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked){
                view.findViewById(R.id.edit_waitlist_limit_size).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.edit_waitlist_limit_size).setVisibility(View.INVISIBLE);
                editWaitlistLimitSize.setText(""); //clear any text if deselected
            }
        }));


        // initialize buttons
        ImageButton cancelButton = view.findViewById(R.id.cancel_create_new_event_button);
        cancelButton.setOnClickListener(v -> closeFragment());

        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        addEventPosterButton.setOnClickListener(v ->{
            // TODO
        });

        Button createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> {
            //check if capacity is not null
            String capacityInput = editEventCapacity.getText().toString().trim();

            if (capacityInput.isEmpty()){
                editEventCapacity.setError("Capacity cannot be empty");
            } else {
                try {
                    int capacityInt = Integer.parseInt(capacityInput); // verifies input is int
                    createEvent();
                    sendEventNotification(androidId);
                } catch (NumberFormatException e){
                    editEventCapacity.setError("Enter a valid Integer");
                }
            }
        });

        //TODO: choose event poster image

        return view;
    }

    /**
     * function used to load new event into database with proper values
     */
    private void createEvent(){
        Event event = new Event();

        //getting input from edit fields
        String eventName = editEventName.getText().toString();
        String eventCapacity = editEventCapacity.getText().toString();
        String waitlistSizeLimit = checkBoxWaitlistLimit.isChecked() ? editWaitlistLimitSize.getText().toString() : null;;

        String eventDescription = editEventDescription.getText().toString();
        boolean geolocationRequired = checkBoxGeolocationRequired.isChecked();

        event.setName(eventName);
        event.setCapacity(Integer.parseInt(eventCapacity));
        // set MaxWaitlistSize to be -1 if not specified
        event.setMaxWaitlistSize(waitlistSizeLimit != null ? Integer.parseInt(waitlistSizeLimit) : -1);
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

    private void sendEventNotification(String androidId) {
        // Ensure the notification channel is created
        NotificationHelper.createNotificationChannel(requireContext());

        // Show the notification
        NotificationHelper.showNotification(
                requireContext(),
                "Event Created",
                "Your event has been successfully created!",
                1 // Unique notification ID
        );

        // Add notification to the user's database entry
        NotificationHelper.addNotificationToDatabase(
                androidId,
                "Event Created",
                "Your event has been successfully created!"
        );
    }
}
