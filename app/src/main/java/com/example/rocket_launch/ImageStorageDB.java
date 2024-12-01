package com.example.rocket_launch;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageDB {
    public static final FirebaseStorage storage
            = FirebaseStorage.getInstance("gs://rocket-launch-21699.firebasestorage.app");

    // add
    public static void uploadImage(Uri imageUri, String path) {
        StorageReference imageRef = storage.getReference().child(path);
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(l -> {
            Log.d("Image Storage", "upload successful");
        }).addOnFailureListener(e -> {
            Log.d("Image Storage", "upload successful", e);
        });
    }

    // remove
    public static void  removeImage(String path) {
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.delete().addOnSuccessListener(l -> {
            Log.d("Image Storage", "removal successful");
        }).addOnFailureListener(e -> {
            Log.d("Image Storage", "removal failure", e);
        });
    }

    // get

    // get all
}
