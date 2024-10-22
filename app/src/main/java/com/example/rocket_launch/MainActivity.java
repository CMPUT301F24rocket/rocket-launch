package com.example.rocket_launch;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UsersDB usersDB = new UsersDB(); // load user database
        User user = usersDB.getUser(); // try to get firebase user (returns null for now)

        // if user is null, make a new one and prompt for user information
        if (user == null) {
            user = new User();
            new NewUserFragment(user).show(getSupportFragmentManager(), "Create New User");
        }

        if (user.isEntrant()) {
            // goto entrant activity
        }
        else if (user.isOrganizer()) {
            // goto organizer activity
        }
        else if (user.isAdmin()) {
            // goto admin activity
        }
        else {
            // we were not supposed to get here
        }

    }
}