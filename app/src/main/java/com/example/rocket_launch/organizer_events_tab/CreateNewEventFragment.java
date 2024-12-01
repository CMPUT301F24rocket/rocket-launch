package com.example.rocket_launch.organizer_events_tab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.NotificationHelper;
import com.example.rocket_launch.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


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
    private TextView eventNameOverlay;
    private ImageView editEventPosterView;
    private Event event; // Declare the Event object


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
        event = new Event();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_new_event_fragment, container, false);

        // Initialize views
        editEventName = view.findViewById(R.id.edit_event_name);
        editEventCapacity = view.findViewById(R.id.edit_event_capacity);
        editWaitlistLimitSize = view.findViewById(R.id.edit_waitlist_limit_size);
        editEventDescription = view.findViewById(R.id.edit_event_description);
        checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);
        eventNameOverlay = view.findViewById(R.id.event_name_overlay);
        editEventPosterView = view.findViewById(R.id.edit_event_poster_view);

        // Hide waitlist limit size EditText unless checkbox is checked
        editWaitlistLimitSize.setVisibility(View.INVISIBLE);

        // CheckBox listener for waitlist limit
        checkBoxWaitlistLimit.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                editWaitlistLimitSize.setVisibility(View.VISIBLE);
            } else {
                editWaitlistLimitSize.setVisibility(View.INVISIBLE);
                editWaitlistLimitSize.setText(""); // Clear any text if deselected
            }
        }));

        // TextWatcher to update event name overlay on the image
        editEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {
                // Do nothing
            }
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                eventNameOverlay.setText(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        // Initialize buttons
        ImageButton cancelButton = view.findViewById(R.id.cancel_create_new_event_button);
        cancelButton.setOnClickListener(v -> closeFragment());

        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        addEventPosterButton.setOnClickListener(v -> {
            // TODO: Implement functionality to add custom event poster
        });

        Button createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> {
            // Check if capacity is not null
            String capacityInput = editEventCapacity.getText().toString().trim();

            if (capacityInput.isEmpty()) {
                editEventCapacity.setError("Capacity cannot be empty");
            } else {
                try {
                    int capacityInt = Integer.parseInt(capacityInput); // Verifies input is int
                    createEvent();
                    sendEventNotification(androidId);
                } catch (NumberFormatException e) {
                    editEventCapacity.setError("Enter a valid Integer");
                }
            }
        });
        addEventPosterButton.setOnClickListener(v -> openImagePicker());
        return view;
    }
    private static final int PICK_IMAGE_REQUEST = 1;

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Use Glide to load the selected image into the ImageView
                Glide.with(requireContext())
                        .load(imageUri)
                        .placeholder(R.drawable.sample_poster) // Placeholder while loading
                        .centerCrop() // Ensure the image fits in the dimensions
                        .into(editEventPosterView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadImageToFirebase(imageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        // Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("event_pictures");

        // Generate a unique file name for the image
        String fileName = "event_" + System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        // Upload the file
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Retrieve the uploaded file's download URL
                    String downloadUrl = uri.toString();
                    saveImageUrlToEvent(downloadUrl); // Save URL to the Event object or database
                    Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    // Handle failed upload
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void saveImageUrlToEvent(String downloadUrl) {
        if (event != null) {
            event.setPosterUrl(downloadUrl);

            // Firestore Database reference
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(event.getEventID())
                    .set(event)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Event poster updated successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the exception
                        String errorMessage = e.getMessage(); // Exception's getMessage()
                        Toast.makeText(requireContext(), "Failed to update event poster: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Event is null. Cannot update poster.", Toast.LENGTH_SHORT).show();
        }
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
        event.setEntrantLocationDataList();

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
