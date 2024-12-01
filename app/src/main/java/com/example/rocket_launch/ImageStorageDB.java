package com.example.rocket_launch;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * helper class for dealing with Firestore Storage
 * Author: Kaiden
 */
public class ImageStorageDB {
    public static final FirebaseStorage storage
            = FirebaseStorage.getInstance("gs://rocket-launch-21699.firebasestorage.app");


    /**
     * uploads an image in imageUri to path in Storage
     * @param imageUri
     *  URI of image to be uploaded
     * @param path
     *  path to upload image to
     */
    public static void uploadImage(Uri imageUri, String path,
                                   OnSuccessListener<String> onSuccessListener,
                                   OnFailureListener onFailureListener) {
        StorageReference imageRef = storage.getReference().child(path);
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                onSuccessListener.onSuccess(uri.toString()); // Pass the download URL as a String
            }).addOnFailureListener(onFailureListener);
        }).addOnFailureListener(onFailureListener);
    }



    /**
     * removes an image at the given path
     * @param path
     *  path of image to remove (includes filename)
     */
    public static void removeImage(String path) {
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.delete().addOnSuccessListener(l -> {
            Log.d("Image Storage", "removal successful");
        }).addOnFailureListener(e -> {
            Log.d("Image Storage", "removal failure", e);
        });
    }

    // get
    public static void getImage(String path, OnSuccessListener<String> onSuccessListener) {

    }

    // get all
    public static void getAllImages(String path, OnSuccessListener<String> onSuccessListener) {

    }
}
