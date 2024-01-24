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

import com.example.warroomapp.Activity.Class.ProficientSkillParameter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.JobTaskParameter;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;
import com.example.warroomapp.TaskCardAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProficientSkillAdapter extends RecyclerView.Adapter<ProficientSkillAdapter.ViewHolder > {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private ArrayList<ProficientSkillParameter> proSkillContainers;

    public ProficientSkillAdapter(ArrayList<ProficientSkillParameter> proSkillContainers) {
        this.proSkillContainers = proSkillContainers;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_proficient_skill, parent, false);
        return new ProficientSkillAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            ProficientSkillParameter proSkill = proSkillContainers.get(position);

            holder.txtProCard_MachineName.setText(proSkill.getMachine_name());
            holder.txtProCard_LineName.setText(proSkill.getLine_name());
            holder.txtProCard_equipCode.setText(proSkill.getEquip_code());
            holder.txtProCard_equipType.setText(proSkill.getEquip_type());
            holder.txtProCard_fundamentalPoint.setText(proSkill.getFund_point());
            holder.txtProCard_SkillPoint.setText(proSkill.getSkill_point());


            String imageURL = "http://10.150.192.16/images/" + proSkill.getEquip_type() + ".jpg";

            Log.i("LOG_MSG", "Image : " + imageURL);

            if (imageURL.trim().isEmpty() || imageURL == null ){
                imageURL = "https://thwgrwarroom.deltaww.com/media/images/default.png";
            }

            Picasso.get()
                    .load(imageURL)
                    .resize(150,150)
                    .centerCrop()
                    .into(holder.imgProCard_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            // The image has been loaded, now apply rounding
                            Bitmap originalBitmap = ((BitmapDrawable) holder.imgProCard_image.getDrawable()).getBitmap();
                            Bitmap roundedBitmap = getRoundedCornerBitmap(originalBitmap);
                            holder.imgProCard_image.setImageBitmap(roundedBitmap);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            String imageURL = "https://thwgrwarroom.deltaww.com/media/images/default.png";
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(80,80)
                                    .centerCrop()
                                    .into(holder.imgProCard_image, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            // The image has been loaded, now apply rounding
                                            Bitmap originalBitmap = ((BitmapDrawable) holder.imgProCard_image.getDrawable()).getBitmap();
                                            Bitmap roundedBitmap = getRoundedCornerBitmap(originalBitmap);
                                            holder.imgProCard_image.setImageBitmap(roundedBitmap);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            e.printStackTrace();
                                            Log.i("LOG_MSG", "Image : " + proSkill.getEquip_type());
                                        }

                                    });
                        }

                    });
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "onBindViewHolder " + ex.getMessage());
        }
    }
    @Override
    public int getItemCount() {
        return proSkillContainers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgProCard_image;
        TextView txtProCard_MachineName;
        TextView txtProCard_LineName;
        TextView txtProCard_equipCode;
        TextView txtProCard_equipType;
        TextView txtProCard_fundamentalPoint;
        TextView txtProCard_SkillPoint;
        public ViewHolder (View view) {
            super(view);
            txtProCard_MachineName = view.findViewById(R.id.txtProCard_MachineName);
            txtProCard_LineName = view.findViewById(R.id.txtProCard_LineName);
            txtProCard_equipCode = view.findViewById(R.id.txtProCard_equipCode);
            txtProCard_equipType = view.findViewById(R.id.txtProCard_equipType);
            txtProCard_fundamentalPoint = view.findViewById(R.id.txtProCard_fundamentalPoint);
            txtProCard_SkillPoint = view.findViewById(R.id.txtProCard_SkillPoint);
            imgProCard_image = view.findViewById(R.id.imgProCard_image);
        }
    }
}
