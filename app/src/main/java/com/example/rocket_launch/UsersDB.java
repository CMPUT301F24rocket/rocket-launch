package com.example.rocket_launch;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * databse class for interfacing with database
 */
public class UsersDB {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    /**
     * constructor
     */
    public UsersDB() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("user_info");  // Reference the collection
    }

    /**
     * add a g iven user to database
     * @param androidId
     *  unique id of user
     * @param user
     *  user information to add
     */
    public void addUser(String androidId, User user) {
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

    /**
     * gets user from databse
     * @param androidId
     *  id of user
     * @param onSuccess
     *  listener for onSuccess
     * @param onFailure
     *  listener for onFailure
     */
    public void getUser(String androidId, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        usersRef.document(androidId).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // notifications

    /**
     * adds a notification to database
     * @param androidID
     *  id of user to add to
     * @param notification
     *  notification data to add
     */
    public void addNotification(String androidID, String notification){
        usersRef.document(androidID).update("notifications", FieldValue.arrayUnion(notification))
                .addOnSuccessListener(unused -> Log.d("Firebase", "Notification added"))
                .addOnFailureListener(e -> Log.w("Firebase", "notfication added failed", e));
    }

    /**
     * removes notification from database
     * @param androidID
     *  id of user to remove from
     * @param notification
     *  notification to remove
     */
    public void removeNotification(String androidID, String notification) {
        usersRef.document(androidID).update("notifications", FieldValue.arrayRemove(notification))
                .addOnSuccessListener(unused -> Log.d("Firebase", "Notification added"))
                .addOnFailureListener(e -> Log.w("Firebase", "notfication added failed", e));
    }

    // waitlisted events

    /**
     * add event to user waitlist
     * @param androidId
     *  id of user
     * @param eventID
     *  id of event
     */
    public void addWaitlistedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsWaitlisted", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "joined event added to user"))
                .addOnFailureListener(e -> Log.w("Firebase", "joined event add failed", e));
    }

    /**
     * remove event from user waitlist
     * @param androidId
     *  id of user
     * @param eventID
     *  id of event
     */
    public void removeWaitlistedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsWaitlisted", FieldValue.arrayRemove(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "joined event removed from user"))
                .addOnFailureListener(e -> Log.w("Firebase", "joined event removed failed", e));
    }

    // registered events

    /**
     * add event to user's registered events
     * @param androidId
     *  id of user
     * @param eventID
     *  id of event
     */
    public void addRegisteredEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsRegistered", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "joined event added to user"))
                .addOnFailureListener(e -> Log.w("Firebase", "joined event add failed", e));
    }

    /**
     * remove event from user registered event list
     * @param androidId
     *  id of user
     * @param eventID
     *  id of event
     */
    public void removeRegisteredEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsRegistered", FieldValue.arrayRemove(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "joined event removed from user"))
                .addOnFailureListener(e -> Log.w("Firebase", "joined event removed failed", e));
    }


    // created events

    /**
     * add event to user's created events
     * @param androidId
     *  id of user
     * @param eventID
     *  id of event
     */
    public void addCreatedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsCreated", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "created event added to user"))
                .addOnFailureListener(e -> Log.w("Firebase", "created event add failed", e));
    }

    /**
     * remove event from user's created evetns
     * @param androidId
     *  id of user
     * @param eventID
     *  id of event
     */
    public void removeCreatedEvent(String androidId, String eventID) {
        usersRef.document(androidId)
                .update("eventsCreated", FieldValue.arrayRemove(eventID))
                .addOnSuccessListener(unused -> Log.d("Firebase", "created event removed from user"))
                .addOnFailureListener(e -> Log.w("Firebase", "created event removed failed", e));
    }


    /**
     * gets user collection reference
     * @return
     *  returns user collection reference
     */
    public CollectionReference getUsersRef() {
        return usersRef;
    }

}
