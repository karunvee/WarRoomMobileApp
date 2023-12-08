package com.example.warroomapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.warroomapp.Activity.AuthInterceptor;
import com.example.warroomapp.Activity.Class.ReasonSolutionRes;
import com.example.warroomapp.Activity.CommonRes;
import com.example.warroomapp.Activity.HomeActivity;
import com.example.warroomapp.Activity.MachineActivity;
import com.example.warroomapp.Activity.PagerAdapter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.MainApp;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.annotations.SerializedName;

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

public class MachineActionFragment extends Fragment {
    public static class RequestBodyReasonSolutionCode {
        @SerializedName("equipment_type")
        private String equipment_type;
        @SerializedName("error_code")
        private String error_code;
        @SerializedName("en_description")
        private String en_description;
        @SerializedName("th_description")
        private String th_description;
        @SerializedName("update_emp")
        private String update_emp;
        public RequestBodyReasonSolutionCode(String equipment_type, String error_code, String en_description, String th_description, String update_emp) {
            this.equipment_type = equipment_type;
            this.error_code = error_code;
            this.en_description = en_description;
            this.th_description = th_description;
            this.update_emp = update_emp;
        }
    }
    public static class RequestBodySummitCode{
        @SerializedName("error_code")
        private String error_code;
        @SerializedName("equip_type")
        private String equip_type;
        @SerializedName("pk_reason_code")
        private String pk_reason_code;
        @SerializedName("pk_solution_code")
        private String pk_solution_code;
        @SerializedName("jid")
        private String jobId;
        public RequestBodySummitCode(String error_code, String equip_type, String pk_reason_code, String pk_solution_code, String jobId){
            this.error_code = error_code;
            this.equip_type = equip_type;
            this.pk_reason_code = pk_reason_code;
            this.pk_solution_code = pk_solution_code;
            this.jobId = jobId;
        }
    }
    private interface ApiService{
        @POST("/post_reason_code/")
        Call<CommonRes> postReasonCode (
                @Body RequestBodyReasonSolutionCode requestBodyReasonSolutionCode
        );
        @POST("/post_solution_code/")
        Call<CommonRes> postSolutionCode (
                @Body RequestBodyReasonSolutionCode requestBodyReasonSolutionCode
        );

        @GET("get_reason_code/")
        Call<ReasonSolutionRes> getReasonCode(
                @Query("equipment_type") String equipmentType,
                @Query("error_code") String errorCode
        );
        @GET("get_solution_code/")
        Call<ReasonSolutionRes> getSolutionCode(
                @Query("equipment_type") String equipmentType,
                @Query("error_code") String errorCode
        );

