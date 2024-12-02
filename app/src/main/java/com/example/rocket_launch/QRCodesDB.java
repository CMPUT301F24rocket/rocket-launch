package com.example.rocket_launch;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * handles all database queries related to QR Codes
 * Author: kaiden
 */
public class QRCodesDB {
    private FirebaseFirestore db;
    private CollectionReference qRRef;

    private EventsDB eventsDB;

    public QRCodesDB() {
        db = FirebaseFirestore.getInstance();
        qRRef = db.collection("QRCodes");  // Reference the collection

        eventsDB = new EventsDB(); // load eventsDB so we can update event's qr code
    }

    /**
     * loads an event given a QR code
     * Author: kaiden
     * @param code
     *  QR code in which to load
     * @param success
     *  callback for if event exists and firestore retreives it
     * @param failure
     *  callback for any failure
     */
    public void loadEventId(String code, OnSuccessListener<String> success, OnFailureListener failure) {
        qRRef.document(code).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventId = (String) documentSnapshot.get("eventId");
                        success.onSuccess(eventId);
                    } else {
                        failure.onFailure(new Exception("Invalid id"));
                    }
                })
                .addOnFailureListener(e -> Log.e("error loading from database", "error", e));
    }

    /**
     * Loads code from database
     * @param code Code
     * @param success OnSuccess listener
     */
    public void loadCode(String code, OnSuccessListener<Event> success) {
        // try to load from database
        qRRef.document(code).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // if we successfully find teh code in the database
                    if (documentSnapshot.exists()) {
                        String eventId = (String) documentSnapshot.get("eventId");

                        // try to load the corresponding event
                        eventsDB.loadEvent(eventId, success); // pass on success to events db
                    }
                })
                .addOnFailureListener(e -> Log.e("error loading from database", "error", e));
    }

    /**
     * loads all qr codes and adds them to a List of Strings supplied in onSuccessListener
     * Author: kaiden
     * @param onSuccessListener
     *  callback used to pass data from firestore load
     */
    public void loadAll(OnSuccessListener<List<String>> onSuccessListener) {
        qRRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> codes = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                codes.add(document.getId());
            }
            onSuccessListener.onSuccess(codes);
        }).addOnFailureListener(e -> {
            Log.e("loadAll", "Failed to load QR Codes", e);
        });
    }

    /**
     * adds a new QR code to QRCode database
     * Author: kaiden
     * @param eventId
     *  id of event to add
     * @param onSuccess
     *  callback for success, is passed new QR code's ID for storage in an event
     * @param onFailure
     *  callback if a failure occurs
     */
    public void addCode(String eventId, OnSuccessListener<String> onSuccess, OnFailureListener onFailure) {
        eventsDB.loadEvent(eventId, event -> {
            if (event != null) {
                DocumentReference newQRRef = qRRef.document();
                event.setQRCode(newQRRef.getId()); // add code ID to event eventId
                eventsDB.updateEvent(eventId, event, l -> {}, f -> {});
                HashMap<String, String> data = new HashMap<>();
                data.put("eventId", eventId);
                newQRRef.set(data)
                        .addOnSuccessListener(l -> {
                            onSuccess.onSuccess(newQRRef.getId());
                        })
                        .addOnFailureListener(onFailure);
            } else {
                Log.w("QRCode", "error adding code: event is is null");
            }
        });
    }

    public void removeCode(String code, String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Log.d("QRCodesDB", "Attempting to remove QR code: " + code + " linked to event ID: " + eventId);
        eventsDB.loadEvent(eventId, event -> {
            if (event != null) {
                Log.d("QRCodesDB", "Event loaded successfully for eventId: " + eventId);
                qRRef.document(code).delete()
                        .addOnSuccessListener(unused -> {
                            event.setQRCode(""); // Optional: clear QR Code link
                            onSuccess.onSuccess(null);
                            Log.d("QRCodesDB", "Successfully deleted QR code: " + code);
                        })
                        .addOnFailureListener(e -> {
                            onFailure.onFailure(e);
                            Log.e("QRCodesDB", "Failed to delete QR code: " + code, e);
                        });
            } else {
                Log.e("QRCodesDB", "Failed to load event for eventId: " + eventId);
                onFailure.onFailure(new Exception("Event not found for eventId: " + eventId));
            }
        });
    }


    /**
     * removes a QR code from firestore database
     * Author: kaiden
     * @param code
     *  QR code in which to remove
     * @param onSuccess
     *  callback for a successful removal
     */
    private void removeCodeFromDatabase(String code, OnSuccessListener<Void> onSuccess) {
        qRRef.document(code).delete() // delete qr code in QR database
                .addOnSuccessListener(l -> {
                    onSuccess.onSuccess(null);
                })
                .addOnFailureListener(e -> Log.e("error removing from database", "error", e));
    }


    /**
     * regenerates the QR code and updates the event, eventsDB and qr codes database
     * Author: kaiden
     * @param code
     *  QR code string of which we wish to update
     * @param eventId
     *  should not be required, refactoring ahead :(
     * @param onSuccess
     *  passes the on string as a parameter so we can redisplay proper data
     * @param onFailure
     *  is called if we encounter a failure
     */
    public void reGenerateCode(String code, String eventId, OnSuccessListener<String> onSuccess, OnFailureListener onFailure) {
        eventsDB.loadEvent(eventId, event -> {
            if (event != null) {
                DocumentReference newQRRef = qRRef.document();
                event.setQRCode(newQRRef.getId()); // add code ID to event eventId
                eventsDB.updateEvent(eventId, event, l -> {}, l -> {});
                HashMap<String, String> data = new HashMap<>();
                data.put("eventId", eventId);
                newQRRef.set(data)
                        .addOnSuccessListener(l -> {
                            // if we successfully add a new one, remove the old (code)
                            removeCodeFromDatabase(code, j -> {
                                onSuccess.onSuccess(event.getQRCode());
                            });
                        })
                        .addOnFailureListener(onFailure);
            }
        });
    }
}