package com.example.rocket_launch.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rocket_launch.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * activity that is displayed if the user is an admin
 * Author: Pouyan
 */
public class AdminModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TabLayout tabLayout = findViewById(R.id.admin_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.admin_view_pager);

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
}
