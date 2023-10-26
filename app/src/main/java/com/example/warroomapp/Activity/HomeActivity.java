package com.example.warroomapp.Activity;

import com.example.warroomapp.SharedPreferencesManager;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warroomapp.Fragment.FavoriteFragment;
import com.example.warroomapp.Fragment.ProfileFragment;
import com.example.warroomapp.Fragment.SettingFragment;
import com.example.warroomapp.Fragment.TasksFragment;
import com.example.warroomapp.R;
import com.example.warroomapp.databinding.ActivityHomeBinding;
import com.example.warroomapp.databinding.ActivityMainBinding;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
public class HomeActivity extends AppCompatActivity {
    private SharedPreferencesManager sharedPrefManager;
    private WebSocketClient mWebSocketClient;
    private LoginRes loginResponse;
    private String CHANNEL_ID = "task_channel";
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        NotificationPermission();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new TasksFragment());
//        Intent intent = getIntent();
//        loginResponse = (LoginRes) intent.getSerializableExtra("LoginRes");

        sharedPrefManager = new SharedPreferencesManager(getApplicationContext());

        binding.btnNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.tasks_menu) {
                replaceFragment(new TasksFragment());
            } else if (item.getItemId() == R.id.favorite_menu) {
                replaceFragment(new FavoriteFragment());
            } else if (item.getItemId() == R.id.profile_menu) {
                ProfileFragment profileFragment = new ProfileFragment();
                profileDataUpdate(profileFragment, sharedPrefManager);
                replaceFragment(profileFragment);
            } else if (item.getItemId() == R.id.setting_menu) {
                replaceFragment(new SettingFragment());
            }
            return true;
        });

        connectWebSocket();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.close();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://10.234.232.193:8000/ws/job_and_member/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        createNotification(loginResponse);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void NotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
    private void createNotification(LoginRes loginResponse) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_arrow_right_color2_36)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("LoginRes", loginResponse);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0,builder.build());
    }
    private void profileDataUpdate(ProfileFragment profileFragment, SharedPreferencesManager sharedPrefManager){
        Bundle bundle = new Bundle();
        bundle.putString("token", sharedPrefManager.getTokenId());
        bundle.putInt("id", sharedPrefManager.getUserId());
        bundle.putString("username", sharedPrefManager.getUsername());
        bundle.putString("name", sharedPrefManager.getName());
        bundle.putString("emp_no", sharedPrefManager.getEmpNo());
        bundle.putString("description", sharedPrefManager.getDescription());
        bundle.putString("image", sharedPrefManager.getImage());
        profileFragment.setArguments(bundle);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_home, fragment);
        fragmentTransaction.commit();
    }


}
