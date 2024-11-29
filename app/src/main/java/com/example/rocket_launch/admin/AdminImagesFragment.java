package com.example.rocket_launch.admin;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminImagesFragment extends Fragment {
    private RecyclerView imagesRecyclerView;
    private AdminImagesAdapter adapter;
    private UsersDB usersDB;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_images_fragment, container, false);

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        imagesRecyclerView = view.findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(imagesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        imagesRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new AdminImagesAdapter(new ArrayList<>(), getContext());
        imagesRecyclerView.setAdapter(adapter);

        usersDB = new UsersDB();
        loadImages();

        return view;
    }

    private void loadImages() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            List<User> usersWithImages = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                User user = doc.toObject(User.class);
                // Only include users with valid profile photo URLs
                if (user != null && user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
                    usersWithImages.add(user);
                }
            }
            adapter.updateData(usersWithImages); // Update the RecyclerView
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Failed to load users: " + e.getMessage());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImages(); // Reload to reflect updates
    }
}