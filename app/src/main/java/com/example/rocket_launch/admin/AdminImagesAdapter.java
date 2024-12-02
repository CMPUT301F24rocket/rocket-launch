package com.example.rocket_launch.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;

import java.util.List;

/**
 * Adapter for displaying a list of user profile images and details in the admin view.
 * This adapter also includes long-press functionality for deleting user images.
 * Author: Pouyan
 */
public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {
    private List<User> users; // List of users to display
    private Context context; // Context for Glide image loading
    private OnImageDeleteListener onImageDeleteListener; // Listener for handling image deletions

    /**
     * Constructor for initializing the adapter with users and context.
     *
     * @param users  List of users to display.
     * @param context The context for image loading.
     * Author: Pouyan
     */
    public AdminImagesAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    /**
     * Creates a new ViewHolder when RecyclerView needs one.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of view (unused here).
     * @return A ViewHolder for displaying user images.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_image, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a specific position.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the user in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        // Load profile image or set a default placeholder if the image path is missing
        String imagePath = user.getProfilePhotoPath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context).load(imagePath).into(holder.profileImage); // Load image with Glide
        } else {
            holder.profileImage.setImageResource(R.drawable.default_image); // Default placeholder
        }

        // Set user name or fallback to "Unknown User" if missing
        holder.userName.setText(user.getUserName() != null ? user.getUserName() : "Unknown User");

        // Set user email or fallback to "No Email" if missing
        holder.userEmail.setText(user.getUserEmail() != null ? user.getUserEmail() : "No Email");

        // Set long press listener for triggering image deletion
        holder.itemView.setOnLongClickListener(v -> {
            if (onImageDeleteListener != null) {
                onImageDeleteListener.onImageDelete(user);
            }
            return true; // Indicate that the long press was handled
        });
    }

    /**
     * Returns the number of users in the list.
     *
     * @return The size of the user list.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Updates the user list and refreshes the RecyclerView.
     *
     * @param newUsers The updated list of users.
     * Author: Pouyan
     */
    public void updateData(List<User> newUsers) {
        this.users = newUsers; // Replace the old list
        notifyDataSetChanged(); // Notify the RecyclerView to refresh
    }

    /**
     * ViewHolder class for holding views in each RecyclerView item.
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage; // ImageView for displaying profile image
        TextView userName, userEmail; // TextViews for displaying user name and email

        /**
         * Constructor for initializing ViewHolder views.
         *
         * @param itemView The root view of the item layout.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }

    /**
     * Interface for handling image deletion events.
     * Author: Pouyan
     */
    public interface OnImageDeleteListener {
        void onImageDelete(User user);
    }

    /**
     * Sets the listener for handling image deletions.
     *
     * @param listener The listener to set.
     * Author: Pouyan
     */
    public void setOnImageDeleteListener(OnImageDeleteListener listener) {
        this.onImageDeleteListener = listener;
    }
}
