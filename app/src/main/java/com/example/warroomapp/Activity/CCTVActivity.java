package com.example.warroomapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.source.MediaSource;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;

public class CCTVActivity extends AppCompatActivity{

    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesMachine sharedPreferencesMachine;
    private Button btnBackToMachine;
    private Button btnRefresh;
    private String videoUrl;

    private WebView CameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctvactivity);

        sharedPrefSetting = new SharedPreferencesSetting(getApplicationContext());
        sharedPreferencesMachine = new SharedPreferencesMachine(getApplicationContext());

        videoUrl = "https://thwgrwarroom.deltaww.com:8070/camera_view/"+ sharedPreferencesMachine.getCameraIp1()+ "/";
//        videoUrl = "https://thwgrwarroom.deltaww.com:8070/stream_camera/10.195.50.13/";
        Log.i("LOG_MSG", videoUrl);
        CameraView = findViewById(R.id.CameraView);
        CameraView.setWebViewClient(new WebViewClient());
        CameraView.loadUrl(videoUrl);
        WebSettings webSettings = CameraView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        btnBackToMachine = findViewById(R.id.btnBackMachine);
        btnBackToMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CCTVActivity.this, MachineActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnRefresh = findViewById(R.id.btnViewRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraView.reload();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}