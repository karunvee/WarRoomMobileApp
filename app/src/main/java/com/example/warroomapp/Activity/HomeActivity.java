package com.example.warroomapp.Activity;

import static android.app.PendingIntent.getActivity;

import static com.example.warroomapp.Adaptor.ImageCustom.getRoundedCornerBitmap;

import com.example.warroomapp.Activity.Class.ActivityUtils;
import com.example.warroomapp.Adaptor.ViewPagerAdapter;
import com.example.warroomapp.Fragment.AllJobsFragment;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.NotificationService;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.JobTaskParameter;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import androidx.viewpager.widget.ViewPager;

import com.example.warroomapp.Fragment.FavoriteFragment;
import com.example.warroomapp.Fragment.ProfileFragment;
import com.example.warroomapp.Fragment.SettingFragment;
import com.example.warroomapp.Fragment.TasksFragment;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesSetting;
import com.example.warroomapp.TaskCardAdapter;
import com.example.warroomapp.WebSocketViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public class HomeActivity extends AppCompatActivity implements TasksFragment.OnFunctionCallListener {
    private static GlobalVariable globalVariable = new GlobalVariable();

    public class RequestBodyPersonToJob {
        @SerializedName("jid")
        private String jid;

        @SerializedName("uid")
        private String uid;

        public RequestBodyPersonToJob(String jid, String uid) {
            this.jid = jid;
            this.uid = uid;
        }
    }
    public class RequestBodyUserStatus{
        @SerializedName("id")
        private String uid;
        @SerializedName("status")
        private String status;
        public RequestBodyUserStatus(String uid, String status){
            this.uid = uid;
            this.status = status;
        }
    }
    private interface ApiService{
        @GET("/job_update/")
        Call<CommonRes> getJobUpdate();

        @POST("/UpdatePICtoJob/")
        Call<CommonRes> postPersonToJob (@Body RequestBodyPersonToJob requestBodyPersonToJob);

        @POST("/user_update_status/")
        Call<CommonRes> postUserStatus (@Body RequestBodyUserStatus requestBodyUserStatus);
    }
    private RecyclerView recyclerView_allJob;
    private RecyclerView recyclerView_myJob;
    private TaskCardAdapter AllTaskAdapter;
    private TaskCardAdapter MyTaskAdapter;
    private  ArrayList<JobTaskParameter> EntireJobContainers = new ArrayList<>();
    private  ArrayList<JobTaskParameter> AllJobContainers = new ArrayList<>();
    private  ArrayList<JobTaskParameter> MyJobContainers = new ArrayList<>();
    private ArrayList<Integer> Stored_AllTasks = new ArrayList<>();
    private ArrayList<Integer> Stored_MyTasks = new ArrayList<>();
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private WebSocketClient mWebSocketClient;
    private LoginRes loginResponse;
    private String CHANNEL_ID = "task_channel";
    private Integer previous_fragmentId = 0;

    private ProgressBar progress;
    private ProgressBarAnimation anim;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private Dialog taskConfirm_dialog;

    private boolean HomeActivityAlive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.i("LOG_MSG", "HomeActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NotificationPermission();

        sharedPrefManager = new SharedPreferencesManager(getApplicationContext());
        sharedPrefSetting = new SharedPreferencesSetting(getApplicationContext());
        if(sharedPrefManager.getUserId() == 0){
            Toast.makeText(getApplicationContext(), "Please sign-in!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        progress = findViewById(R.id.progressBar);
        anim = new ProgressBarAnimation(progress, 0, 100);
        anim.setDuration(1500);

        MenuListener();
        HomeActivityAlive = true;
//        WebSocketViewModel viewModel = new ViewModelProvider(this).get(WebSocketViewModel.class);
//        viewModel.connectWebSocket("ws://10.234.232.193:8000/ws/job_and_member/");
        try{
            connectWebSocket();
//            getJobUpdate(sharedPrefManager.getTokenId());
        }catch (Exception e){
            Log.i("LOG_MSG", "Exception" + e.getMessage());
        }
    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
//            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        postUserStatus(sharedPrefManager.getTokenId(), "Online");
        Log.i("LOG_MSG", "HomeActivity was onResume");
    }
    @Override
    protected void onStart() {
        super.onStart();
        postUserStatus(sharedPrefManager.getTokenId(), "Online");
        Log.i("LOG_MSG", "HomeActivity was onStart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        HomeActivityAlive = false;
        postUserStatus(sharedPrefManager.getTokenId(), "Idle");
        Log.i("LOG_MSG", "HomeActivity was onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        HomeActivityAlive = false;
        postUserStatus(sharedPrefManager.getTokenId(), "Offline");
        Log.i("LOG_MSG", "HomeActivity was destroy");
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(globalVariable.websocket_url + sharedPrefSetting.getApiUrl() +":8001/ws/job_and_member/");
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
                        progress.setVisibility(View.VISIBLE);
                        progress.startAnimation(anim);
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
            ArrayList<Integer> Current_AllTasks = new ArrayList<Integer>();
            ArrayList<Integer> Current_MyTasks = new ArrayList<Integer>();

            for (int i = 0; i < JobTaskArray.length(); i++) {
                JSONObject JobList = JobTaskArray.getJSONObject(i);
                int id = JobList.getInt("pk");
                String plant = JobList.getString("plant");
                String line = JobList.getString("line");
                String machine = JobList.getString("machine");
                String equipCode = JobList.getString("equipCode");
                String equipType = JobList.getString("equipType");
                String cameraIp1 = JobList.getString("cameraIp1");
                String cameraIp2 = JobList.getString("cameraIp2");
                String name = JobList.getString("name");
                String description = JobList.getString("description");
                String typeof = JobList.getString("typeof");
                String start_date = JobList.getString("issued_date");
                String ended_date = JobList.getString("ended_date");
                String responder_member = JobList.getString("responder_member").toLowerCase();

                JobTaskParameter job = new JobTaskParameter(id, plant, line, machine,
                        equipCode, equipType, cameraIp1, cameraIp2, name, description, typeof,
                        responder_member, start_date, ended_date);

                if(ended_date.equals("-")){
                    if(responder_member.equals(sharedPrefManager.getUsername())){
                        Current_MyTasks.add(id);
                        if(!Stored_MyTasks.contains(id)){
                            Stored_MyTasks.add(id);
                            MyJobContainers.add(job);
                            EntireJobContainers.add(job);
                            if(!HomeActivityAlive){
                                createNotification(EntireJobContainers.get(i));
                            }
                        }
                    }
                    else if(responder_member.equals("non-assigned")){
                        Current_AllTasks.add(id);
                        if(!Stored_AllTasks.contains(id)){
                            Stored_AllTasks.add(id);
                            AllJobContainers.add(job);
                            EntireJobContainers.add(job);
                            if(!HomeActivityAlive){
                                createNotification(EntireJobContainers.get(i));
                            }
                        }
                    }
                }else{  EntireJobContainers.add(job); }

            }
            ArrayList<Integer> difference_MyTasks = new ArrayList<>(Stored_MyTasks);
            difference_MyTasks.removeAll(Current_MyTasks);

            difference_MyTasks.forEach((_id) -> {
                for (JobTaskParameter job : MyJobContainers) {
                    if (job.getId() == _id && MyTaskAdapter != null) {

                        MyTaskAdapter.removeItemById(_id);
                        MyJobContainers.remove(job);
                        Stored_MyTasks.removeIf(item -> item == _id);
                        break;
                    }
                }
            });

            ArrayList<Integer> difference_AllTasks = new ArrayList<>(Stored_AllTasks);
            difference_AllTasks.removeAll(Current_AllTasks);

            difference_AllTasks.forEach((_id) -> {
                for (JobTaskParameter job : AllJobContainers) {
                    if (job.getId() == _id && AllTaskAdapter != null) {

                        AllTaskAdapter.removeItemById(_id);
                        AllJobContainers.remove(job);
                        Stored_AllTasks.removeIf(item -> item == _id);
                        break;
                    }
                }
            });

            recyclerView_allJob = findViewById(R.id.allJob_RecyclerView);
            recyclerView_myJob = findViewById(R.id.myJob_RecyclerView);

            AllTaskAdapter = new TaskCardAdapter(AllJobContainers);
            MyTaskAdapter = new TaskCardAdapter(MyJobContainers);

            AllTaskAdapter.setOnItemClickListener(new TaskCardAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    JobTaskParameter clickedTask = AllJobContainers.get(position);
                    SharedPreferencesMachine sharedPreferencesMachine = new SharedPreferencesMachine(getApplicationContext());
                    sharedPreferencesMachine.saveMachineData(
                            clickedTask.getId(),
                            clickedTask.getPlant(),
                            clickedTask.getLine(),
                            clickedTask.getMachine(),
                            clickedTask.getEquipId(),
                            clickedTask.getEquipType(),
                            clickedTask.getCameraIp1(),
                            clickedTask.getCameraIp2(),
                            clickedTask.getJob(),
                            clickedTask.getDescription(),
                            clickedTask.getTypeOf(),
                            clickedTask.getStartDate(),
                            clickedTask.getEndDate()
                            );


                    taskConfirm_dialog = new Dialog(HomeActivity.this);
                    taskConfirm_dialog.setContentView(R.layout.dialog_confirm_task);
                    taskConfirm_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    taskConfirm_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.dialog_background_corner_top));
                    taskConfirm_dialog.getWindow().setGravity(Gravity.BOTTOM);
                    taskConfirm_dialog.setCancelable(false);
                    taskConfirm_dialog.show();

                    try{
                        Intent intentMachineActivity = new Intent(HomeActivity.this, MachineActivity.class);

                        Button btnConfirmJob = taskConfirm_dialog.findViewById(R.id.btnConfirmJob);
                        Button btnCancelJob = taskConfirm_dialog.findViewById(R.id.btnCancelJob);

                        TextView txtMachineNameOfConfirm = taskConfirm_dialog.findViewById(R.id.txtMachineNameOfConfirm);
                        txtMachineNameOfConfirm.setText(clickedTask.getMachine());

                        btnConfirmJob.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PostPersonToJobFunc(String.valueOf(clickedTask.getId()), String.valueOf(sharedPrefManager.getUserId()));
                                Toast.makeText(getApplicationContext(), "Job Id: " + clickedTask.getId() + "\nUser Id: " + sharedPrefManager.getUserId(), Toast.LENGTH_SHORT).show();
                                taskConfirm_dialog.dismiss();
                                startActivity(intentMachineActivity);
                            }
                        });
                        btnCancelJob.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                taskConfirm_dialog.dismiss();
                            }
                        });
                    }catch (Exception e){
                        Log.i("LOG_MSG", "taskConfirm_dialog " + e.getMessage());
                    }

                }
            });
            MyTaskAdapter.setOnItemClickListener(new TaskCardAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    JobTaskParameter clickedTask = MyJobContainers.get(position);
                    SharedPreferencesMachine sharedPreferencesMachine = new SharedPreferencesMachine(getApplicationContext());
                    sharedPreferencesMachine.saveMachineData(
                            clickedTask.getId(),
                            clickedTask.getPlant(),
                            clickedTask.getLine(),
                            clickedTask.getMachine(),
                            clickedTask.getEquipId(),
                            clickedTask.getEquipType(),
                            clickedTask.getCameraIp1(),
                            clickedTask.getCameraIp2(),
                            clickedTask.getJob(),
                            clickedTask.getDescription(),
                            clickedTask.getTypeOf(),
                            clickedTask.getStartDate(),
                            clickedTask.getEndDate()
                    );
                    Intent intentMachineActivity = new Intent(HomeActivity.this, MachineActivity.class);
                    startActivity(intentMachineActivity);
                }
            });

            recyclerView_allJob.setLayoutManager(new LinearLayoutManager(this));
            recyclerView_myJob.setLayoutManager(new LinearLayoutManager(this));

            recyclerView_allJob.setAdapter(AllTaskAdapter);
            recyclerView_myJob.setAdapter(MyTaskAdapter);

        }
        catch (Exception ex){
            Log.i("LOG_MSG", "StoreJobArray " + ex.getMessage());
        }
    }
    private void PostPersonToJobFunc(String jobId, String userId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(globalVariable.api_url + sharedPrefSetting.getApiUrl()) // Replace with your API's base URL
                .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        RequestBodyPersonToJob requestBodyPersonToJob = new RequestBodyPersonToJob(jobId, userId);
        apiService.postPersonToJob(requestBodyPersonToJob).enqueue(new Callback<CommonRes>() {
            @Override
            public void onResponse(Call<CommonRes> call, Response<CommonRes> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Assigned successful", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CommonRes> call, Throwable t) {
            }
        });
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
//        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error_icon);
        int iconBackgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.red);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.error_circle_regular_36_white)
                .setColor(iconBackgroundColor)
