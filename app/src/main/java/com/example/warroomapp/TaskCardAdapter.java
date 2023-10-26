package com.example.warroomapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskCardAdapter extends RecyclerView.Adapter<TaskCardAdapter.ViewHolder > {
    private ArrayList<JobTaskParameter> jobContainers;

    public TaskCardAdapter(ArrayList<JobTaskParameter> jobContainers) {
        this.jobContainers = jobContainers;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder  holder, int position) {
        try{
            JobTaskParameter job = jobContainers.get(position);
            holder.nameTextView.setText(job.getJob());
            holder.descriptionTextView.setText(job.getDescription());
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "onBindViewHolder " + ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return jobContainers.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;

        public ViewHolder (View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
        }
    }
}
