package com.example.warroomapp.Activity;

import static android.app.PendingIntent.getActivity;

import com.example.warroomapp.Fragment.AllJobsFragment;
import com.example.warroomapp.NotificationService;
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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warroomapp.Fragment.FavoriteFragment;
import com.example.warroomapp.Fragment.ProfileFragment;
import com.example.warroomapp.Fragment.SettingFragment;
import com.example.warroomapp.Fragment.TasksFragment;
import com.example.warroomapp.R;
import com.example.warroomapp.TaskCardAdapter;
import com.example.warroomapp.WebSocketViewModel;
import com.example.warroomapp.databinding.ActivityHomeBinding;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskCardAdapter taskAdapter;
    private ArrayList<Integer> StoredTasks = new ArrayList<>();
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
//        startService();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new TasksFragment());
//        Intent intent = getIntent();
//        loginResponse = (LoginRes) intent.getSerializableExtra("LoginRes");

        sharedPrefManager = new SharedPreferencesManager(getApplicationContext());
        if(sharedPrefManager.getUserId() == 0){
            Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        binding.btnNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.tasks_menu) {
                TasksFragment tasksFragment = new TasksFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(TasksFragment.ARG_JOB_CONTAINERS, (ArrayList<? extends Parcelable>) jobContainers);
                tasksFragment.setArguments(bundle);
                replaceFragment(tasksFragment);
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

//        WebSocketViewModel viewModel = new ViewModelProvider(this).get(WebSocketViewModel.class);
//        viewModel.connectWebSocket("ws://10.234.232.193:8000/ws/job_and_member/");
                connectWebSocket();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mWebSocketClient.close();
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

    public void StoreJobArray(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray JobTaskArray = json.getJSONArray("jobtask_info");
            ArrayList<Integer> CurrentTasks = new ArrayList<Integer>();

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
                CurrentTasks.add(id);
                if(!StoredTasks.contains(id)){
                    StoredTasks.add(id);
                    Log.i("LOG_MSG", "JobTaskArray jobIndex " + StoredTasks.get(i) );
                    jobContainers.add(new JobTaskParameter(id, plant, line, machine,
                            equipId, name, description, typeof,
                            responder_member, start_date, ended_date));

                    createNotification(jobContainers.get(i));
//                    recyclerView = findViewById(R.id.allJob_RecyclerView);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
//                    taskAdapter = new TaskCardAdapter(jobContainers);
//                    recyclerView.setAdapter(taskAdapter);
                }
            }
            Log.i("LOG_MSG", "JobTaskArray CurrentTasks " + CurrentTasks);
            Log.i("LOG_MSG", "JobTaskArray StoredTasks " + StoredTasks);

            ArrayList<Integer> difference = new ArrayList<>(StoredTasks);
            difference.removeAll(CurrentTasks);
            Log.i("LOG_MSG", "JobTaskArray difference " + difference);


            difference.forEach((_id) -> {
                Log.i("LOG_MSG", "StoredTasks removing..." + _id);
                for (JobTaskParameter job : jobContainers) {
                    if (job.getId() == _id && taskAdapter != null) {

                        taskAdapter.removeItemById(_id);
                        Log.i("LOG_MSG", "JobTaskArray jobContainers was removed" + job.getId() );
                        jobContainers.remove(job);
                        StoredTasks.removeIf(item -> item == _id);
                        break;
                    }
                }

            });

            recyclerView = findViewById(R.id.allJob_RecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            taskAdapter = new TaskCardAdapter(jobContainers);
            recyclerView.setAdapter(taskAdapter);
//            taskAdapter.updateAdapter(jobContainers);

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
