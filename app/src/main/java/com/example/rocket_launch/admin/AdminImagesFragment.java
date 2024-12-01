package com.example.rocket_launch.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminImagesFragment extends Fragment {
    private RecyclerView imagesRecyclerView;
    private AdminImagesAdapter adapter;
    private UsersDB usersDB;
    private List<User> usersWithImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_images_fragment, container, false);

        imagesRecyclerView = view.findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(imagesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        imagesRecyclerView.addItemDecoration(divider);

        usersWithImages = new ArrayList<>();
        adapter = new AdminImagesAdapter(usersWithImages, getContext());
        imagesRecyclerView.setAdapter(adapter);

        usersDB = new UsersDB();
        listenForImageChanges();

        adapter.setOnImageDeleteListener(user -> showDeleteConfirmationDialog(user));

        return view;
    }

    private void listenForImageChanges() {
        usersDB.getUsersRef().addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {
                // Clear the existing list and repopulate it based on the snapshot
                usersWithImages.clear();

                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null && user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                        usersWithImages.add(user);
                    }
                }

                // Notify the adapter of the updated data
                adapter.updateData(usersWithImages);
            }
        });
    }

    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Image?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteImage(user))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteImage(User user) {
        usersDB.getUsersRef().document(user.getAndroidId()).update("profilePhotoPath", null)
                .addOnSuccessListener(aVoid -> {
                    // Real-time updates will handle UI refresh
                })
                .addOnFailureListener(e -> {
                    // Handle error if deletion fails
                });
    }
}
