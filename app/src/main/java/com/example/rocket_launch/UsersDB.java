package com.example.rocket_launch;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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
        usersRef = db.collection("user_info_dev");  // Reference the collection
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
     * gets user from database
     *
     * @param androidId
     *  id of user
     * @param onSuccess
     *  listener for onSuccess, will pass a User user where if a user is in the db user != null
     * @param onFailure
     *  listener for onFailure
     */
    public void getUser(String androidId, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        usersRef.document(androidId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = null;
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                    }
                    onSuccess.onSuccess(user);
                })
                .addOnFailureListener(onFailure);
    }

    public void updateUser(String androidId, User user, OnSuccessListener<Void> onSuccess, OnFailureListener onFailureListener) {
        usersRef.document(androidId).set(user)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * used for updating a user androidId with new contents contained in user
     * @param androidId
     *  id of user info to update
     * @param roles
     *  roles to update to
     *  user info
     */
    public void setRoles(String androidId, Roles roles) {
        usersRef.document(androidId).update("roles", roles)
                .addOnSuccessListener(unused -> Log.d("Firebase", "roles update"))
                .addOnFailureListener(e -> Log.w("Firebase", "roles update failed", e));
    }

    // notifications

    /**
     * adds a notification to database
     * @param androidID
     *  id of user to add to
     * @param notification
     *  notification data to add
     */
    public void addNotification(String androidID, Notification notification){
        usersRef.document(androidID)
                .update("notifications", FieldValue.arrayUnion(notification))
                .addOnSuccessListener(unused -> Log.d("Firebase", "Notification added successfully"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding notification", e));
    }

    /**
     * removes notification from database
     * @param androidID
     *  id of user to remove from
     * @param notification
     *  notification to remove
     */
    public void removeNotification(String androidID, Notification notification) {
        usersRef.document(androidID).update("notifications", FieldValue.arrayRemove(notification))
                .addOnSuccessListener(unused -> Log.d("Firebase", "Notification removed"))
                .addOnFailureListener(e -> Log.w("Firebase", "notfication removal failed", e));
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

    /**
     * gets list of event titles from user's created events list
     * @param androidId
     *  id of user to get events from
     */
    public void getCreatedEventIds(String androidId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        usersRef.document(androidId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = null;
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                    }
                    if (user != null) {
                        List<String> events = user.getEventsCreated();
                        if (events != null) {
                            onSuccess.onSuccess(events);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * gets list of event titles from user's created events list
     * @param androidId
     *  id of user to get events from
     */
    public void getRegisteredEventIds(String androidId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        usersRef.document(androidId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = null;
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                    }
                    if (user != null) {
                        List<String> events = user.getEventsRegistered();
                        if (events != null) {
                            onSuccess.onSuccess(events);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * gets list of event titles from user's created events list
     * @param androidId
     *  id of user to get events from
     */
    public void getWaitlistedEventIds(String androidId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        usersRef.document(androidId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = null;
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                    }
                    if (user != null) {
                        List<String> events = user.getEventsWaitlisted();
                        if (events != null) {
                            onSuccess.onSuccess(events);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * get all users in a string if androidId's
     * @param usersList
     *  list of user to get
     * @param onSuccess
     *  what to do on successful load
     * @param onFailure
     *  what to do on failed load
     */
    public void getAllUsersInList(List<String> usersList, OnSuccessListener<List<User>> onSuccess, OnFailureListener onFailure) {
        List<User> users = new ArrayList<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String docTitle : usersList) {
            Task<DocumentSnapshot> task = usersRef.document(docTitle).get();
            tasks.add(task);
        }
        Tasks.whenAllComplete(tasks).addOnSuccessListener(l -> {
                    for (Task<DocumentSnapshot> task : tasks) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                User user = doc.toObject(User.class);
                                if (user != null) {
                                    users.add(user);
                                }
                            }
                        }
                    }
                })
                .addOnSuccessListener(v -> {onSuccess.onSuccess(users);})
                .addOnFailureListener(onFailure);
    }

    public void deleteFacility(String androidId, OnSuccessListener<Void> onSuccessListener) {
        getUser(androidId, user -> {
            // remove organizer role
            user.getRoles().setOrganizer(false);

            // remove all created events
            EventsDB eventsDB = new EventsDB();
            for (String event : user.getEventsCreated()) {
                eventsDB.deleteEvent(event,
                        l -> Log.d("delete facility", "created event deleted successfully"),
                        e -> Log.d("delete facility", "created event deletion error", e));
            }

            // if both roles are false, remove from database
            if (!user.getRoles().isEntrant()) {
                usersRef.document(androidId).delete()
                        .addOnSuccessListener(onSuccessListener)
                        .addOnFailureListener(e -> Log.d("delete facility", "full deletion error", e));
            } else {
                // remove facility name and address
                user.setUserFacility("");
                user.setUserFacilityAddress("");
                user.setEventsCreated(new ArrayList<String>());
                updateUser(androidId, user, onSuccessListener, e -> {
                    Log.d("delete facility", "error updating user");
                });
            }
        }, e -> Log.e("delete facility", "error retrieving facility", e));
    }

    public void deleteUser(String androidId, OnSuccessListener<Void> onSuccessListener) {
        getUser(androidId, user -> {
            // remove user role
            user.getRoles().setEntrant(false);
            EventsDB eventsDB = new EventsDB();

            // remove from waitlisted events
            for (String eventId : user.getEventsWaitlisted()) {
                eventsDB.removeUserFromWaitingList(eventId, androidId);
                removeWaitlistedEvent(androidId, eventId);
            }

            // remove from registered events
            for (String eventId : user.getEventsRegistered()) {
                eventsDB.removeUserFromRegisteredList(eventId, androidId);
                removeRegisteredEvent(androidId, eventId);
            }

            // if both roles are false, remove from database
            if (!user.getRoles().isOrganizer()) {
                usersRef.document(androidId).delete()
                        .addOnSuccessListener(l -> Log.d("delete user", "full deletion successful"))
                        .addOnFailureListener(e -> Log.d("delete user", "full deletion error", e));
            } else {
                user.setUserName("");
                user.setUserEmail("");
                user.setUserPhoneNumber("");
                updateUser(androidId, user, onSuccessListener, e -> {
                    Log.d("delete user", "error updating user", e);
                });
            }
        }, e -> Log.e("delete user", "error retrieving user", e));
    }

    public void loadProfilePhoto(String androidId) {
        // TODO
    }

    public void addProfilePhoto(String androidId) {
        // TODO
    }

    public void removeProfilePhoto(String androidId) {
        // TODO
    }

    public void editProfilePhoto(String androidId) {
        // TODO
    }
}
