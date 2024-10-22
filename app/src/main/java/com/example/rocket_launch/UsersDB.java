package com.example.rocket_launch;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


/*
users stored in users collection
each user will be obtained through device id (the document id)
we keep it in a class so we can reference the database and change values whenever
*/
public class UsersDB {
     private FirebaseFirestore db;
     private CollectionReference user_ref;

    UsersDB() {
        // uncomment when we actually have a database
        // db = FirebaseFirestore.getInstance(); // load database on initialization
        // user_ref = db.collection("users"); // from collection of users load user


    }

    public User getUser() {
        // return user when implemented
        // null for testing
        return null;
    }

    public void addUser(User user) {

    }

    public void deleteUser() {

    }
}
