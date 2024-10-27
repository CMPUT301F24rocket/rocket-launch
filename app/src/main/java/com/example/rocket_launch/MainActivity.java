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

        // User is an entrant (the user joins or signs up for events)
        if (user.isEntrant()) {

            // Create a new user that is an entrant. I will delete this once
            // we have a firebase db running
            User new_user = new User();
            Roles entrant_role = new Roles();
            entrant_role.entrant = true;
            new_user.setRoles(entrant_role);

            NewUserFragment new_entrant = new NewUserFragment(new_user);

        }
        else if (user.isOrganizer()) {

            // Create a new user that is an organizer
            User new_user = new User();
            Roles organizer_role = new Roles();
            organizer_role.organizer = true;
            new_user.setRoles(organizer_role);

            NewUserFragment new_organizer = new NewUserFragment(new_user);

        }
        else if (user.isAdmin()) {

            // Create a new user that is an organizer
            User new_user = new User();
            Roles admin_role = new Roles();
            admin_role.organizer = true;
            new_user.setRoles(admin_role);

            NewUserFragment new_organizer = new NewUserFragment(new_user);
        }
        else {
           System.out.print("Error");
        }

    }
}