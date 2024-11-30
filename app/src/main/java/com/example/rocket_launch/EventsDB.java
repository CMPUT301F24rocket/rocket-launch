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
     * update eventDB
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

    //Remove location data from the database
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


    // Add a notification to an event
    // Updated addNotificationToEvent Method
    public void addNotificationToEvent(String eventID, Notification notification) {
        // Convert Notification object to a Map
        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("id", notification.getId());
        notificationMap.put("title", notification.getTitle());
        notificationMap.put("message3", notification.getMessage());
       // notificationMap.put("timestamp", notification.getTimestamp());

        // Update Firebase with the notification map
        eventsRef.document(eventID)
                .update("notifications", FieldValue.arrayUnion(notificationMap))
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Notification added to event"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding notification", e));
    }


    // Remove a notification from an event
    public void removeNotificationFromEvent(String eventID, Notification notification) {
        eventsRef.document(eventID)
                .update("notifications", FieldValue.arrayRemove(notification))
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Notification removed from event"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error removing notification", e));
    }

    // Retrieve notifications for an event
    public void getNotificationsForEvent(String eventID, OnSuccessListener<List<Notification>> onSuccess) {
        eventsRef.document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> notificationMaps = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        List<Notification> notifications = new ArrayList<>();

                        if (notificationMaps != null) {
                            for (Map<String, Object> map : notificationMaps) {
                                String id = (String) map.get("id");
                                String title = (String) map.get("title");
                                String message = (String) map.get("message");
                                Notification notification = new Notification(id, title, message);
                                notifications.add(notification);
                            }
                        }

                        onSuccess.onSuccess(notifications);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firebase", "Error fetching notifications", e));
    }
}