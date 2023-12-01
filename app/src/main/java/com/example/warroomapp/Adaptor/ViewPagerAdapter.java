package com.example.warroomapp.Adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.warroomapp.Fragment.ChatFragment;
import com.example.warroomapp.Fragment.FavoriteFragment;
import com.example.warroomapp.Fragment.ProfileFragment;
import com.example.warroomapp.Fragment.SettingFragment;
import com.example.warroomapp.Fragment.TasksFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment based on the position
        switch (position) {
            case 0:
                return new TasksFragment();
            case 1:
                return new FavoriteFragment();
            case 2:
                return new ChatFragment();
            case 3:
                return new ProfileFragment();
            case 4:
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Return the number of fragments
        return 5; // Change this based on the number of fragments
    }
}
