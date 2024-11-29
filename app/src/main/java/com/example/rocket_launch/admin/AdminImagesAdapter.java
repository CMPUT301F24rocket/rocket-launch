package com.example.rocket_launch.admin;

import android.content.Context;
import android.util.Log;
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

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {
    private List<User> users;
    private Context context;

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
        String imagePath = user.getProfilePhotoPath();

        if (imagePath != null && !imagePath.isEmpty()) {
            // Load the image using Firebase URL
            Glide.with(holder.profileImage.getContext())
                    .load(imagePath) // Firebase URL
                    .into(holder.profileImage);
        } else {
            // Set default placeholder if no image exists
            holder.profileImage.setImageResource(R.drawable.default_image);
        }

        holder.userName.setText(user.getUserName() != null ? user.getUserName().trim() : "Unknown User");
        holder.userEmail.setText(user.getUserEmail() != null ? user.getUserEmail().trim() : "No Email");
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
}