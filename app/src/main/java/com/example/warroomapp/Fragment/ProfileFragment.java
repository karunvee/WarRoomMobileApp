package com.example.warroomapp.Fragment;
import com.example.warroomapp.Activity.Class.ReasonSolutionRes;
import com.example.warroomapp.Activity.CommonRes;
import com.example.warroomapp.Activity.HomeActivity;
import com.example.warroomapp.Activity.UserInfoRes;

import com.example.warroomapp.Activity.AuthInterceptor;
import com.example.warroomapp.Activity.Class.ProficientSkillParameter;

import static com.example.warroomapp.Adaptor.ImageCustom.getRoundedCornerBitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.warroomapp.Activity.User;
import com.example.warroomapp.Activity.MainActivity;
import com.example.warroomapp.Adaptor.ProficientSkillAdapter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesManager sharedPrefManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ProficientSkillParameter> proficientSkillContainer = new ArrayList<>();
    private RecyclerView proSkillView;

    private TextView txtProfile_SkillPoint ;
    private TextView txtProfile_ActionPeriod;
    private TextView txtProfile_MachineType;
    private static class ProficientRes {
        @SerializedName("data")
        public List<ProficientRes.DataItem> data;
        @SerializedName("detail")
        public String detail;
        public List<ProficientRes.DataItem> getData() {
            return data;
        }
        public String getDetail() {
            return detail;
        }
        public static class DataItem{
            @SerializedName("pk")
            private Integer pk;
            @SerializedName("machine")
            public MachineItem machine;
            @SerializedName("member")
            private Integer member_pk;
            @SerializedName("action_period")
            private Integer action_period;
            @SerializedName("fund_point")
            private Integer fund_point;
            @SerializedName("skill_point")
            private Integer skill_point;
            public Integer getPk(){ return pk;}
            public MachineItem getMachine(){ return machine;}
            public Integer getMember_pk(){ return member_pk;}
            public Integer getAction_period(){ return action_period; }
            public Integer getFund_point(){ return  fund_point;}
            public Integer getSkill_point(){ return skill_point;}
            public static class MachineItem{
                @SerializedName("lineInfo")
                private LineItem lineInfo;
                @SerializedName("machine_name")
                private String machine_name;
                @SerializedName("equipment_code")
                private String equipment_code;
                @SerializedName("equipment_type")
                private String equipment_type;

                public LineItem getLineInfo(){ return lineInfo;}
                public String getMachine_name(){ return machine_name;}
                public String getEquipment_code(){ return equipment_code;}
                public String getEquipment_type(){ return equipment_type;}
                public static class LineItem{
                    @SerializedName("line_name")
                    private String line_name;
                    public String getLine_name(){ return line_name;}
                }
            }
        }
    }
    private interface ApiService{
        @GET("/get_proficient_skill/")
        Call<ProficientRes> getProSkillUpdate(
                @Query("pk") Integer userId
        );
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPrefSetting = new SharedPreferencesSetting(getContext());
        sharedPrefManager = new SharedPreferencesManager(getContext());
        ImageView imageView = view.findViewById(R.id.profile_image); // Replace with your ImageView ID
        TextView txtProfileName = view.findViewById(R.id.txtProfileName);
        TextView txtProfileDetail = view.findViewById(R.id.txtProfileDetail);
        txtProfile_SkillPoint = view.findViewById(R.id.txtProfile_SkillPoint);
        txtProfile_ActionPeriod = view.findViewById(R.id.txtProfile_ActionPeriod);
        txtProfile_MachineType = view.findViewById(R.id.txtProfile_MachineType);

        swipeRefreshLayout = view.findViewById(R.id.layout_swipe_profile);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        proSkillView = view.findViewById(R.id.ProficientSkillsView);


        txtProfileName.setText(sharedPrefManager.getName());
        txtProfileDetail.setText((sharedPrefManager.getEmpNo() + " " + sharedPrefManager.getDescription()));
        txtProfile_SkillPoint.setText(sharedPrefManager.getSkillPoint());
        txtProfile_ActionPeriod.setText(sharedPrefManager.getActionPeriod());
        txtProfile_MachineType.setText(sharedPrefManager.getMachineQty());
        String imageURL = sharedPrefManager.getImage();

        Log.i("LOG_MSG", "Image : " + globalVariable.api_url + sharedPrefSetting.getApiUrl() + imageURL);
        String imageUrl = globalVariable.api_url + sharedPrefSetting.getApiUrl() + imageURL; // Replace with your image URL

        if (imageURL.trim().isEmpty() || imageURL == null ){
            imageUrl += "/static/img/person_1.jpg";
        }

        // Load the image from the URL using Picasso
        Picasso.get()
                .load(imageUrl)
                .resize(220,220)
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // The image has been loaded, now apply rounding
                        Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Bitmap roundedBitmap = getRoundedCornerBitmap(originalBitmap);
                        imageView.setImageBitmap(roundedBitmap);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });


        getProSkillUpdate(sharedPrefManager.getTokenId(), sharedPrefManager.getUserId());
        return view;
    }

    @Override
    public void onRefresh(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
            @Override
            public void run(){
                getUserInfoToUpdate(sharedPrefManager.getTokenId(), sharedPrefManager.getUserId());
                getProSkillUpdate(sharedPrefManager.getTokenId(), sharedPrefManager.getUserId());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private void getProSkillUpdate(String tokenId, int userId){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenId))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(globalVariable.api_url + sharedPrefSetting.getApiUrl()) // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        try{
            Call<ProficientRes> call = apiService.getProSkillUpdate(userId);
            call.enqueue(new retrofit2.Callback<ProficientRes>() {
                @Override
                public void onResponse(Call<ProficientRes> call, Response<ProficientRes> response) {
                    if (response.isSuccessful()) {
                        proficientSkillContainer.clear();
                        ProficientRes res = response.body();
                        List<ProficientRes.DataItem> data = res.getData();
                        for (ProficientRes.DataItem item : data) {
                            proficientSkillContainer.add(new ProficientSkillParameter(
                                    item.getPk(),
                                    item.getMachine().getLineInfo().getLine_name(),
                                    item.getMachine().getMachine_name(),
                                    item.getMachine().getEquipment_code(),
                                    item.getMachine().getEquipment_type(),
                                    item.getFund_point().toString(),
                                    item.getSkill_point().toString()
                            ));
                        }
                        ProficientSkillAdapter proficientSkillAdapter = new ProficientSkillAdapter(proficientSkillContainer);
                        proSkillView.setLayoutManager(new LinearLayoutManager(getContext()));
                        proSkillView.setAdapter(proficientSkillAdapter);
                    }else {
                        Log.i("LOG_MSG", "Failed to add ReasonCode. HTTP Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ProficientRes> call, Throwable t) {
                    Log.i("LOG_MSG", "getProSkillUpdate: " + t.getMessage());
                }
            });
        }catch (Exception ex){
            Log.i("LOG_MSG", "getProSkillUpdate: " + ex.getMessage());
        }
    }
    private void getUserInfoToUpdate(String tokenId, int userId){

        try{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenId))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(globalVariable.api_url + sharedPrefSetting.getApiUrl()) // Replace with your API base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            MainActivity.ApiService apiService = retrofit.create(MainActivity.ApiService.class);
            Call<UserInfoRes> call = apiService.getUserInfo(userId);

            call.enqueue(new retrofit2.Callback<UserInfoRes>() {
                @Override
                public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                    if (response.isSuccessful()) {
                        UserInfoRes apiResponse = response.body();

                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();

                        sharedPrefManager.saveUserData(
                                tokenId,
                                apiResponse.getUser().getId(),
                                apiResponse.getUser().getUsername(),
                                apiResponse.getUser().getName(),
                                apiResponse.getUser().getEmpNo(),
                                apiResponse.getUser().getDescription(),
                                apiResponse.getUser().getRemark(),
                                apiResponse.getUser().isUser(),
                                apiResponse.getUser().isStaff(),
                                apiResponse.getUser().getImage(),
                                apiResponse.getUser().getSkillPoint(),
                                apiResponse.getUser().getActionPeriod(),
                                apiResponse.getUser().getMachineQty()
                        );
                        txtProfile_SkillPoint.setText(sharedPrefManager.getSkillPoint());
                        txtProfile_ActionPeriod.setText(sharedPrefManager.getActionPeriod());
                        txtProfile_MachineType.setText(sharedPrefManager.getMachineQty());
                    } else {
                        // Handle the error
                        Toast.makeText(getContext(), "API request failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserInfoRes> call, Throwable t) {
                    // Handle network or other errors
                    Toast.makeText(getContext(), "Network error: " + t.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("LOG_MSG", "Network error: " + t.getMessage());
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("LOG_MSG", "getUserInfo: " + e.getMessage());
        }

    }
}