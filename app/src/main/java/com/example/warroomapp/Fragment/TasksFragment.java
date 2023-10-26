package com.example.warroomapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.warroomapp.Activity.PagerAdapter;
import com.example.warroomapp.R;
import com.google.android.material.tabs.TabLayout;


public class TasksFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        tabLayout = view.findViewById(R.id.jobs_tab);
        viewPager = view.findViewById(R.id.jobsview_pager);

        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new AllJobsFragment(), "All");
        adapter.AddFragment(new MyJobsFragment(), "My Jobs");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}