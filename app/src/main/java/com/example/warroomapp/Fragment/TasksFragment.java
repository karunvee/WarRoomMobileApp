package com.example.warroomapp.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.warroomapp.Activity.HomeActivity;
import com.example.warroomapp.Activity.LoginActivity;
import com.example.warroomapp.Activity.MainActivity;
import com.example.warroomapp.Activity.PagerAdapter;
import com.example.warroomapp.JobTaskParameter;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.TaskCardAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class TasksFragment extends Fragment {
    private  ArrayList<JobTaskParameter> jobContainers = new ArrayList<JobTaskParameter>();
    public static final String ARG_JOB_CONTAINERS = "jobContainers";
    TabLayout tabLayout;
    ViewPager viewPager;
    private SharedPreferencesManager sharedPrefManager;
    private OnFunctionCallListener functionCallListener;

    private boolean isFragmentVisible = false;
    public interface OnFunctionCallListener {
        void getJobUpdate(String tokenId);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("LOG_MSG", "TasksFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        sharedPrefManager = new SharedPreferencesManager(getContext());
        tabLayout = view.findViewById(R.id.jobs_tab);
        viewPager = view.findViewById(R.id.jobsview_pager);

        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        AllJobsFragment allJobsFragment = new AllJobsFragment();
        MyJobsFragment myJobsFragment = new MyJobsFragment();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (functionCallListener != null) {
                    functionCallListener.getJobUpdate(sharedPrefManager.getTokenId());
                }
            }
        }, 1000);

        Button btnRefreshJob = view.findViewById(R.id.btnRefreshJob);
        btnRefreshJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionCallListener != null) {
                    functionCallListener.getJobUpdate(sharedPrefManager.getTokenId());
                }
            }
        });

        adapter.AddFragment(allJobsFragment, "All");
        adapter.AddFragment(myJobsFragment, "My Jobs");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Log.i("LOG_MSG", "TasksFragment onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }

    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i("LOG_MSG", "TasksFragment was onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("LOG_MSG", "TasksFragment was onStop");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("LOG_MSG", "TasksFragment was destroy");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            functionCallListener = (OnFunctionCallListener) context;
        } catch (ClassCastException e) {
            Log.i("LOG_MSG", "onAttach :" + e.getMessage());
            throw new ClassCastException(context.toString() + " must implement OnFunctionCallListener");
        }
    }
}