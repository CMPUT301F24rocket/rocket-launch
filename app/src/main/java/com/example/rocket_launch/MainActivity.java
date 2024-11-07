package com.example.rocket_launch;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    User user;
    UsersDB usersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usersDB = new UsersDB();

        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        usersDB.getUser(androidID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    checkUserRole(user);
                } else {
                    Button getStartedButton = findViewById(R.id.get_started);
                    getStartedButton.setVisibility(View.VISIBLE);
                    getStartedButton.setOnClickListener(v -> {
                        user = new User();
                        user.setAndroid_id(androidID);
                        new NewUserFragment(user, usersDB).show(getSupportFragmentManager(), "Create New User");
                        usersDB.addUser(androidID, user);
                    });
                }
            }
        }, e -> {
            Log.w("Firebase", "Error getting user", e);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setSelectedItemId(R.id.navigation_home);
    }

    private void checkUserRole(User user) {
        if (user.isAdmin()) {
            Intent intent = new Intent(this, AdminModeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}