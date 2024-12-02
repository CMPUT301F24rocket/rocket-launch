package com.example.rocket_launch.organizer_events_tab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.rocket_launch.R;
import com.example.rocket_launch.User;

import java.util.List;

public class UserListArrayAdapter extends ArrayAdapter<User> {

    public UserListArrayAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, 0, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_content, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.user_name);
        TextView userEmail = convertView.findViewById(R.id.user_email);

        if (user != null) {
            userName.setText("name: " + user.getUserName());
            userEmail.setText("email: " + user.getUserEmail());
        }

        return convertView;
    }
}
