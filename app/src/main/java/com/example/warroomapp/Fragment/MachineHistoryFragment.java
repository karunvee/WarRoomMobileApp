package com.example.warroomapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.warroomapp.Activity.AuthInterceptor;
import com.example.warroomapp.Activity.Class.JobHistoryParameter;
import com.example.warroomapp.Activity.Class.ReasonSolutionRes;
import com.example.warroomapp.Activity.ReasonSolutionParameter;
import com.example.warroomapp.Adaptor.JobHistoryAdapter;
import com.example.warroomapp.Adaptor.ReaSolAdapter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class MachineHistoryFragment extends Fragment {

    private GlobalVariable globalVariable = new GlobalVariable();
    private JobHistoryAdapter jobHistoryAdapter;
    private RecyclerView jobHistoryView;
    private ArrayList<JobHistoryParameter> jobHistoryContainer = new ArrayList<>();
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesMachine sharedPreferencesMachine;

    private class JobsHistoryRes implements Serializable{
        @SerializedName("data")
        public List<JobsHistoryRes.DataItem> data;
        @SerializedName("detail")
        public String detail;
        public List<JobsHistoryRes.DataItem> getData() {
            return data;
        }
        public String getDetail() {
            return detail;
        }
        public class DataItem {
            @SerializedName("pk")
            private Integer pk;

            @SerializedName("reasonCode")
            private ReasonCode reasonCode;

            @SerializedName("solutionCode")
            private SolutionCode solutionCode;

            @SerializedName("responder_member")
            private ResponseBy responderBy;
            @SerializedName("ended_date")
            private String EndedDate;

            public class ReasonCode{
                @SerializedName("en_description")
                private String enDescription;
                @SerializedName("th_description")
                private String thDescription;

                public String getEnDescription() {
                    return enDescription;
                }

                public String getThDescription() {
                    return thDescription;
                }
            }
            public class SolutionCode{
                @SerializedName("en_description")
                private String enDescription;
                @SerializedName("th_description")
                private String thDescription;

                public String getEnDescription() {
                    return enDescription;
                }

                public String getThDescription() {
                    return thDescription;
                }
            }
            public class ResponseBy{
                @SerializedName("username")
                private String username;
                @SerializedName("emp_no")
                private String emp_no;

                public String getUsername() {
                    return username;
                }

                public String getEmp_no() {
                    return emp_no;
                }
            }
            public Integer getPk() {
                return pk;
            }

            public ReasonCode getReasonCode() {
                return reasonCode;
            }
            public SolutionCode getSolutionCode() {
                return solutionCode;
            }

            public ResponseBy getResponderBy() {
                return responderBy;
            }
            public String getEndedDate() {
                return EndedDate;
            }
        }
    }
    private interface ApiService{
        @GET("/get_jobs_history/")
        Call<JobsHistoryRes> getJobsHistory(
                @Query("equipment_code") String equipment_code,
                @Query("error_code") String error_code
        );
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefManager = new SharedPreferencesManager(getContext());
        sharedPrefSetting = new SharedPreferencesSetting(getContext());
        sharedPreferencesMachine = new SharedPreferencesMachine(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machine_history, container, false);
        jobHistoryView = view.findViewById(R.id.job_history_view);

        Log.i("LOG_MSG", "tokenId: " + sharedPrefManager.getTokenId() + "\nEquipCode: " + sharedPreferencesMachine.getEquipCode()+ "\nErrorCode: " + sharedPreferencesMachine.getName());
        getJobsHistory(sharedPrefManager.getTokenId(), sharedPreferencesMachine.getEquipCode(), sharedPreferencesMachine.getName());
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("LOG_MSG", "Jobs history onStart");
    }
    private void getJobsHistory(String tokenId, String equipCode, String errorCode){

        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenId))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(globalVariable.api_url + sharedPrefSetting.getApiUrl()) // Replace with your API base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);
            Call<JobsHistoryRes> call = apiService.getJobsHistory(equipCode, errorCode);
            call.enqueue(new Callback<JobsHistoryRes>() {
                @Override
                public void onResponse(Call<JobsHistoryRes> call, Response<JobsHistoryRes> response) {
                    if(response.isSuccessful()){
                        jobHistoryContainer.clear();
                        JobsHistoryRes res = response.body();
                        List<JobsHistoryRes.DataItem> data = res.getData();
                        for (JobsHistoryRes.DataItem item : data) {

                            jobHistoryContainer.add(new JobHistoryParameter(
                                    item.getPk(),
                                    item.getReasonCode().getEnDescription(),
                                    item.getReasonCode().getThDescription(),
                                    item.getSolutionCode().getEnDescription(),
                                    item.getSolutionCode().getThDescription(),
                                    item.getResponderBy().getEmp_no() + " ," + item.getResponderBy().getUsername() ,
                                    item.getEndedDate()
                            ));
                        }
                        jobHistoryAdapter = new JobHistoryAdapter(jobHistoryContainer);

                        jobHistoryView.setLayoutManager(new LinearLayoutManager(getContext()));
                        jobHistoryView.setAdapter(jobHistoryAdapter);
                    }
                    else {
                        Toast.makeText(getContext(), "Failed to getJobsHistory. HTTP Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JobsHistoryRes> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to getJobsHistory. Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception ex){
            Log.i("LOG_MSG", "getJobsHistory: " + ex.getMessage());
        }
    }
}