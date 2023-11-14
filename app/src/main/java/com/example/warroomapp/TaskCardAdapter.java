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

    public void updateAdapter(ArrayList<JobTaskParameter> updatedJobContainers) {
        this.jobContainers.clear();
        this.jobContainers.addAll(updatedJobContainers);
        this.notifyDataSetChanged(); // Notify the adapter of data changes
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

            holder.txtMachineName.setText(job.getMachine());
            holder.txtType.setText(job.getTypeOf());
            holder.txtLineName.setText(job.getLine());
            holder.txtErrorCode.setText(job.getJob());
            holder.txtIssuedDate.setText(job.getStartDate());
            holder.txtDescription.setText(job.getDescription());
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "onBindViewHolder " + ex.getMessage());
        }
    }
    public void removeItemById(int id) {
        try{
            for (int i = 0; i < jobContainers.size(); i++) {
                Log.i("LOG_MSG", "jobContainers id :" + jobContainers.get(i).getId());
                if (jobContainers.get(i).getId() == id) {
                    Log.i("LOG_MSG", "Adapter deleted " + i);
                    jobContainers.remove(i);
                    notifyItemRemoved(i);
                    break; // Stop searching after the item is found and removed
                }
            }
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "Adapter removeItemById " + ex.getMessage());
        }

    }
    @Override
    public int getItemCount() {
        return jobContainers.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder {
        TextView txtMachineName;
        TextView txtType;
        TextView txtLineName;
        TextView txtErrorCode;
        TextView txtIssuedDate;
        TextView txtDescription;

        public ViewHolder (View view) {
            super(view);
            txtMachineName = view.findViewById(R.id.txtMachineName);
            txtType = view.findViewById(R.id.txtType);
            txtLineName = view.findViewById(R.id.txtLineName);
            txtErrorCode = view.findViewById(R.id.txtErrorCode);
            txtIssuedDate = view.findViewById(R.id.txtIssuedDate);
            txtDescription = view.findViewById(R.id.txtDescription);
        }
    }
}
