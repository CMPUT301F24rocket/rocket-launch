package com.example.rocket_launch;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
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

    public EventDB(){
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");
    }


    public void addEvent(String eventID, Event event){
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", event.getName());
        eventMap.put("description", event.getDescription());
        eventMap.put("startTime", event.getStartTime());
        eventMap.put("endTime", event.getEndTime());
        eventMap.put("participants", event.getParticipants());
        eventMap.put("waitingList", new ArrayList<>());
        eventMap.put("maxWaitingList", event.getMaxWaitingListSize());

        eventRef.document(eventID).set(eventMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firebase", "Event added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firebase", "Error adding event", e);
                    }
                });
    }

    // Add user to waiting list and check max waiting list size
    public void addUserToWaitingList(String eventID, String userID) {
        eventRef.document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Event event = documentSnapshot.toObject(Event.class);

                    if (event != null) {
                        int currentSize = event.getWaitingList().size();

                        if (currentSize < event.getMaxWaitingListSize()) {
                            eventRef.document(eventID).update("waitingList", FieldValue.arrayUnion(userID))
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
            }
        });
    }

    // Remove user from waiting list
    public void removeUserFromWaitingList(String eventID, String userID) {
        eventRef.document(eventID).update("waitingList", FieldValue.arrayRemove(userID))
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
}


