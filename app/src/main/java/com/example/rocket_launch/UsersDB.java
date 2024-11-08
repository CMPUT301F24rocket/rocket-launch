package com.example.rocket_launch;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.mutation.ArrayTransformOperation;

import java.util.HashMap;
import java.util.Map;

public class UsersDB {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    public UsersDB() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("user_info");  // Reference the collection
    }

    public void addUser(String androidId, User user) {
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("android_id", androidId);
//        userMap.put("userName", user.getUserName());
//        userMap.put("userEmail", user.getUserEmail());
//        userMap.put("userPhoneNumber", user.getUserPhoneNumber());
//        userMap.put("profilePhoto", user.getProfilePhoto());
//        userMap.put("userFacility", user.getUserFacility());
//        userMap.put("roles", user.getRoles());

        usersRef.document(androidId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firebase", "User added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firebase", "Error adding user", e);
                    }
                });
    }

    public void getUser(String androidId, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        usersRef.document(androidId).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateUser(String androidId, User user){
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("android_id", androidId);
        userMap.put("userName", user.getUserName());
        userMap.put("userEmail", user.getUserEmail());
        userMap.put("userPhoneNumber", user.getUserPhoneNumber());
        userMap.put("profilePhoto", user.getProfilePhoto());
        userMap.put("userFacility", user.getUserFacility());
        userMap.put("roles", user.getRoles());

        usersRef.document(androidId).update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firebase", "User added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firebase", "Error updating user", e);
                    }
                });
    }

    public void addNotificationToUser(String androidID, String notification){
        usersRef.document(androidID).update("notifications", FieldValue.arrayUnion(notification))
                .addOnSuccessListener(unused -> Log.d("Firebase", "Notification added"))
                .addOnFailureListener(e -> Log.w("Firebase", "notfication added failed", e));

    }

    public void addJoinedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsJoined", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "joined event added to user"))
                .addOnFailureListener(e -> Log.w("Firebase", "joined event add failed", e));
    }
    public void removeJoinedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsJoined", FieldValue.arrayRemove(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "joined event removed from user"))
                .addOnFailureListener(e -> Log.w("Firebase", "joined event removed failed", e));
    }

    public void addCreatedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsCreated", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "created event added to user"))
                .addOnFailureListener(e -> Log.w("Firebase", "created event add failed", e));
    }
    public void removeCreatedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsCreated", FieldValue.arrayRemove(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "created event removed from user"))
                .addOnFailureListener(e -> Log.w("Firebase", "created event removed failed", e));
    }



    public CollectionReference getUsersRef() {
        return usersRef;
    }

}
