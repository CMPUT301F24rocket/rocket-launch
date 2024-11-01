package com.example.rocket_launch;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
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


}