        @POST("/summit_reason_solution/")
        Call<CommonRes> postSummitCode (
                @Body RequestBodySummitCode requestBodySummitCode
        );
    }
    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesManager sharedPrefManager;
    private static SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesMachine sharedPreferencesMachine;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machine_action, container, false);

        sharedPrefManager = new SharedPreferencesManager(getActivity().getApplicationContext());
        sharedPrefSetting = new SharedPreferencesSetting(getActivity().getApplicationContext());
        sharedPreferencesMachine = new SharedPreferencesMachine(getActivity().getApplicationContext());

        TextView txtErrorCode = view.findViewById(R.id.txtErrorCode);
        TextView txtErrorDescription = view.findViewById(R.id.txtErrorDescription);
        TextView txtIssueDate = view.findViewById(R.id.txtIssueDate);

        txtIssueDate.setText(sharedPreferencesMachine.getIssuedDate());
        txtErrorCode.setText(sharedPreferencesMachine.getName());
        txtErrorDescription.setText(sharedPreferencesMachine.getDescription());

        Button btnSummitJob = view.findViewById(R.id.btnSummitJob);
        TabLayout tabLayout = view.findViewById(R.id.reason_solution_tab);
        ViewPager viewPager = view.findViewById(R.id.reason_solution_pager);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());

        adapter.AddFragment(new ReasonFragment(), "Reason");
        adapter.AddFragment(new SolutionFragment(), "Solution");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        ((MainApp)getContext().getApplicationContext()).setPk_reasonCode(0);
        ((MainApp)getContext().getApplicationContext()).setPk_solutionCode(0);
        btnSummitJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Integer reasonCode = ((MainApp)getContext().getApplicationContext()).getPk_reasonCode();
                    Integer solutionCode = ((MainApp)getContext().getApplicationContext()).getPk_solutionCode();

                    Log.i("LOG_MSG", "getPkReasonCode Id: " + reasonCode +
                            "\ngetPkSolutionCode Id: " + solutionCode +
                            "\nErrorCode pk: " + sharedPreferencesMachine.getName()
                    );

                    if(reasonCode.equals(0) || solutionCode.equals(0)){
                        Toast.makeText(getContext(), "Please select ReasonCode and SolutionCode!!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Integer jobId = sharedPreferencesMachine.getPk();
                        RequestBodySummitCode requestBodySummitCode = new RequestBodySummitCode(
                                sharedPreferencesMachine.getName(),
                                sharedPreferencesMachine.getEquipType(),
                                reasonCode.toString(), solutionCode.toString(),
                                jobId.toString()
                        );
                        postSummitCodeFunc(sharedPrefManager.getTokenId(), requestBodySummitCode);

                        Intent intentMachineActivity = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intentMachineActivity);
                        getActivity().finish();
                    }
                }
                catch (Exception ex){
                    Log.i("LOG_MSG", "btnSummitJob: " + ex.getMessage());
                }
            }
        });

        return view;
    }

    private void postSummitCodeFunc(String tokenId, RequestBodySummitCode requestBodySummitCode){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenId))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(globalVariable.api_url + sharedPrefSetting.getApiUrl()) // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<CommonRes> call = apiService.postSummitCode(requestBodySummitCode);
        try {
            call.enqueue(new Callback<CommonRes>() {
                @Override
                public void onResponse(Call<CommonRes> call, Response<CommonRes> response) {
                    if (response.isSuccessful()) {
                        String res = response.body().getDetail().toString();
                        Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Failed to add ReasonCode. HTTP Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CommonRes> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to add ReasonCode. Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "postReasonCode: " + ex.getMessage());
        }
    }
    public interface PostReasonSolutionCallback {
        void onSuccess(String response);

        void onFailure(String errorMessage);
    }
    public static void postReasonSolutionFunc(String method, String tokenId, RequestBodyReasonSolutionCode requestBodyReasonSolutionCode,
                                              PostReasonSolutionCallback callback){

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
            Call<CommonRes> call;
            if("reason".equals(method)){
                call = apiService.postReasonCode(requestBodyReasonSolutionCode);
            }
            else{
                call = apiService.postSolutionCode(requestBodyReasonSolutionCode);
            }


            call.enqueue((new Callback<CommonRes>() {
                @Override
                public void onResponse(Call<CommonRes> call, Response<CommonRes> response) {
                    if (response.isSuccessful()) {
                        String res = response.body().getDetail().toString();
                        callback.onSuccess(res);
                    }else {
                        callback.onFailure("Failed to add ReasonCode. HTTP Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<CommonRes> call, Throwable t) {
                    callback.onFailure("Failed to add ReasonCode. Error: " + t.getMessage());
                }
            }));
        }
        catch (Exception e){
            Log.i("LOG_MSG", "postReasonCode: " + e.getMessage());
            callback.onFailure("Failed to add ReasonCode. Exception: " + e.getMessage());
        }
    }

    public interface getReasonSolutionCallback {
        void onSuccess(ReasonSolutionRes response);

        void onFailure(String errorMessage);
    }
   public static void getReasonSolutionFunc(String method, String tokenId, String EquipmentType, String ErrorCode,  getReasonSolutionCallback callback){
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
           Call<ReasonSolutionRes> call;
           if("reason".equals(method)){
               call = apiService.getReasonCode(EquipmentType, ErrorCode);
           }
           else{
               call = apiService.getSolutionCode(EquipmentType, ErrorCode);
           }


           call.enqueue((new Callback<ReasonSolutionRes>() {
               @Override
               public void onResponse(Call<ReasonSolutionRes> call, Response<ReasonSolutionRes> response) {
                   if (response.isSuccessful()) {
                       ReasonSolutionRes res = response.body();
                       callback.onSuccess(res);
                   }else {
                       callback.onFailure("Failed to add ReasonCode. HTTP Code: " + response.code());
                   }
               }

               @Override
               public void onFailure(Call<ReasonSolutionRes> call, Throwable t) {
                   callback.onFailure("Failed to add ReasonCode. Error: " + t.getMessage());
               }
           }));
       }
       catch (Exception e){
           Log.i("LOG_MSG", "postReasonCode: " + e.getMessage());
           callback.onFailure("Failed to add ReasonCode. Exception: " + e.getMessage());
       }
   }
}