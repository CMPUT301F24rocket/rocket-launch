package com.example.rocket_launch;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * handles all database queries related to QR Codes
 */
public class QRCodesDB {
    private FirebaseFirestore db;
    private CollectionReference QRRef;

    public QRCodesDB() {
        db = FirebaseFirestore.getInstance();
        QRRef = db.collection("QRCodes");  // Reference the collection
    }

    public void loadAll() {

    }
}
