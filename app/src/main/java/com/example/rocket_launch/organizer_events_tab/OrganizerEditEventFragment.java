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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

/**
 * Fragment to edit an event, including updating the poster image.
 */
public class OrganizerEditEventFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 22;

    // Firebase storage
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    // UI elements
    private ImageView editEventPosterView;
    private EditText editEventName, editEventCapacity, editEventDescription;
    private CheckBox checkBoxGeolocationRequired, checkBoxWaitlistLimit;
    private Uri newPosterUri;

    // Event object
    private Event event;

    /**
     * Default constructor
     */
    public OrganizerEditEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("event_pictures");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_edit_event_fragment, container, false);

        // Initialize UI elements
        editEventPosterView = view.findViewById(R.id.edit_event_poster_view);
        editEventName = view.findViewById(R.id.edit_event_name);
        editEventCapacity = view.findViewById(R.id.edit_event_capacity);
        editEventDescription = view.findViewById(R.id.edit_event_description);
        checkBoxGeolocationRequired = view.findViewById(R.id.checkbox_geolocation_requirement);
        checkBoxWaitlistLimit = view.findViewById(R.id.checkbox_waitlist_limit);

        // Set up event data
        if (event != null) {
            loadEventData();
        }

        // Back button functionality
        ImageButton backButton = view.findViewById(R.id.cancel_edit_event_button);
        backButton.setOnClickListener(v -> closeFragment());

        // Add new poster functionality
        ImageButton addEventPosterButton = view.findViewById(R.id.add_event_poster_button);
        addEventPosterButton.setOnClickListener(v -> selectImage());

        // Save changes functionality
        Button saveButton = view.findViewById(R.id.save_event_edits_button);
        saveButton.setOnClickListener(v -> saveEventEdits());

        return view;
    }

    /**
     * Load event data into the UI.
     */
    private void loadEventData() {
        // Display event details
        editEventName.setText(event.getName());
        editEventCapacity.setText(String.valueOf(event.getCapacity()));
        editEventDescription.setText(event.getDescription());

        // Load the poster image
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.sample_poster) // Show placeholder while loading
                    .into(editEventPosterView);
        } else {
            // Display the sample poster as a default image
            editEventPosterView.setImageResource(R.drawable.sample_poster);
        }
    }


    /**
     * Select a new image for the event poster.
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
     * Save the updated event details.
     */
    private void saveEventEdits() {
        if (newPosterUri != null) {
            uploadNewPoster();
        } else {
        }
    }

    /**
     * Upload a new poster image to Firebase Storage.
     */
    private void uploadNewPoster() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String fileName = "poster_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference posterRef = storageReference.child(fileName);

        posterRef.putFile(newPosterUri)
                .addOnSuccessListener(taskSnapshot -> posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Update event with the new poster URL
                    event.setPosterUrl(uri.toString());
                    progressDialog.dismiss();
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Save the updated event details to the database.
     */


    /**
     * Close the fragment and return to the Created Activities view.
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
