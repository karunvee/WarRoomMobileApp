package com.example.warroomapp.Adaptor;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warroomapp.Activity.ReasonSolutionParameter;
import com.example.warroomapp.R;

import java.util.ArrayList;

public class ReaSolAdapter extends RecyclerView.Adapter<ReaSolAdapter.ViewHolder> {

    private ArrayList<ReasonSolutionParameter> ReaSolContainer;
    private OnItemClickListener onItemClickListener;
    private OnLongClickListener onLongClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    public ReaSolAdapter(ArrayList<ReasonSolutionParameter> ReaSolContainer) {
        this.ReaSolContainer = ReaSolContainer;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reasol, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,@SuppressLint("RecyclerView") int position) {
        try{
            ReasonSolutionParameter reaSol = ReaSolContainer.get(position);

            holder.txtDescriptionEN.setText(reaSol.getEn_description());
            holder.txtDescriptionTH.setText(reaSol.getTh_description());
            holder.txtRating.setText(reaSol.getRating() + "%");

            if (position == 0) {
                setMarginTop(holder.itemView, 90);
            } else {
                setMarginTop(holder.itemView, 0);
            }

            if (position != selectedPosition) {
                holder.itemView.setBackgroundResource(R.drawable.custom_ripple_white_to_green_card);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        selectedPosition = position;
                        notifyDataSetChanged(); // Notify the adapter to redraw the items

                        onItemClickListener.onItemClick(position);
                        holder.itemView.setBackgroundResource(R.drawable.custom_ripple_green_to_white_card);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onLongClickListener != null) {
                        onLongClickListener.onItemLongClick(position);
                    }
                    return false;
                }
            });
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "onBindViewHolder ReaSol " + ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return ReaSolContainer.size();
    }
    public static class ViewHolder  extends RecyclerView.ViewHolder {
        TextView txtDescriptionEN;
        TextView txtDescriptionTH;
        TextView txtRating;
        CardView cardView;
        public ViewHolder (View view) {
            super(view);
            txtDescriptionEN = view.findViewById(R.id.txtDesENofReaSol);
            txtDescriptionTH = view.findViewById(R.id.txtDesTHofReaSol);
            txtRating = view.findViewById(R.id.txtRatingOfReaSol);
            cardView = view.findViewById(R.id.cardViewReasonSolution);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnLongClickListener {
        void onItemLongClick(int position);
    }
    public void setOnLongClickListener(OnLongClickListener listener) {
        this.onLongClickListener = listener;
    }
    private void setMarginTop(View view, int marginTop) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.topMargin = marginTop;
        view.setLayoutParams(params);
    }
}
