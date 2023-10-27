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

    private RecyclerView recyclerView;
    private TaskCardAdapter taskAdapter;
    private ArrayList<Integer> StoredTasks = new ArrayList<>();
    private  ArrayList<JobTaskParameter> jobContainers = new ArrayList<JobTaskParameter>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_jobs, container, false);
        FirstTimeAdapterReload(view);

        return view;
    }

    private void FirstTimeAdapterReload(View view){
        try {
//            recyclerView = view.findViewById(R.id.allJob_RecyclerView);
//            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//            taskAdapter = new TaskCardAdapter(new ArrayList<>());
//            recyclerView.setAdapter(taskAdapter);

            recyclerView = view.findViewById(R.id.allJob_RecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            Bundle argsReceiver = getArguments();
            if (argsReceiver != null && argsReceiver.containsKey(ARG_JOB_CONTAINERS)) {
                jobContainers = argsReceiver.getParcelableArrayList(ARG_JOB_CONTAINERS);
                for (JobTaskParameter job : jobContainers) {
                    Log.i("LOG_MSG", "jobContainers fragment_all_jobs :" + job.getId());
                }
                taskAdapter = new TaskCardAdapter(jobContainers);
                recyclerView.setAdapter(taskAdapter);
            }
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "TaskFragment :" + ex.getMessage());
        }
    }
}