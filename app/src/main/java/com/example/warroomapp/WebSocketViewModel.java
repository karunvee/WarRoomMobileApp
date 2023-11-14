package com.example.warroomapp;

import static android.app.PendingIntent.getActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warroomapp.Activity.HomeActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

public class WebSocketViewModel extends ViewModel {
    private String CHANNEL_ID = "task_channel";
    private WebSocketClient webSocketClient;
    private Context applicationContext;
    private RecyclerView recyclerView;
    private TaskCardAdapter taskAdapter;
    private ArrayList<Integer> JobIndex = new ArrayList<Integer>();
    private  ArrayList<JobTaskParameter> jobContainers = new ArrayList<JobTaskParameter>();
    private SharedPreferencesManager sharedPrefManager;

    public WebSocketViewModel(Context context) {
        applicationContext = context;
    }
    public void connectWebSocket(String serverUrl) {
        // Initialize and connect the WebSocket
        URI serverUri = URI.create(serverUrl);
        webSocketClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                webSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("LOG_MSG", "message :\n" + message);
//                StoreJobArray(message);
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
        webSocketClient.connect();
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
    }

//    public void StoreJobArray2(String jsonString){
//        try {
//            JSONObject json = new JSONObject(jsonString);
//            JSONArray JobTaskArray = json.getJSONArray("jobtask_info");
//            ArrayList<Integer> CurrentTasks = new ArrayList<Integer>();
//
//            for (int i = 0; i < JobTaskArray.length(); i++) {
//                JSONObject JobList = JobTaskArray.getJSONObject(i);
//                int id = JobList.getInt("pk");
//                String plant = JobList.getString("plant");
//                String line = JobList.getString("line");
//                String machine = JobList.getString("machine");
//                String equipId = JobList.getString("equipId");
//                String name = JobList.getString("name");
//                String description = JobList.getString("description");
//                String typeof = JobList.getString("typeof");
//                String start_date = JobList.getString("issued_date");
//                String ended_date = JobList.getString("ended_date");
//                String responder_member = JobList.getString("responder_member").toLowerCase();
//                CurrentTasks.add(id);
//                if(!StoredTasks.contains(id)){
//                    StoredTasks.add(id);
//                    JobTaskParameter job = new JobTaskParameter(id, plant, line, machine,
//                            equipId, name, description, typeof,
//                            responder_member, start_date, ended_date);
//
//                    if(responder_member.equals(sharedPrefManager.getUsername())){
//                        Log.i("LOG_MSG", "JobTaskArray my jobIndex " + StoredTasks.get(i) + "..." + responder_member + "..." + sharedPrefManager.getUsername());
//                        MyJobContainers.add(job);
//                    }
//                    else{
//                        Log.i("LOG_MSG", "JobTaskArray All jobIndex " + StoredTasks.get(i) );
//                        AllJobContainers.add(job);
//                    }
//                    EntireJobContainers.add(job);
//
//                    createNotification(EntireJobContainers.get(i));
//
//                }
//            }
//            Log.i("LOG_MSG", "JobTaskArray CurrentTasks " + CurrentTasks);
//            Log.i("LOG_MSG", "JobTaskArray StoredTasks " + StoredTasks);
//
//            ArrayList<Integer> difference = new ArrayList<>(StoredTasks);
//            difference.removeAll(CurrentTasks);
//            Log.i("LOG_MSG", "JobTaskArray difference " + difference);
//
//            difference.forEach((_id) -> {
//                Log.i("LOG_MSG", "StoredTasks removing..." + _id);
//                for (JobTaskParameter job : AllJobContainers) {
//                    if (job.getId() == _id && AllTaskAdapter != null) {
//
//                        AllTaskAdapter.removeItemById(_id);
//                        Log.i("LOG_MSG", "JobTaskArray AllJobContainers was removed" + job.getId() );
//                        AllJobContainers.remove(job);
//                        StoredTasks.removeIf(item -> item == _id);
//                        break;
//                    }
//                }
//
//            });
//
//            recyclerView_allJob = findViewById(R.id.allJob_RecyclerView);
//            recyclerView_myJob = findViewById(R.id.myJob_RecyclerView);
//
//            AllTaskAdapter = new TaskCardAdapter(AllJobContainers);
//            MyTaskAdapter = new TaskCardAdapter(MyJobContainers);
//
//            recyclerView_allJob.setLayoutManager(new LinearLayoutManager(this));
//            recyclerView_myJob.setLayoutManager(new LinearLayoutManager(this));
//
//            recyclerView_allJob.setAdapter(AllTaskAdapter);
//            recyclerView_myJob.setAdapter(MyTaskAdapter);
//
////            AllTaskAdapter.updateAdapter(AllJobContainers);
//
//        }
//        catch (Exception ex){
//            Log.i("LOG_MSG", "StoreJobArray " + ex.getMessage());
//        }
//    }

}
