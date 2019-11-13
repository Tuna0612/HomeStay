package com.tuna.homestay.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tuna.homestay.R;
import com.tuna.homestay.adapter.SectionsPagerAdapter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private Button btnToolbar;
    private int currentTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setSupportActionBar(toolbar);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcon();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch ((tab.getPosition())){
                    case 0:
                        toolbar.setTitle("Homestay");
                        btnToolbar.setVisibility(View.VISIBLE);
                        btnToolbar.setBackgroundResource(R.drawable.ic_filter_white);
                        currentTab = 1;
                        break;
                    case 1:
                        toolbar.setTitle("Homestay around here");
                        btnToolbar.setVisibility(View.VISIBLE);
                        btnToolbar.setBackgroundResource(R.drawable.ic_autorenew);
                        currentTab = 2;
                        break;
                    case 2:
                        toolbar.setTitle("Profile");
                        btnToolbar.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        btnToolbar = findViewById(R.id.btn_toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupTabIcon(){
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_query_builder);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_place);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_person_outline);
    }
}
