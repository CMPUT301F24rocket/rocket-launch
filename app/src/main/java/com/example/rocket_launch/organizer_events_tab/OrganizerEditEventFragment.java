package com.example.rocket_launch.organizer_events_tab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.ImageStorageDB;
import com.example.rocket_launch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.UUID;

public class OrganizerEditEventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";
    private static final int PICK_IMAGE_REQUEST = 22;

    // UI elements
    private ImageView editEventPosterView;
    private EditText editEventDescription;
    private Button saveChangesButton;
    private Uri newPosterUri;

    // Firebase and Event object
    private EventsDB eventsDB;
    private Event event;
    private String currentPosterPath;

    public OrganizerEditEventFragment() {
        // Default constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsDB = new EventsDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_edit_event_fragment, container, false);

        // Initialize UI elements
        editEventPosterView = view.findViewById(R.id.edit_event_poster_view);
        editEventDescription = view.findViewById(R.id.edit_event_description);
        saveChangesButton = view.findViewById(R.id.save_event_edits_button);
        ImageButton addPosterButton = view.findViewById(R.id.add_event_poster_button);
        ImageButton cancelButton = view.findViewById(R.id.cancel_edit_event_button);

        // Load event details from Firestore
        fetchEventDetails();

        // Handle adding a new poster
        addPosterButton.setOnClickListener(v -> selectImage());

        // Save changes to event description and poster
        saveChangesButton.setOnClickListener(v -> saveEventEdits());

        // Handle cancel button
        cancelButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    /**
     * Fetch event details from Firestore and populate UI.
     */
    private void fetchEventDetails() {
        String eventId = getArguments() != null ? getArguments().getString(ARG_EVENT_ID) : null;
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "Error: Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        eventsDB.loadEvent(eventId, new OnSuccessListener<Event>() {
            @Override
            public void onSuccess(Event fetchedEvent) {
                if (fetchedEvent != null) {
                    event = fetchedEvent;
                    currentPosterPath = event.getPosterUrl();
                    loadEventData(); // Populate the UI with fetched event details
                } else {
                    Toast.makeText(requireContext(), "Error: Event not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Load event data into UI components.
     */
    private void loadEventData() {
        editEventDescription.setText(event.getDescription());

        // Load poster image
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.sample_poster)
                    .into(editEventPosterView);
        } else {
            editEventPosterView.setImageResource(R.drawable.sample_poster);
        }
    }

    /**
     * Open image picker to select a new poster.
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Event Poster"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            newPosterUri = data.getData();
            try {
                // Display the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), newPosterUri);
                editEventPosterView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save the updated poster and description.
     */
    private void saveEventEdits() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Saving changes...");
        progressDialog.show();

        // Update description
        event.setDescription(editEventDescription.getText().toString());

        if (newPosterUri != null) {
            // Upload the new poster
            String fileName = "event_pictures/event_" + UUID.randomUUID().toString() + ".jpg";
            ImageStorageDB.uploadImage(newPosterUri, fileName,
                    downloadUrl -> {
                        event.setPosterUrl(downloadUrl);
                        updateEventInDatabase(progressDialog);
                    },
                    e -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Failed to upload poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Update the event without changing the poster
            updateEventInDatabase(progressDialog);
        }
    }

    /**
     * Update the event in Firestore.
     */
    private void updateEventInDatabase(ProgressDialog progressDialog) {
        eventsDB.updateEvent(event.getEventID(), event,
                v -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
                    closeFragment();
                },
                e -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Failed to update event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Close the fragment and return to the previous view.
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
