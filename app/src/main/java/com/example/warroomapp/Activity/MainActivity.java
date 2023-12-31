package com.example.warroomapp.Activity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.NotificationService;
import com.example.warroomapp.R;
import com.example.warroomapp.Restarter;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {
    private static GlobalVariable globalVariable = new GlobalVariable();
    Intent mServiceIntent;
    private NotificationService notificationService;
    public interface ApiService{
        @GET("/user_info/{userId}/")
        Call<UserInfoRes> getUserInfo(@Path("userId") int userId);
    }
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new SharedPreferencesManager(this);
        sharedPrefSetting = new SharedPreferencesSetting(this);

        notificationService = new NotificationService();
        mServiceIntent = new Intent(this, notificationService.getClass());
        if (!isMyServiceRunning(notificationService.getClass())) {
            startService(mServiceIntent);
        }

        Toast.makeText(getApplicationContext(), sharedPrefManager.getTokenId() + " @ " + sharedPrefManager.getUserId(), Toast.LENGTH_SHORT).show();
        int delayMillis = 2500; // Adjust this value as needed

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPrefManager.getTokenId() != ""){
                    getUserInfoToUpdate(sharedPrefManager.getTokenId(), sharedPrefManager.getUserId());
                }
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, delayMillis);
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

            ApiService apiService = retrofit.create(ApiService.class);
            Call<UserInfoRes> call = apiService.getUserInfo(userId);

            call.enqueue(new Callback<UserInfoRes>() {
                @Override
                public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                    if (response.isSuccessful()) {
                        UserInfoRes apiResponse = response.body();
                        // Handle the response data here
                        Toast.makeText(getApplicationContext(), "Welcome! "+ apiResponse.getUser().getName() , Toast.LENGTH_SHORT).show();

                        sharedPrefManager.saveUserData(
                                tokenId,
                                userId,
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
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Handle the error
                        Toast.makeText(getApplicationContext(), "API request failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserInfoRes> call, Throwable t) {
                    // Handle network or other errors
                    Toast.makeText(getApplicationContext(), "Network error: " + t.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("LOG_MSG", "Network error: " + t.getMessage());
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("LOG_MSG", "getUserInfo: " + e.getMessage());
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}

