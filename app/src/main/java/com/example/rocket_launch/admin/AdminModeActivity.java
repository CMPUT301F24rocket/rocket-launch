package com.example.rocket_launch.admin;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rocket_launch.MainActivity;
import com.example.rocket_launch.R;
import com.example.rocket_launch.Roles;
import com.example.rocket_launch.SelectRolesFragment;
import com.example.rocket_launch.StartUpFragment;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UserHomepageFragment;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * activity that is displayed if the user is an admin
 * Author: Pouyan
 */
public class AdminModeActivity extends AppCompatActivity {
    private UsersDB usersDB;
    private User user;
    private ImageButton mainActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        usersDB = new UsersDB();
        user = new User();

        TabLayout tabLayout = findViewById(R.id.admin_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.admin_view_pager);
        mainActivityButton = findViewById(R.id.back_to_main_activity_button);

        getUser();
        mainActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        AdminModePagerAdapter adapter = new AdminModePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Events"); break;
                case 1: tab.setText("Profiles"); break;
                case 2: tab.setText("Images"); break;
                case 3: tab.setText("QR Data"); break;
                case 4: tab.setText("Facilities"); break;
            }
        }).attach();
    }

    private void getUser(){
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        usersDB.getUser(androidID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                if (newUser != null) {
                    user = newUser;

                    if (user.getRoles().isEntrant() || user.getRoles().isOrganizer()){
                        mainActivityButton.setVisibility(View.VISIBLE);
                    } else {
                        mainActivityButton.setVisibility(View.GONE);
                    }
                }
            }
        }, e -> Log.w("Firebase", "Error getting user", e));
    }
}
