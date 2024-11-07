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
        this.db = FirebaseFirestore.getInstance();
        this.eventRef = db.collection("events");
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

    // Add user to waiting list
    public void addUserToWaitingList(String eventID, String userID) {
        eventRef.document(eventID).update("waitingList", FieldValue.arrayUnion(userID))
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

    // promote user from waiting list to participant
    public void promoteUserFromWaitingList(String eventID, String userID){
      eventRef.document(eventID).get().addOnSuccessListener(documentSnapshot -> {
          Event event = documentSnapshot.toObject(Event.class);
          String eventName = event.getName();

          eventRef.document(eventID).update(
                  "participants", FieldValue.arrayUnion(userID),
                  "waitinglist", FieldValue.arrayRemove(userID))
                  .addOnSuccessListener(aVoid -> {
                      Log.d("Firebase", "User promoted");

                      String notficationMsg = "Youve been seleected for event" + eventName;

                      UsersDB usersDB = new UsersDB();
                      usersDB.addNotificationToUser(userID, notficationMsg);
                  })
                  .addOnFailureListener(e -> {
                      Log.w("Firebase", "Error promoting user");
                  });

      });

    }


}


