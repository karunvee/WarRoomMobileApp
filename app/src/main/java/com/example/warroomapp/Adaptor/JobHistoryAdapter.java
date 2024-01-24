package com.example.warroomapp.Adaptor;

import static com.example.warroomapp.Adaptor.ImageCustom.getRoundedCornerBitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.warroomapp.Activity.Class.JobHistoryParameter;
import com.example.warroomapp.Activity.Class.ProficientSkillParameter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JobHistoryAdapter extends RecyclerView.Adapter<JobHistoryAdapter.ViewHolder > {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private ArrayList<JobHistoryParameter> jobHistoryContainers;

    public JobHistoryAdapter(ArrayList<JobHistoryParameter> jobHistoryParameters) {
        this.jobHistoryContainers = jobHistoryParameters;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_action_history, parent, false);
        return new JobHistoryAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            JobHistoryParameter jobHistory = jobHistoryContainers.get(position);

            holder.txtHistory_EmpNo.setText(jobHistory.getResponse_by());
            holder.txtHistory_Reason.setText(jobHistory.getReason_en());
            holder.txtHistory_Solution.setText(jobHistory.getSolution_en());
            holder.txtHistory_EndedTime.setText(jobHistory.getEnded_date());

        }
        catch (Exception ex){
            Log.i("LOG_MSG", "onBindViewHolder " + ex.getMessage());
        }
    }
    @Override
    public int getItemCount() {
        return jobHistoryContainers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtHistory_EmpNo;
        TextView txtHistory_Reason;
        TextView txtHistory_Solution;
        TextView txtHistory_EndedTime;
        public ViewHolder (View view) {
            super(view);
            txtHistory_EmpNo = view.findViewById(R.id.txtHistory_EmpNo);
            txtHistory_Reason = view.findViewById(R.id.txtHistory_Reason);
            txtHistory_Solution = view.findViewById(R.id.txtHistory_Solution);
            txtHistory_EndedTime = view.findViewById(R.id.txtHistory_EndedTime);
        }
    }
}
