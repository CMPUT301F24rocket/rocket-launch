package com.example.rocket_launch;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UsersDB {
    private FirebaseFirestore db;

    public CollectionReference getUserRef() {
        return userRef;
    }

    private CollectionReference userRef;

    public UsersDB() {
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("user_info");  // Reference the collection
    }

    public void addUser(String androidId, User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("android_id", androidId);
        userMap.put("userName", user.getUserName());
        userMap.put("userEmail", user.getUserEmail());
        userMap.put("userPhoneNumber", user.getUserPhoneNumber());
        userMap.put("profilePhoto", user.getProfilePhoto());
        userMap.put("userFacility", user.getUserFacility());
        userMap.put("roles", user.getRoles());

        userRef.document(androidId).set(userMap)
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
        userRef.document(androidId).get()
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

        userRef.document(androidId).update(userMap)
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
        userRef.document(androidID).update("notifications", FieldValue.arrayUnion(notification))
                .addOnSuccessListener(unused -> Log.d("Firebase", "Notification added"))
                .addOnFailureListener(e -> Log.w("Firebase", "notfication added failed", e));

    }

}
