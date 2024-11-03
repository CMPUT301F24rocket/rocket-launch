package com.example.rocket_launch;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UsersDB {
    private FirebaseFirestore db;
    private CollectionReference userRef;

    UsersDB() {
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("user_info");  // Reference the collection
    }

    public void addUser(String androidId, User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("android_id", androidId);
        userMap.put("name", user.getUserName());
        userMap.put("email", user.getUserEmail());
        userMap.put("phone", user.getUserPhoneNumber());
        userMap.put("profile_picture_url", user.getProfilePhoto());
        userMap.put("facility", user.getUserFacility());
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
        userMap.put("name", user.getUserName());
        userMap.put("email", user.getUserEmail());
        userMap.put("phone", user.getUserPhoneNumber());
        userMap.put("profile_picture_url", user.getProfilePhoto());
        userMap.put("facility", user.getUserFacility());
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

}