//                .setLargeIcon(iconBitmap)
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
    private void replaceFragment(Fragment fragment, int Id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if( Id > previous_fragmentId){
            fragmentTransaction
                    .setCustomAnimations(R.anim.fragment_right_to_left, R.anim.fragmen_exit_right_to_left, R.anim.fragment_left_to_right, R.anim.fragment_exit_left_to_right)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .replace(R.id.frame_home, fragment);

        } else if (Id < previous_fragmentId) {
            fragmentTransaction
                    .setCustomAnimations(R.anim.fragment_left_to_right, R.anim.fragment_exit_left_to_right, R.anim.fragment_right_to_left, R.anim.fragmen_exit_right_to_left)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .replace(R.id.frame_home, fragment);
        }
        fragmentTransaction.commit();

        previous_fragmentId = Id;
    }

    public void postUserStatus(String tokenId, String Status){
        Integer uid = sharedPrefManager.getUserId();
        RequestBodyUserStatus requestBodyUserStatus = new RequestBodyUserStatus(
                uid.toString(),
                Status
        );

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
            Call<CommonRes> call = apiService.postUserStatus(requestBodyUserStatus);

            call.enqueue((new Callback<CommonRes>() {
                @Override
                public void onResponse(Call<CommonRes> call, Response<CommonRes> response) {
                    if (response.isSuccessful()) {
                        Log.i("LOG_MSG", response.body().detail);
                    }
                }

                @Override
                public void onFailure(Call<CommonRes> call, Throwable t) {
                    Log.i("LOG_MSG", "Updating user status: " + t.toString());
                }
            }));
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("LOG_MSG", "GetJobTask: " + e.toString());
        }
    }
    public void getJobUpdate(String tokenId){
        ProgressBarAnimation animChild = new ProgressBarAnimation(progress, 0, 20);
        animChild.setDuration(500);
        progress.setVisibility(View.VISIBLE);
        progress.startAnimation(animChild);

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
            Call<CommonRes> call = apiService.getJobUpdate();

            call.enqueue((new Callback<CommonRes>() {
                @Override
                public void onResponse(Call<CommonRes> call, Response<CommonRes> response) {
                    if (response.isSuccessful()) {
                        ProgressBarAnimation animChild2 = new ProgressBarAnimation(progress, 20, 50);
                        animChild2.setDuration(1000);
                        progress.setVisibility(View.VISIBLE);
                        progress.startAnimation(animChild2);
//                        Toast.makeText(getApplicationContext(), "Job tasks update successfully!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CommonRes> call, Throwable t) {

                }
            }));
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("LOG_MSG", "GetJobTask: " + e.toString());
        }
    }
    public void MenuListener(){
        try{
            viewPager = findViewById(R.id.frame_home);
            ViewPagerAdapter ViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(ViewPagerAdapter);
            viewPager.setOffscreenPageLimit(4);

            bottomNavigationView = findViewById(R.id.btnNavigationView);
            bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    if (item.getItemId() == R.id.tasks_menu) {
                        viewPager.setCurrentItem(0, false);
                        return true;
                    } else if (item.getItemId() == R.id.favorite_menu) {
                        viewPager.setCurrentItem(1, false);
                        return true;
                    } else if (item.getItemId() == R.id.chat_menu) {
                        viewPager.setCurrentItem(2, false);
                        return true;
                    } else if (item.getItemId() == R.id.profile_menu) {
                        viewPager.setCurrentItem(3, false);
                        return true;
                    } else if (item.getItemId() == R.id.setting_menu) {
                        viewPager.setCurrentItem(4, false);
                        return true;
                    }
                    return false;
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            bottomNavigationView.setSelectedItemId(R.id.tasks_menu);
                            break;
                        case 1:
                            bottomNavigationView.setSelectedItemId(R.id.favorite_menu);
                            break;
                        case 2:
                            bottomNavigationView.setSelectedItemId(R.id.chat_menu);
                            break;
                        case 3:
                            bottomNavigationView.setSelectedItemId(R.id.profile_menu);
                            break;
                        case 4:
                            bottomNavigationView.setSelectedItemId(R.id.setting_menu);
                            break;
                    }
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

        }catch (Exception e){
            Log.i("LOG_MSG", "binding" + e.getMessage());
        }

    }
    public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);

            if (value >= 100) {
                // Hide the ProgressBar when the target value is reached
                progressBar.setVisibility(View.GONE);
            }
        }

    }
}
