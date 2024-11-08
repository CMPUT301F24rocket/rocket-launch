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
import java.util.List;

public class EventsDB {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    public EventsDB() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
    }


    public void addCreatedEvent(Event event, String androidId) {
        UsersDB usersDB = new UsersDB();
        DocumentReference newEventRef = eventsRef.document();
        newEventRef.set(event)
                .addOnSuccessListener(documentReference -> {
                    usersDB.addCreatedEvent(androidId, newEventRef.getId());
                    Log.d("Firebase", "Event added successfully!");
                })
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding Event", e));
    }

    // Add user to waiting list and check max waiting list size
    public void addUserToWaitingList(String eventID, String userID) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);

                if (event != null) {
                    int currentSize = event.getWaitingList().size();

                    if (currentSize < event.getMaxWaitlistSize()) {
                        eventsRef.document(eventID).update("waitingList", FieldValue.arrayUnion(userID))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Firebase", "User added to waiting list");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Firebase", "Error adding user", e);
                                    }
                                });
                    } else {
                        Log.d("Firebase", "Waiting list is full");
                    }
                }
            } else {
                Log.w("Firebase", "Event document does not exist");
            }
        });
    }

    // Remove user from waiting list
    public void removeUserFromWaitingList(String eventID, String userID) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.update("waitingList", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "User removes from waiting list");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error removing user", e);
                    }
                });

    }

    //TODO: Update Event
    public void updateEventDB() {

    }

    public void loadEvent(String id, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        eventsRef.document(id).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    //get all events for a specific organizer via androidID
    public void getAllEventsInList(List<String> eventsList, OnSuccessListener<Event> onSuccess, OnFailureListener onFailure) {
        for (String docTitle : eventsList) {
            eventsRef.document(docTitle).get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null && doc.exists()) {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                onSuccess.onSuccess(event);
                            }
                        }
                    });
        }
    }
}


