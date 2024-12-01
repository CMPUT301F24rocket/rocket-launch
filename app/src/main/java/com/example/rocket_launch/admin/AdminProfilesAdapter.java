package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for managing and displaying user profiles in the admin view.
 * Handles binding user data to RecyclerView items.
 * Author: Pouyan
 */
public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {
    private List<User> users;

    /**
     * Constructor for AdminProfilesAdapter.
     *
     * @param users The initial list of users to display.
     * Author: Pouyan
     */
    public AdminProfilesAdapter(List<User> users) {
        this.users = users;
    }

    /**
     * Creates and returns a new ViewHolder for a profile item.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type (not used here).
     * @return A new ViewHolder instance.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual user profile items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_profile, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds user data to the ViewHolder at the specified position.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        // Use placeholders if the user's name or email is missing
        String userName = (user.getUserName() != null && !user.getUserName().trim().isEmpty())
                ? user.getUserName().trim()
                : "No name provided";
        String userEmail = (user.getUserEmail() != null && !user.getUserEmail().trim().isEmpty())
                ? user.getUserEmail().trim()
                : "No email provided";

        holder.userName.setText(userName);
        holder.userEmail.setText(userEmail);
    }

    /**
     * Returns the number of user profiles in the list.
     *
     * @return The total number of users.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Updates the list of users and refreshes the RecyclerView.
     * Only users with valid profile photo paths are included.
     *
     * @param newUsers The new list of users to display.
     * Author: Pouyan
     */
    public void updateData(List<User> newUsers) {
        this.users = new ArrayList<>();
        for (User user : newUsers) {
            // Only include users with valid photo paths
            if (user.getProfilePhotoPath() != null) {
                this.users.add(user);
            }
        }
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    /**
     * ViewHolder class for AdminProfilesAdapter.
     * Holds references to the TextViews for name and email.
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view of the individual profile item.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind UI elements to their IDs in the layout
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}
