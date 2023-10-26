package com.example.warroomapp.Activity;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.warroomapp.R;
import com.google.gson.annotations.SerializedName;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SignUpActivity extends AppCompatActivity {

    public interface ApiService{
        @POST("sign_up_api/")
        Call<SignupRes> signup(@Body LoginData requestBody);
    }
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtDomain;
    private Button btnSignUp;
    private  Button btnBackToLogin;
    private ProgressBar progressBarSignUp;
    private TextView txtResponseSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtUsername = findViewById(R.id.txtUsernameSignUp);
        txtPassword = findViewById(R.id.txtPasswordSignUp);
        txtDomain = findViewById(R.id.txtDomainSigUp);
        txtDomain.setEnabled(false);
        btnSignUp = findViewById(R.id.btnSignup);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        progressBarSignUp = findViewById(R.id.progressBarSignUp);
        txtResponseSignUp = findViewById(R.id.txtResponseSignUp);

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtUsername.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                    // Display a warning message
                    txtResponseSignUp.setVisibility(TextView.VISIBLE);
                    displayMessage(txtResponseSignUp, R.drawable.incorrect_icon, "Username and password are required.", R.drawable.error_message_box, 3000);
                } else {
                    SignUp(txtUsername.getText().toString(), txtPassword.getText().toString());
                }
            }
        }));
    }

    public void SignUp(String username ,String password){

        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        btnSignUp.setEnabled(false);
        btnBackToLogin.setEnabled(false);
        progressBarSignUp.setVisibility(ProgressBar.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.234.232.193:8000/") // Replace with your API's base URL
                .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
                .build();

        SignUpActivity.ApiService apiService = retrofit.create(SignUpActivity.ApiService.class);

        LoginData loginRequest = new LoginData(username, password, "delta.corp");

        apiService.signup(loginRequest).enqueue(new Callback<SignupRes>() {
            @Override
            public void onResponse(Call<SignupRes> call, Response<SignupRes> response) {
                if (response.isSuccessful()) {
                    SignupRes signupResponse = response.body();
                    if(signupResponse.status.toString().equals("success")){
                        // Handle the successful login response here
                        displayMessage(txtResponseSignUp, R.drawable.correct_icon, "Sign up successfully",  R.drawable.message_box, 2000);

                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        displayMessage(txtResponseSignUp, R.drawable.incorrect_icon, signupResponse.detail,  R.drawable.error_message_box, 3000);
                    }

                } else {
                    // Handle login error (e.g., invalid credentials, server error)

                    displayMessage(txtResponseSignUp, R.drawable.incorrect_icon, "Login failed.",  R.drawable.error_message_box, 3000);
//                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
                progressBarSignUp.setVisibility(ProgressBar.INVISIBLE);
                txtUsername.setEnabled(true);
                txtPassword.setEnabled(true);
                btnSignUp.setEnabled(true);
                btnBackToLogin.setEnabled(true);
            }

            @Override
            public void onFailure(Call<SignupRes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                progressBarSignUp.setVisibility(ProgressBar.INVISIBLE);
                txtUsername.setEnabled(true);
                txtPassword.setEnabled(true);
                btnSignUp.setEnabled(true);
                btnBackToLogin.setEnabled(true);
            }
        });
    }

    private void displayMessage(TextView textView, int iconResource, String text, int ColorResource, int Delay){
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

    public  class  SignupRes{
        @SerializedName("status")
        private String status;

        @SerializedName("detail")
        private String detail;

        public String getStatus() {
            return status;
        }

        public String getDetail() {
            return detail;
        }
    }
}
