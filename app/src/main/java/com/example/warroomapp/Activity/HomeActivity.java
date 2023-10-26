package com.example.warroomapp.Activity;

import static android.app.PendingIntent.getActivity;

import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.JobTaskParameter;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warroomapp.Fragment.FavoriteFragment;
import com.example.warroomapp.Fragment.ProfileFragment;
import com.example.warroomapp.Fragment.SettingFragment;
import com.example.warroomapp.Fragment.TasksFragment;
import com.example.warroomapp.R;
import com.example.warroomapp.TaskCardAdapter;
import com.example.warroomapp.databinding.ActivityHomeBinding;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskCardAdapter taskAdapter;
    private ArrayList<Integer> JobIndex = new ArrayList<Integer>();
    private  ArrayList<JobTaskParameter> jobContainers = new ArrayList<JobTaskParameter>();
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
                        Log.i("LOG_MSG", "message :\n" + message);
                        StoreJobArray(message);
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

    private void StoreJobArray(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray JobTaskArray = json.getJSONArray("jobtask_info");

            for (int i = 0; i < JobTaskArray.length(); i++) {
                JSONObject JobList = JobTaskArray.getJSONObject(i);
                int id = JobList.getInt("pk");
                String plant = JobList.getString("plant");
                String line = JobList.getString("line");
                String machine = JobList.getString("machine");
                String equipId = JobList.getString("equipId");
                String name = JobList.getString("name");
                String description = JobList.getString("description");
                String typeof = JobList.getString("typeof");
                String start_date = JobList.getString("issued_date");
                String ended_date = JobList.getString("ended_date");
                String responder_member = JobList.getString("responder_member");

                if(!JobIndex.contains(id)){
                    JobIndex.add(id);
                    Log.i("LOG_MSG", "JobTaskArray jobIndex " + JobIndex.get(i) );
                    jobContainers.add(new JobTaskParameter(id, plant, line, machine,
                            equipId, name, description, typeof,
                            responder_member, start_date, ended_date));

                    createNotification(jobContainers.get(i));

                    recyclerView = findViewById(R.id.allJob_RecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    taskAdapter = new TaskCardAdapter(jobContainers);
                    recyclerView.setAdapter(taskAdapter);
                }
            }

        }
        catch (Exception ex){
            Log.i("LOG_MSG", "StoreJobArray " + ex.getMessage());
        }
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
    private void createNotification(JobTaskParameter jobTaskParameter) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_arrow_right_color2_36)
//                .setContentTitle("["+ jobTaskParameter.getLine() +"] "+ jobTaskParameter.getMachine())
                .setContentTitle(String.format("[%s] %s", jobTaskParameter.getLine(), jobTaskParameter.getMachine()))
                .setContentText(jobTaskParameter.getJob() + ", " + jobTaskParameter.getDescription())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("LoginRes", loginResponse);

        PendingIntent pendingIntent = getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
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
        notificationManager.notify(jobTaskParameter.getId(),builder.build());
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
