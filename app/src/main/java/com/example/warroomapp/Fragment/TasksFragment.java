package com.example.warroomapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.warroomapp.Activity.PagerAdapter;
import com.example.warroomapp.JobTaskParameter;
import com.example.warroomapp.R;
import com.example.warroomapp.TaskCardAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class TasksFragment extends Fragment {
    private  ArrayList<JobTaskParameter> jobContainers = new ArrayList<JobTaskParameter>();
    public static final String ARG_JOB_CONTAINERS = "jobContainers";
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
        AllJobsFragment allJobsFragment = new AllJobsFragment();
        MyJobsFragment myJobsFragment = new MyJobsFragment();

        //GET jobContainers From HomeActivity to this fragment
        jobContainers = GettingJobContainer();

        //POST jobContainers From this fragment to AllJobFragment
        Bundle argsSender = new Bundle();
        argsSender.putParcelableArrayList(ARG_JOB_CONTAINERS, jobContainers); // Replace with your data
        allJobsFragment.setArguments(argsSender);

        adapter.AddFragment(allJobsFragment, "All");
        adapter.AddFragment(myJobsFragment, "My Jobs");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
    public ArrayList<JobTaskParameter> GettingJobContainer(){
        ArrayList<JobTaskParameter> jobCon = new ArrayList<JobTaskParameter>();
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_JOB_CONTAINERS)) {
            jobCon = args.<JobTaskParameter>getParcelableArrayList(ARG_JOB_CONTAINERS);
            for (JobTaskParameter job : jobCon) {
                Log.i("LOG_MSG", "jobContainers TaskFragment :" + job.getId());
            }
        }
        if (jobCon == null) {
            // Handle the case where jobContainers is not provided
            jobCon = new ArrayList<>(); // Initialize with an empty list or some default data
        }
        return jobCon;
    }
}