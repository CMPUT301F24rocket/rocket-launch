package com.example.rocket_launch;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDB {

    private FirebaseFirestore db;
    private CollectionReference eventRef;
    private CollectionReference organizerEventRef;
    private String androidId;

    // Constructor without context
    public EventDB() {
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");
        organizerEventRef = db.collection("organizer_events");
    }

    // Constructor with context to get androidId
    public EventDB(Context context) {
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");
        organizerEventRef = db.collection("organizer_events");
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Add Event method
    public void addEvent(String eventID, Event event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", event.getName());
        eventMap.put("description", event.getDescription());
        eventMap.put("capacity", event.getCapacity());
        // eventMap.put("startTime", event.getStartTime());
        // eventMap.put("endTime", event.getEndTime());
        eventMap.put("participants", event.getParticipants());
        eventMap.put("waitingList", new ArrayList<>());
        eventMap.put("waitlist_size_limit", event.getMaxWaitingListSize());
        eventMap.put("geolocation_required", event.getGeolocationRequired());

        // Ensure androidId is initialized
        if (androidId == null) {
            Log.w("EventDB", "androidId is null. Cannot add event to organizer_events.");
            return;
        }

        DocumentReference organizerDocRef = organizerEventRef.document(androidId);
        organizerDocRef.collection("events").document(eventID).set(eventMap)
                .addOnSuccessListener(unused -> Log.d("Firebase", "Event added successfully!"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding Event", e));
    }

    // Add user to waiting list
    public void addUserToWaitingList(String eventID, String userID) {
        eventRef.document(eventID).update("waitingList", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User added to waiting list"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding user to waiting list", e));
    }

    // Remove user from waiting list
    public void removeUserFromWaitingList(String eventID, String userID) {
        // Ensure androidId is initialized
        if (androidId == null) {
            Log.w("EventDB", "androidId is null. Cannot remove user from waiting list.");
            return;
        }

        DocumentReference eventDocRef = organizerEventRef.document(androidId).collection("events").document(eventID);

        eventDocRef.update("waitingList", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User removed from waiting list"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error removing user", e));
    }

    // Get all events for a specific organizer via androidId
    public void getAllOrganizerEvents(OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        // Ensure androidId is initialized
        if (androidId == null) {
            Log.w("EventDB", "androidId is null. Cannot get organizer events.");
            return;
        }

        CollectionReference eventsRef = organizerEventRef.document(androidId).collection("events");

        eventsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            events.add(event);
                        }
                    }
                    onSuccess.onSuccess(events);
                })
                .addOnFailureListener(onFailure);
    }

    // Promote user from waiting list to participant
    public void promoteUserFromWaitingList(String eventID, String userID) {
        eventRef.document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);
            if (event != null) {
                String eventName = event.getName();

                eventRef.document(eventID).update(
                                "participants", FieldValue.arrayUnion(userID),
                                "waitingList", FieldValue.arrayRemove(userID))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firebase", "User promoted");

                            String notificationMsg = "You've been selected for event " + eventName;

                            UsersDB usersDB = new UsersDB();
                            usersDB.addNotificationToUser(userID, notificationMsg);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firebase", "Error promoting user", e);
                        });
            } else {
                Log.w("Firebase", "Event not found");
            }
        }).addOnFailureListener(e -> {
            Log.w("Firebase", "Error fetching event", e);
        });
    }

    // Additional methods...
}



