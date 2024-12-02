package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;

import java.util.List;

/**
 * Adapter for displaying a list of user profiles in the admin view.
 * Provides functionality for long-press deletion of profiles.
 * Author: Pouyan
 */
public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {
    private List<User> users;
    private OnProfileDeleteListener deleteListener;

    /**
     * Constructor for initializing the adapter with a list of user profiles.
     *
     * @param users List of users to display in the RecyclerView.
     * Author: Pouyan
     */
    public AdminProfilesAdapter(List<User> users) {
        this.users = users;
    }

    /**
     * Inflates the layout for an individual user profile item.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of view (unused here).
     * @return A ViewHolder instance for the user profile item.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_profile, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds user data to the ViewHolder for the given position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the user in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        // Set user name with a fallback for empty or null values
        holder.tvUserName.setText(user.getUserName() != null && !user.getUserName().trim().isEmpty()
                ? user.getUserName()
                : "No name provided");

        // Set user email with a fallback for empty or null values
        holder.tvUserEmail.setText(user.getUserEmail() != null && !user.getUserEmail().trim().isEmpty()
                ? user.getUserEmail()
                : "No email provided");

        // Trigger deletion callback on long press
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onProfileDelete(user, holder.getAdapterPosition());
            }
            return true; // Consume the event
        });
    }

    /**
     * Returns the number of user profiles in the list.
     *
     * @return The total number of user profiles.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Updates the data in the adapter with a new list of users.
     *
     * @param newUsers The updated list of users.
     * Author: Pouyan
     */
    public void updateData(List<User> newUsers) {
        users = newUsers;
        notifyDataSetChanged();
    }

    /**
     * Removes a profile from the list and notifies the adapter.
     *
     * @param position The position of the profile to remove.
     * Author: Pouyan
     */
    public void removeProfile(int position) {
        users.remove(position);
        notifyItemRemoved(position); // Notify RecyclerView of the removed item
    }

    /**
     * Sets the listener for handling profile deletion.
     *
     * @param listener The listener to handle long-press delete events.
     * Author: Pouyan
     */
    public void setOnProfileDeleteListener(OnProfileDeleteListener listener) {
        this.deleteListener = listener;
    }

    /**
     * ViewHolder for user profile items in the RecyclerView.
     * Author: Pouyan
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail;

        /**
         * Links UI components to this ViewHolder.
         *
         * @param itemView The root view of the user profile item layout.
         * Author: Pouyan
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.user_name); // TextView for the user's name
            tvUserEmail = itemView.findViewById(R.id.user_email); // TextView for the user's email
        }
    }

    /**
     * Interface for handling profile deletion events.
     * Author: Pouyan
     */
    public interface OnProfileDeleteListener {
        /**
         * Called when a profile is long-pressed for deletion.
         *
         * @param user The user to be deleted.
         * @param position The position of the user in the list.
         * Author: Pouyan
         */
        void onProfileDelete(User user, int position);
    }
}
