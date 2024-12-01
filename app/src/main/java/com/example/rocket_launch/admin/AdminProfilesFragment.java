package com.example.rocket_launch.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying the profiles tab in the admin view.
 * This shows a list of user profiles to the admin using a RecyclerView.
 * Author: Pouyan
 */
public class AdminProfilesFragment extends Fragment {
    private RecyclerView profilesRecyclerView;
    private AdminProfilesAdapter adapter;
    private UsersDB usersDB;

    /**
     * Called to initialize and inflate the fragment's UI.
     *
     * @param inflater  The LayoutInflater object that can be used to inflate views.
     * @param container The parent view that this fragment's UI should attach to.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @return The root view of the fragment's layout.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the admin profile fragment
        View view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        // Set up the RecyclerView for displaying profiles
        profilesRecyclerView = view.findViewById(R.id.profiles_recycler_view);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add dividers between RecyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(profilesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        profilesRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set up the adapter with an empty list initially
        adapter = new AdminProfilesAdapter(new ArrayList<>());
        profilesRecyclerView.setAdapter(adapter);

        // Initialize the database handler for fetching user data
        usersDB = new UsersDB();
        loadProfiles(); // Load profiles from the database

        return view;
    }

    /**
     * Loads user profiles from the database and updates the RecyclerView.
     * Author: Pouyan
     */
    private void loadProfiles() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            // Create a list to hold the user profiles
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                // Convert each Firestore document into a User object
                User user = doc.toObject(User.class);
                if (user != null) {
                    users.add(user); // Add the user to the list
                }
            }
            // Update the adapter with the loaded user profiles
            adapter.updateData(users);
        }).addOnFailureListener(e -> {
            // Handle database read failure if needed (currently left blank)
        });
    }
}
