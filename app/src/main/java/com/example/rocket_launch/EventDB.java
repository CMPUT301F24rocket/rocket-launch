package com.example.rocket_launch;

import static androidx.core.content.ContentProviderCompat.requireContext;

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
    private CollectionReference organizerEventRef;
    private String androidId;


    public EventDB(){
        this.db = FirebaseFirestore.getInstance();
        this.eventRef = db.collection("events");

    public EventDB(Context context){
        db = FirebaseFirestore.getInstance();
        organizerEventRef = db.collection("organizer_events");
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    

    public EventDB() {

    }


    public void addEvent(String eventID, Event event){
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", event.getName());
        eventMap.put("description", event.getDescription());
        eventMap.put("capacity", event.getCapacity());
        //eventMap.put("startTime", event.getStartTime());
        //eventMap.put("endTime", event.getEndTime());
        eventMap.put("participants", event.getParticipants());
        eventMap.put("waitingList", new ArrayList<>());
        eventMap.put("waitlist_size_limit", event.getMaxWaitingListSize());
        eventMap.put("geolocation_required", event.getGeolocationRequired());

        DocumentReference organizerDocRef = organizerEventRef.document(androidId);
        organizerDocRef.collection("events").document(eventID).set(eventMap)
                .addOnSuccessListener(unused -> Log.d("Firebase", "Event added successfully!"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding Event", e));


        /*
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
                }); */
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
        DocumentReference eventDocRef = organizerEventRef.document(androidId).collection("events").document(eventID);

        eventDocRef.update("waitingList", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "User removes from waiting list");}})

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error removing user", e);}});

    }

    //TODO: Update Event

    //get all events for a specific organizer via androidID
    public void getAllOrganizerEvents(OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure){
        CollectionReference eventsRef = organizerEventRef.document(androidId).collection("events");

        eventsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                        Event event = doc.toObject(Event.class);
                        if (event != null){
                            events.add(event);
                        }
                    }
                    onSuccess.onSuccess(events);
                })
                .addOnFailureListener(onFailure);
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


