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


}
