package com.example.rocket_launch.organizer_events_tab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.rocket_launch.ImageStorageDB;
import com.example.rocket_launch.NotificationHelper;
import com.example.rocket_launch.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * Fragment displayed to an organizer when they want to create an event.
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
    private Event event;

    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * Default constructor.
     */
    public CreateNewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsDB = new EventsDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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

        checkBoxWaitlistLimit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editWaitlistLimitSize.setVisibility(View.VISIBLE);
            } else {
                editWaitlistLimitSize.setVisibility(View.INVISIBLE);
                editWaitlistLimitSize.setText(""); // Clear any text if deselected
            }
        });

        editEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventNameOverlay.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initialize buttons
        ImageButton cancelButton = view.findViewById(R.id.cancel_create_new_event_button);
        cancelButton.setOnClickListener(v -> closeFragment());

        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        addEventPosterButton.setOnClickListener(v -> openImagePicker());

        Button createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> {
            String capacityInput = editEventCapacity.getText().toString().trim();

            if (capacityInput.isEmpty()) {
                editEventCapacity.setError("Capacity cannot be empty");
            } else {
                try {
                    int capacityInt = Integer.parseInt(capacityInput);
                    createEvent();
                    sendEventNotification(androidId);
                } catch (NumberFormatException e) {
                    editEventCapacity.setError("Enter a valid Integer");
                }
            }
        });

        return view;
    }

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
            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.sample_poster)
                    .centerCrop()
                    .into(editEventPosterView);

            uploadEventPoster(imageUri);
        }
    }

    private void uploadEventPoster(Uri imageUri) {
        String fileName = "event_pictures/event_" + System.currentTimeMillis() + ".jpg";

        // Use ImageStorageDB to upload the image
        ImageStorageDB.uploadImage(imageUri, fileName,
                downloadUrl -> {
                    if (event.getEventID() == null) {
                        // Generate an event ID if not already set
                        String eventID = UUID.randomUUID().toString();
                        event.setEventID(eventID);
                    }
                    saveImageUrlToEvent(downloadUrl); // Save the download URL
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    }
                },
                e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveImageUrlToEvent(String downloadUrl) {
        if (event.getEventID() == null) {
            Log.e("SaveImageUrl", "Event ID is null, cannot save poster URL.");
            if (isAdded()) {
                Toast.makeText(requireContext(), "Event ID is null. Cannot save poster.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        event.setPosterUrl(downloadUrl); // Set the poster URL in the event object

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events_dev").document(event.getEventID())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded()) {
                        Log.d("SaveImageUrl", "Poster URL saved successfully in events_dev: " + downloadUrl);
                        Toast.makeText(requireContext(), "Event poster updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SaveImageUrl", "Failed to save poster URL: " + e.getMessage());
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed to save event poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createEvent() {
        String eventName = editEventName.getText().toString();
        String eventCapacity = editEventCapacity.getText().toString();
        String waitlistSizeLimit = checkBoxWaitlistLimit.isChecked() ? editWaitlistLimitSize.getText().toString() : null;

        String eventDescription = editEventDescription.getText().toString();
        boolean geolocationRequired = checkBoxGeolocationRequired.isChecked();

        event.setName(eventName);
        event.setCapacity(Integer.parseInt(eventCapacity));
        event.setMaxWaitlistSize(waitlistSizeLimit != null ? Integer.parseInt(waitlistSizeLimit) : -1);
        event.setDescription(eventDescription);
        event.setGeolocationRequired(geolocationRequired);

        String eventID = UUID.randomUUID().toString();
        event.setEventID(eventID);

        event.setOrganizer(androidId);

        eventsDB.addCreatedEvent(event, androidId, v -> closeFragment());
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void sendEventNotification(String androidId) {
        NotificationHelper.sendNotification(androidId, "Event Created", "Your event has been successfully created!");
    }
}
