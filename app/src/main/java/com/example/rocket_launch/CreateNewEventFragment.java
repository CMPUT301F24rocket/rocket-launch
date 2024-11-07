package com.example.rocket_launch;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNewEventFragment extends Fragment {
    private FirebaseFirestore db;
    private String androidId;

    public CreateNewEventFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        androidId = Settings.Secure.getString((requireContext()).getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_event_fragment, container, false);

        //Initializing Buttons
        ImageButton cancelButton = view.findViewById(R.id.cancel_create_new_event_button);
        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        Button createEventButton = view.findViewById(R.id.save_event_edits_button);

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
        createEventButton.setOnClickListener(v -> createEvent(view));

        //TODO: choose event poster image
        // save event details into eventDB (should store based on user android ID as well)
        // generate QR code for event + store in eventDB

        return view;
    }

    //Create new event
    private void createEvent(View view){
        EditText editEventName = view.findViewById(R.id.edit_event_name);
        EditText editEventCapacity = view.findViewById(R.id.edit_event_capacity);
        EditText editWaitlistLimitSize = view.findViewById(R.id.edit_waitlist_limit_size);
        EditText editEventDescription = view.findViewById(R.id.edit_event_description);
        CheckBox checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        CheckBox checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);

        //getting input from edit fields
        String eventName = editEventName.getText().toString();
        String eventCapacity = editEventCapacity.getText().toString();
        String waitlistSizeLimit;

        if (checkBoxWaitlistLimit.isChecked())
            waitlistSizeLimit = editWaitlistLimitSize.getText().toString();
        else waitlistSizeLimit = null;

        String eventDescription = editEventDescription.getText().toString();
        boolean geolocationRequired = checkBoxGeolocationRequired.isChecked();

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", eventName);
        eventMap.put("capacity", eventCapacity);
        eventMap.put("waitlist_size_limit", waitlistSizeLimit);
        eventMap.put("description", eventDescription);
        eventMap.put("geolocation_required", geolocationRequired);

        db.collection("organizer_events")
                .document(androidId)
                .collection("events")
                .add(eventMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Event added successfully with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding event", e);
                });
    }

    // Close the fragment and return to the Created Activities view
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
