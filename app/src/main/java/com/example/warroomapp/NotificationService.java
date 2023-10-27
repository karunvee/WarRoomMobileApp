package com.example.warroomapp;
import static android.app.PendingIntent.getActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.warroomapp.Activity.HomeActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class NotificationService extends Service {
    private ArrayList<Integer> JobIndex = new ArrayList<Integer>();
    private  ArrayList<JobTaskParameter> jobContainers = new ArrayList<JobTaskParameter>();
    private WebSocketClient mWebSocketClient;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "task_channel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            Notification notification = createNotificationService();
            startForeground(NOTIFICATION_ID, notification);
        }
        catch (Exception ex){
            Log.i("LOG_MSG", "onStartCommand " + ex.getMessage());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotificationService() {
        // Create a notification for the foreground service
        NotificationChannel channel = new NotificationChannel("default", "Foreground Service", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_warroom_logo)
                .setContentTitle("WarRoomNotice Service")
                .setContentText("Notification Service is running in the foreground");

        return builder.build();
    }

}
