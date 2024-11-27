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
 * Fragment for displaying contents of profiles tab for admin
 */
public class AdminProfilesFragment extends Fragment {
    private RecyclerView profilesRecyclerView;
    private AdminProfilesAdapter adapter;
    private UsersDB usersDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        profilesRecyclerView = view.findViewById(R.id.profiles_recycler_view);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(profilesRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        profilesRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new AdminProfilesAdapter(new ArrayList<>());
        profilesRecyclerView.setAdapter(adapter);

        usersDB = new UsersDB();
        loadProfiles();

        return view;
    }

    private void loadProfiles() {
        usersDB.getUsersRef().get().addOnSuccessListener(querySnapshot -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                User user = doc.toObject(User.class);
                if (user != null) {
                    users.add(user);
                }
            }
            adapter.updateData(users);
        }).addOnFailureListener(e -> {
        });
    }
}