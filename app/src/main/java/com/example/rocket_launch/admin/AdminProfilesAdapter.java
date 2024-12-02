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
 * Allows long-click deletion of profiles.
 * Author: Pouyan
 */
public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {
    private List<User> users;
    private OnProfileDeleteListener deleteListener;

    public AdminProfilesAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        // Set name and email with placeholders if missing
        holder.tvUserName.setText(user.getUserName() != null && !user.getUserName().trim().isEmpty()
                ? user.getUserName()
                : "No name provided");

        holder.tvUserEmail.setText(user.getUserEmail() != null && !user.getUserEmail().trim().isEmpty()
                ? user.getUserEmail()
                : "No email provided");

        // Handle long-click for deletion
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onProfileDelete(user, holder.getAdapterPosition());
            }
            return true; // Indicate the event is consumed
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateData(List<User> newUsers) {
        users = newUsers;
        notifyDataSetChanged();
    }

    public void removeProfile(int position) {
        users.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnProfileDeleteListener(OnProfileDeleteListener listener) {
        this.deleteListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.user_name);
            tvUserEmail = itemView.findViewById(R.id.user_email);
        }
    }

    public interface OnProfileDeleteListener {
        void onProfileDelete(User user, int position);
    }
}
