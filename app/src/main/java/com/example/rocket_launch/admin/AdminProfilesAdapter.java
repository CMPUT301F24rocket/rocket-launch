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

public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {
    private List<User> users;

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
        String userName = (user.getUserName() != null && !user.getUserName().trim().isEmpty())
                ? user.getUserName().trim()
                : "No name provided";
        String userEmail = (user.getUserEmail() != null && !user.getUserEmail().trim().isEmpty())
                ? user.getUserEmail().trim()
                : "No email provided";
        holder.userName.setText(userName);
        holder.userEmail.setText(userEmail);
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
        TextView userName, userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}