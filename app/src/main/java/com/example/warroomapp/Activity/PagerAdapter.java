package com.example.warroomapp.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentsArrayList = new ArrayList<>();
    private List<String> stringArrayList = new ArrayList<>();

    public void AddFragment(Fragment fragment, String s){
        fragmentsArrayList.add(fragment);
        stringArrayList.add(s);
    }
    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentsArrayList.get(position);
    }

    @NonNull
    @Override
    public int getCount() {
        return fragmentsArrayList.size();
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        return stringArrayList.get(position);
    }
}