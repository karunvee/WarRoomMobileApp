package com.example.warroomapp.Fragment;

import static com.example.warroomapp.Fragment.TasksFragment.ARG_JOB_CONTAINERS;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.warroomapp.JobTaskParameter;
import com.example.warroomapp.R;
import com.example.warroomapp.TaskCardAdapter;

import java.util.ArrayList;

public class AllJobsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_jobs, container, false);
        return view;
    }
}