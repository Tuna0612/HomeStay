package com.tuna.homestay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tuna.homestay.fragment.AccountFragment;
import com.tuna.homestay.fragment.FindAroundFragment;
import com.tuna.homestay.fragment.HomeFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new FindAroundFragment();
            case 2:
                return new AccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
