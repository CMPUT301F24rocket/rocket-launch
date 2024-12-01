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
    private OnProfileDeleteListener onProfileDeleteListener;

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
        holder.userName.setText(user.getUserName() != null ? user.getUserName() : "No name provided");
        holder.userEmail.setText(user.getUserEmail() != null ? user.getUserEmail() : "No email provided");

        // Long-press to delete
        holder.itemView.setOnLongClickListener(v -> {
            if (onProfileDeleteListener != null) {
                onProfileDeleteListener.onProfileDelete(user, position);
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

    public void removeProfile(int position) {
        users.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }

    public interface OnProfileDeleteListener {
        void onProfileDelete(User user, int position);
    }

    public void setOnProfileDeleteListener(OnProfileDeleteListener listener) {
        this.onProfileDeleteListener = listener;
    }
}
