package com.example.rocket_launch;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

/**
 * class to help with firestore database
 */
public class EventsDB {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    /**
     * constructor
     */
    public EventsDB() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events_dev");
    }

    /**
     * add a created event to database
     * @param event
     *  event to add
     * @param androidId
     *  androidId of who wants to add
     * @param onCompleteListener
     *  listener for what to do on completion
     */
    public void addCreatedEvent(Event event, String androidId, OnCompleteListener<Void> onCompleteListener) {
        UsersDB usersDB = new UsersDB();
        DocumentReference newEventRef = eventsRef.document();
        event.setEventID(newEventRef.getId());
        newEventRef.set(event)
                .addOnSuccessListener(documentReference -> {
                    usersDB.addCreatedEvent(androidId, newEventRef.getId());
                    Log.d("Firebase", "Event added successfully!");
                })
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding Event", e))
                .addOnCompleteListener(onCompleteListener);
    }

    /**
     * Add user to waiting list and check max waiting list size
     * @param eventID
     *  add user to event wit heventID
     * @param userID
     *  user to add to waitlist
     */
    public void addUserToWaitingList(String eventID, String userID) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);

                if (event != null) {
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
                }
            } else {
                Log.w("Firebase", "Event document does not exist");
            }
        });
    }

    /**
     * Remove user from waiting list
     * @param eventID
     *  id of event who's waitlist we want to remove from
     * @param userID
     *  id of user to remove from waitlist
     */
    public void removeUserFromWaitingList(String eventID, String userID) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.update("waitingList", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "User removed from waiting list");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error removing user", e);
                    }
                });

    }

    /**
     * Remove user from registered list
     * @param eventID
     *  id of event who's registered list we want to remove from
     * @param userID
     *  id of user to remove from registered list
     */
    public void addUserToRegisteredList(String eventID, String userID) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.update("registeredEntrants", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "User removed from registered list");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error removing user", e);
                    }
                });

    }

    /**
     * Remove user from registered list
     * @param eventID
     *  id of event who's registered list we want to remove from
     * @param userID
     *  id of user to remove from registered list
     */
    public void removeUserFromRegisteredList(String eventID, String userID) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.update("registeredEntrants", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "User removed from registered list");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error removing user", e);
                    }
                });

    }

    // TODO: update event
    /**
     * update eventDB
     */
    public void updateEventDB() {

    }

    /**
     * loads a given event with eventID id
     * @param id
     *  id of event to load
     * @param onSuccess
     *  listener for what to do on successful load
     */
    public void loadEvent(String id, OnSuccessListener<DocumentSnapshot> onSuccess) {
        eventsRef.document(id).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error loading Event", e);
                    }
                });
    }

    /**
     * gets list of user Ids from an event's waitlist
     * @param eventId
     *  id of event
     * @param onSuccess
     *  what to do on success
     * @param onFailure
     *  what to do on failure
     */
    public void getWaitlistedUserIds(String eventId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        eventsRef.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = null;
                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                    }
                    if (event != null) {
                        List<String> users = event.getWaitingList();
                        if (users != null) {
                            onSuccess.onSuccess(users);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * gets list of user Ids from an event's Invited list
     * @param eventId
     *  id of event
     * @param onSuccess
     *  what to do on success
     * @param onFailure
     *  what to do on failure
     */
    public void getInvitedUserIds(String eventId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        eventsRef.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = null;
                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                    }
                    if (event != null) {
                        List<String> users = event.getInvitedEntrants();
                        if (users != null) {
                            onSuccess.onSuccess(users);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * gets list of user Ids from an event's cancelled list
     * @param eventId
     *  id of event
     * @param onSuccess
     *  what to do on success
     * @param onFailure
     *  what to do on failure
     */
    public void getCancelledUserIds(String eventId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        eventsRef.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = null;
                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                    }
                    if (event != null) {
                        List<String> users = event.getCancelledEntrants();
                        if (users != null) {
                            onSuccess.onSuccess(users);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * gets list of user Ids from an event's final list
     * @param eventId
     *  id of event
     * @param onSuccess
     *  what to do on success
     * @param onFailure
     *  what to do on failure
     */
    public void getFinalUserIds(String eventId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        eventsRef.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = null;
                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                    }
                    if (event != null) {
                        List<String> users = event.getFinalEntrants();
                        if (users != null) {
                            onSuccess.onSuccess(users);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * get all events in a string of eventId's
     * @param eventsList
     *  list of events to get
     * @param onSuccess
     *  what to do on successful load
     * @param onFailure
     *  what to do on failed load
     */
    public void getAllEventsInList(List<String> eventsList, OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        List<Event> events = new ArrayList<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String docTitle : eventsList) {
            Task<DocumentSnapshot> task = eventsRef.document(docTitle).get();
            tasks.add(task);
        }
        Tasks.whenAllComplete(tasks).addOnSuccessListener(l -> {
           for (Task<DocumentSnapshot> task : tasks) {
               if (task.isSuccessful()) {
                   DocumentSnapshot doc = task.getResult();
                   if (doc != null && doc.exists()) {
                       Event event = doc.toObject(Event.class);
                       if (event != null) {
                           events.add(event);
                       }
                   }
               }
           }
        })
                .addOnSuccessListener(v -> {onSuccess.onSuccess(events);})
                .addOnFailureListener(onFailure);
    }

    public void sampleWaitlist(String eventId, int sampleAmount, OnSuccessListener<Void> onSuccessListener) {
        // load event
        loadEvent(eventId, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        event.sampleWaitlist(sampleAmount);
                        eventsRef.document(eventId).set(event)
                                .addOnSuccessListener(onSuccessListener)
                                .addOnFailureListener(e -> Log.w("Firebase", "Error saving Event", e));
                    }
                }
            }
        });
    }
}