package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.rocket_launch.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class OrganizerViewEntrantListsFragment extends Fragment {

    /**
     * Default constructor
     */
    public OrganizerViewEntrantListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_view_entrant_lists_fragment, container, false);

        TabLayout tabLayout = view.findViewById(R.id.entrant_list_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.entrant_list_view_pager);

        OrganizerEntrantListPagerAdapter adapter = new OrganizerEntrantListPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Waitlist"); break;
                case 1: tab.setText("Invited"); break;
                case 2: tab.setText("Cancelled"); break;
                case 3: tab.setText("Final"); break;
            }
        }).attach();


        //back button
        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}