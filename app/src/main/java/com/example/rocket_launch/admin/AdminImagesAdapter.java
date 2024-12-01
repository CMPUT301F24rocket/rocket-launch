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
 * Author: Pouyan
 */
public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {
    private List<User> users;
    private Context context;

    /**
     * Constructor for AdminImagesAdapter.
     *
     * @param users   List of users whose profile images and details will be displayed.
     * @param context The context of the calling activity or fragment.
     * Author: Pouyan
     */
    public AdminImagesAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    /**
     * Creates a new ViewHolder for displaying each user's profile.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of the view (not used here).
     * @return A new ViewHolder instance.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for an individual profile item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_image, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the user's data (image, name, email) to the ViewHolder.
     *
     * @param holder   The ViewHolder for the user profile.
     * @param position The position of the user in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        String imagePath = user.getProfilePhotoPath();

        if (imagePath != null && !imagePath.isEmpty()) {
            // Load the user's profile image using Glide
            Glide.with(holder.profileImage.getContext())
                    .load(imagePath) // Load from Firebase URL
                    .into(holder.profileImage);
        } else {
            // Set a default placeholder if no image exists
            holder.profileImage.setImageResource(R.drawable.default_image);
        }

        // Set the user's name or default text if missing
        holder.userName.setText(user.getUserName() != null ? user.getUserName().trim() : "Unknown User");

        // Set the user's email or default text if missing
        holder.userEmail.setText(user.getUserEmail() != null ? user.getUserEmail().trim() : "No Email");
    }

    /**
     * Gets the total number of user profiles in the list.
     *
     * @return The size of the user list.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Updates the user list with new data and refreshes the RecyclerView.
     *
     * @param newUsers The updated list of users.
     * Author: Pouyan
     */
    public void updateData(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for holding and binding the user's profile data (image, name, and email).
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName, userEmail;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view representing a single profile item.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find and bind the profile image and text views
            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}
