package com.example.rocket_launch;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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

    public void loadCode(String code, OnSuccessListener<Event> success) {
        // try to load from database
        qRRef.document(code).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // if we successfully find teh code in the database
                    String eventId = (String) documentSnapshot.get("eventId");

                    // try to load the corresponding event
                    eventsDB.loadEvent(eventId, success); // pass on success to events db
                })
                .addOnFailureListener(e -> Log.e("error removing from database", "error", e));
    }

    public void loadAll() {
        // TODO - implement for admin needs
    }

    public void addCode(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        eventsDB.loadEvent(eventId, event -> {
            if (event != null) {
                DocumentReference newQRRef = qRRef.document();
                event.setQRCode(newQRRef.getId()); // add code ID to event eventId
                HashMap<String, String> data = new HashMap<>();
                data.put("eventId", eventId);
                newQRRef.set(data)
                        .addOnSuccessListener(l -> {
                            onSuccess.onSuccess(null);
                        })
                        .addOnFailureListener(onFailure);
            }
        });
    }

    public void removeCode(String code, String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        eventsDB.loadEvent(eventId, event -> {
            if (event != null) {
                qRRef.document(code).delete() // delete qr code in QR database
                        .addOnSuccessListener(l -> {
                            event.setQRCode(""); // remove code to event eventId
                            onSuccess.onSuccess(null);
                        })
                        .addOnFailureListener(onFailure);
            }
        });
    }

    private void removeCodeFromDatabase(String code, OnSuccessListener<Void> onSuccess) {
        qRRef.document(code).delete() // delete qr code in QR database
                .addOnSuccessListener(l -> {
                    onSuccess.onSuccess(null);
                })
                .addOnFailureListener(e -> Log.e("error removing from database", "error", e));
    }

    public void updateCode(String code, String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        eventsDB.loadEvent(eventId, event -> {
            if (event != null) {
                DocumentReference newQRRef = qRRef.document();
                event.setQRCode(newQRRef.getId()); // add code ID to event eventId
                HashMap<String, String> data = new HashMap<>();
                data.put("eventId", eventId);
                newQRRef.set(data)
                        .addOnSuccessListener(l -> {
                            // if we successfully add a new one, remove the old (code)
                            removeCodeFromDatabase(code, j -> {
                                onSuccess.onSuccess(null);
                            });
                        })
                        .addOnFailureListener(onFailure);
            }
        });
    }
}
