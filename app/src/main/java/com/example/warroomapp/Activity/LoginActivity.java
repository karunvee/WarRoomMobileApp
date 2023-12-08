package com.example.warroomapp.Activity;
import static com.example.warroomapp.Activity.SignUpActivity.hideKeyboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LoginActivity extends AppCompatActivity {

    private static GlobalVariable globalVariable = new GlobalVariable();
    private interface ApiService{
        @POST("/user_login/")
        Call<LoginRes> login(@Body LoginData requestBody);
    }
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnOpenSignUp;
    private TextView txtResponseLogin;

    private ProgressBar progressBarLogin;
    private Dialog url_dialog;
    private LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPrefSetting = new SharedPreferencesSetting(this);

        url_dialog = new Dialog(this);
        url_dialog.setContentView(R.layout.dialog_url_setting);
        url_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        url_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        url_dialog.setCancelable(false);
        EditText txtUrlServer = url_dialog.findViewById(R.id.txtUrlSetting);
        Button btnSaveUrl = url_dialog.findViewById(R.id.btnSaveUrlSetting);
        Button btnCancelUrl = url_dialog.findViewById(R.id.btnCancelUrlSetting);
        txtUrlServer.setHint(sharedPrefSetting.getApiUrl().toString());

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtResponseLogin = findViewById(R.id.txtResponseLogin);
        btnOpenSignUp = findViewById(R.id.btnOpenSignUp);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        animationView = findViewById(R.id.autologin_animation);
        animationView.setAnimation("satellite_loading_animation.json");
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                hideKeyboard(LoginActivity.this);

                if(sharedPrefSetting.getApiUrl().equals("") || sharedPrefSetting.getApiUrl() == null){
                    url_dialog.show();
                }
                else {
                    if (isConnectedToWifi()) {
                        animationView.setVisibility(View.GONE);
                        animationView.pauseAnimation();
                        if (txtUsername.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                            // Display a warning message
                            txtResponseLogin.setVisibility(TextView.VISIBLE);
                            displayMessage(txtResponseLogin, R.drawable.incorrect_icon, "Username and password are required.", R.drawable.error_message_box, 3000);
                        } else {
                            LoginFunc(txtUsername.getText().toString(), txtPassword.getText().toString());
                            displayMessage(txtResponseLogin, R.drawable.correct_icon, "Login success!",  R.drawable.message_box, 2000);
                        }
                    }
                    else{
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        displayMessage(txtResponseLogin, R.drawable.incorrect_icon, "No connection", R.drawable.error_message_box, 4000);
                    }

                }
            }
        });

        btnOpenSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Button btnSetUrl = findViewById(R.id.buttonSettingURL);
        btnSetUrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                url_dialog.show();
                return false;
            }
        });

        btnCancelUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_dialog.dismiss();
            }
        });
        btnSaveUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String str = txtUrlServer.getText().toString();
                    sharedPrefSetting.saveApiUrl(str);
                    url_dialog.dismiss();
                    displayMessage(txtResponseLogin, R.drawable.correct_icon, "URL was saved!",  R.drawable.message_box, 2000);
                }
                catch (Exception ex) {
                    Log.i("LOG_MSG", "btnSaveUrl " + ex.getMessage());
                }
            }
        });

    }
    public void LoginFunc(String username ,String password){


        try{
            sharedPrefManager = new SharedPreferencesManager(getApplicationContext());
            sharedPrefSetting = new SharedPreferencesSetting(getApplicationContext());

            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
            btnLogin.setEnabled(false);
            btnOpenSignUp.setEnabled(false);
            progressBarLogin.setVisibility(ProgressBar.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(globalVariable.api_url + sharedPrefSetting.getApiUrl()) // Replace with your API's base URL
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            LoginData loginRequest = new LoginData(username, password, globalVariable.ad_server);
            apiService.login(loginRequest).enqueue(new Callback<LoginRes>() {
                @Override
                public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                    if (response.isSuccessful()) {
                        LoginRes loginResponse = response.body();

                        // Handle the successful login response here
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    intent.putExtra("LoginRes", loginResponse);

                        sharedPrefManager.saveUserData(
                                loginResponse.getToken(),
                                loginResponse.user.getId(),
                                loginResponse.user.getUsername(),
                                loginResponse.user.getName(),
                                loginResponse.user.getEmpNo(),
                                loginResponse.user.getDescription(),
                                loginResponse.user.getRemark(),
                                loginResponse.user.isUser(),
                                loginResponse.user.isStaff(),
                                loginResponse.user.getImage(),
                                loginResponse.user.getSkillPoint(),
                                loginResponse.user.getActionPeriod(),
                                loginResponse.user.getMachineQty()
                        );

//                    Toast.makeText(getApplicationContext(), loginResponse.getUser().getName() + "! Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle login error (e.g., invalid credentials, server error)

                        displayMessage(txtResponseLogin, R.drawable.incorrect_icon, "Login failed.",  R.drawable.error_message_box, 3000);
//                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    progressBarLogin.setVisibility(ProgressBar.INVISIBLE);
                    txtUsername.setEnabled(true);
                    txtPassword.setEnabled(true);
                    btnLogin.setEnabled(true);
                    btnOpenSignUp.setEnabled(true);
                }

                @Override
                public void onFailure(Call<LoginRes> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    progressBarLogin.setVisibility(ProgressBar.INVISIBLE);
                    txtUsername.setEnabled(true);
                    txtPassword.setEnabled(true);
                    btnLogin.setEnabled(true);
                    btnOpenSignUp.setEnabled(true);

                    Log.i("LOG_MSG", "animationView GONE" );
                }
            });
        }
        catch (Exception ex){
            displayMessage(txtResponseLogin, R.drawable.incorrect_icon, ex.getMessage(), R.drawable.error_message_box, 3000);
            Log.i("LOG_MSG", "LoginFunc " + ex.getMessage());
        }

    }

    private void displayMessage(TextView textView, int iconResource, String text, int ColorResource,int Delay){
        textView.setVisibility(View.VISIBLE);
//        textView.setBackgroundColor(getResources().getColor(Color));
        textView.setBackgroundResource(ColorResource);
        addIconToTextView(textView, iconResource, text);
        // Delay hiding the TextView for 3 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set the TextView to invisible after 3 seconds
                textView.setVisibility(View.INVISIBLE);
            }
        }, Delay); // 3000 milliseconds = 3 seconds
    }
    private void addIconToTextView(TextView textView, int iconResource, String text) {
        Drawable icon = ContextCompat.getDrawable(this, iconResource);

        if (icon != null) {
            // Set the bounds for the drawable (left, top, right, bottom)
//            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            icon.setBounds(0, 0, 40, 40);

            // Set the icon to appear to the left of the text
            textView.setCompoundDrawables(icon, null, null, null);
            textView.setCompoundDrawablePadding(15);
        }

        // Set the text
        textView.setText(text);
    }


    public boolean isConnectedToWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() &&
                    networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }

        return false;
    }

    private WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }
}
