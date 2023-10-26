package com.example.warroomapp.Activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;
import com.google.gson.annotations.SerializedName;

import java.text.MessageFormat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private interface ApiService{
        @GET("user_info/{userId}/")
        Call<UserInfoRes> getUserInfo( @Path("userId") int userId);
    }
    private SharedPreferencesManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new SharedPreferencesManager(this);

        Toast.makeText(getApplicationContext(), sharedPrefManager.getTokenId() + " @ " + sharedPrefManager.getUserId(), Toast.LENGTH_SHORT).show();
        int delayMillis = 1000; // Adjust this value as needed

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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenId))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.234.232.193:8000/") // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        try{
            Call<UserInfoRes> call = apiService.getUserInfo(userId);

            call.enqueue(new Callback<UserInfoRes>() {
                @Override
                public void onResponse(Call<UserInfoRes> call, Response<UserInfoRes> response) {
                    if (response.isSuccessful()) {
                        UserInfoRes apiResponse = response.body();
                        // Handle the response data here
                        Toast.makeText(getApplicationContext(), apiResponse.getUser().getName() + "! GET" , Toast.LENGTH_SHORT).show();

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
                                apiResponse.getUser().getImage()
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
                    Log.i("LOG_MSG", "Network error: " + t.toString());
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("LOG_MSG", "getUserInfo: " + e.toString());
        }

    }
}

