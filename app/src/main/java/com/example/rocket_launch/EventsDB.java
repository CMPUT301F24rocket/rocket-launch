package com.example.rocket_launch;

import android.util.Log;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     * Author: Kaiden
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
     * Get the reference to the events collection.
     * @return CollectionReference to the events collection
     */
    public CollectionReference getEventsRef() {
        return eventsRef;
    }

    /**
     * Add user to waiting list and check max waiting list size
     * Author: Kaiden
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
     * Author: Kaiden
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
     * Author: Kaiden
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
                        Log.d("Firebase", "User added to registered list");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error adding user", e);
                    }
                });

    }

    /**
     * Remove user from registered list
     * Author: Kaiden
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

    /**
     * remove a user from an events invited list
     * Author: Kaiden
     * @param eventId
     *  id of event
     * @param userId
     *  id of user
     */
    public void removeUserFromInvitedList(String eventId, String userId) {
        DocumentReference eventDocRef = eventsRef.document(eventId);

        eventDocRef.update("invitedEntrants", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(l -> {
                    Log.d("Firebase", "User removed from registered list");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error removing user", e);
                });
    }

    /**
     * adds a user to a given event's cancelled list
     * Author: Kaiden
     * @param eventId
     *  event to add user to
     * @param userId
     *  user to add
     */
    public void addUserToCancelledList(String eventId, String userId) {
        DocumentReference eventDocRef = eventsRef.document(eventId);

        eventDocRef.update("cancelledEntrants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(l -> {
                    Log.d("Firebase", "User added to cancelled list");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error adding user", e);
                });
    }


    /**
     * updates a given event(eventId) with new event data store in event
     * Author: Kaiden
     * @param eventId
     *  id of event to update
     * @param event
     *  updated data to load
     * @param onSuccess
     *  callback for if firestore succeeds
     * @param onFailureListener
     *  callback for if firestore fails
     */
    public void updateEvent(String eventId, Event event, OnSuccessListener<Void> onSuccess, OnFailureListener onFailureListener) {
        eventsRef.document(eventId).set(event)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * loads a given event with eventID id
     * @param id
     *  id of event to load
     * @param onSuccess
     *  listener for what to do on successful load
     */
    public void loadEvent(String id, OnSuccessListener<Event> onSuccess) {
        eventsRef.document(id).get()
        .addOnSuccessListener(documentSnapshot -> {
            Event event = null;
            if (documentSnapshot.exists()) {
                event = documentSnapshot.toObject(Event.class);
            }
            onSuccess.onSuccess(event);
        }).addOnFailureListener(new OnFailureListener() {
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
    public void getRegisteredUserIds(String eventId, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        eventsRef.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = null;
                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                    }
                    if (event != null) {
                        List<String> users = event.getregisteredEntrants();
                        if (users != null) {
                            onSuccess.onSuccess(users);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * get all events in a string of eventId's
     * Author: Kaiden
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

    /**
     * adds entrant location to list of location data for the event
     * Author: Rachel
     * @param eventID
     *  id of event to add
     * @param entrantLocation
     *  location of entrant signing up
     */
    public void addToEntrantLocationDataList(String eventID, EntrantLocationData entrantLocation) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);

                if (event != null) {
                    eventsRef.document(eventID).update("entrantLocationDataList", FieldValue.arrayUnion(entrantLocation))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firebase", "entrant location added to entrant location data list");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firebase", "Error adding entrant location data", e);
                                }
                            });
                }
            } else {
                Log.w("Firebase", "Event document does not exist");
            }
        });
    }

    /**
     * Remove location data from the database
     * Author: Rachel
     * @param eventID
     *  id of event to remove user location form
     * @param entrantID
     *  id of user of which to remove data
     */
    public void removeFromEntrantLocationDataList(String eventID, String entrantID) {
      
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
              
                List<Map<String, Object>> locationDataList = (List<Map<String, Object>>) documentSnapshot.get("entrantLocationDataList");

                // Filter the list to remove the object with the matching entrantID
                // since only one instance of entrantID can exist in the list
                List<Map<String, Object>> updatedLocationDataList = new ArrayList<>();
                for (Map<String, Object> locationData : locationDataList) {
                    if (!locationData.get("entrantID").equals(entrantID)) {
                        updatedLocationDataList.add(locationData);
                    }
                }

                eventDocRef.update("entrantLocationDataList", updatedLocationDataList)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firebase", "Entrant location removed from entrant location data list");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Firebase", "Error removing location data", e);
                            }
                        });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firebase", "Error fetching event data: ", e);
            }
        });
    }

    /**
     * Delete an event from the database.
     * Author: Kaiden
     * @param eventId The ID of the event to delete.
     * @param onSuccess Callback for successful deletion.
     * @param onFailure Callback for failed deletion.
     */
    public void deleteEvent(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        loadEvent(eventId, event -> {
            UsersDB usersDB = new UsersDB();
            // when an event is deleted it must be deleted from the organizer's created events list
            usersDB.removeCreatedEvent(event.getEventID(), eventId);

            // remove occurrence from all users in waiting list
            for (String androidId : event.getWaitingList()) {
                usersDB.removeWaitlistedEvent(androidId, eventId);
            }

            // remove occurrence from all useres in registered (final) list
            for (String androidId : event.getregisteredEntrants()) {
                usersDB.removeRegisteredEvent(androidId, eventId);
            }

            eventsRef.document(eventId).delete()
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);
        });
    }
}