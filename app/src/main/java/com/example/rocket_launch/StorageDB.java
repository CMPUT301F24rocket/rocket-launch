package com.example.rocket_launch;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageDB {
    private StorageReference profilesRef;
    private StorageReference eventsRef;

    public StorageDB() {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://rocket-launch-21699.firebasestorage.app");
        profilesRef = storage.getReference().child("profile_pictures");
        eventsRef = storage.getReference().child("event_pictures");
    }

    // profile photos

    public void getProfilePicture(String filename) {

    }

    public void addProfilePicture(String filename) {

    }

    public void removeProfilePicture(String filename) {

    }

    // event posters

    public void getEventPoster(String filename) {

    }

    public void addEventPoster(String filename) {

    }

    public void removeEventPoster(String filename) {

    }
}
