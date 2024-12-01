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
 * Includes delete functionality.
 */
public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private OnImageDeleteListener onImageDeleteListener;

    public AdminImagesAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        // Load profile image or default placeholder
        String imagePath = user.getProfilePhotoPath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context).load(imagePath).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.default_image);
        }

        // Set user details
        holder.userName.setText(user.getUserName() != null ? user.getUserName() : "Unknown User");
        holder.userEmail.setText(user.getUserEmail() != null ? user.getUserEmail() : "No Email");

        // Highlight on long press and trigger delete confirmation
        holder.itemView.setOnLongClickListener(v -> {
            if (onImageDeleteListener != null) {
                onImageDeleteListener.onImageDelete(user);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateData(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName, userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }

    public interface OnImageDeleteListener {
        void onImageDelete(User user);
    }

    public void setOnImageDeleteListener(OnImageDeleteListener listener) {
        this.onImageDeleteListener = listener;
    }
}
