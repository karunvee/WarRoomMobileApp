package com.example.warroomapp.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.warroomapp.Adaptor.MessageListAdapter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private  RecyclerView chatView;
    private WebSocketClient mWebSocketClient;
    private List<Message> messages = new ArrayList<>();
    private MessageListAdapter messageListAdapter;

    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;

    private BottomNavigationView btnChatNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefManager = new SharedPreferencesManager(getContext());
        sharedPrefSetting = new SharedPreferencesSetting(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        chatView = view.findViewById(R.id.ChatView);
        EditText txtSendMsg = view.findViewById(R.id.txtSendMsg);
        Button btnSendMsg = view.findViewById(R.id.btnSendMsg);

        // Initialize the adapter
        messageListAdapter = new MessageListAdapter(getContext(), messages);
        chatView.setAdapter(messageListAdapter);
        chatView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnChatNav = view.findViewById(R.id.chat_menu);
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = txtSendMsg.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    // Add a new message to the list
                    sendMessageToWebSocket(messageText);
                    // Clear the input field
                    txtSendMsg.getText().clear();
//                    chatView.scrollToPosition(messages.size() - 1);
                }
            }
        });

        chatView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try{
                    if (bottom < oldBottom) {
                        chatView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(messages.size() > 0){
                                    chatView.smoothScrollToPosition(messages.size() - 1);
                                }
                            }
                        });
                    }
                } catch (Exception ex){
                    Log.i("LOG_MSG", "onLayoutChange : " + ex.getMessage());
                }
            }
        });

        try{
            connectChatWebSocket();
        }
        catch (Exception e){
            Log.i("LOG_MSG", "Websocket : " + e.getMessage());
        }

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i("LOG_MSG", "ChatFragment was onStart");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("LOG_MSG", "ChatFragment was onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("LOG_MSG", "ChatFragment was destroy");
    }
    private void connectChatWebSocket() {
        URI uri;
        try {
            uri = new URI(globalVariable.websocket_url + sharedPrefSetting.getApiUrl() +":8001/ws/chat_message/" + sharedPrefManager.getUserId() + "/");
        } catch (URISyntaxException e) {
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("LOG_MSG", "Opened : " + uri);
//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonMessage = new JSONObject(message);
                                String messageType = jsonMessage.getString("type");
                                String messageText = "";
                                String sender = "";
                                String timeStamp = "";
                                Log.i("LOG_MSG", "chat : " + jsonMessage);

                                if("initial_messages".equals(messageType)) {
                                    JSONArray messagesArray = jsonMessage.getJSONArray("messages");
                                    for (int i = 0; i < messagesArray.length(); i++) {
                                        JSONObject messageObject = messagesArray.getJSONObject(i);
                                        sender = messageObject.getString("sender");
                                        messageText = messageObject.getString("message");
                                        timeStamp = messageObject.getString("timestamp");

                                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX");
                                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeStamp, inputFormatter);

                                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
                                        String formattedTime = outputFormatter.format(offsetDateTime.plusHours(7).toLocalDateTime());

                                        Message newMessage = new Message(messageText, sender, formattedTime);
                                        messageListAdapter.addMessage(newMessage);
                                        chatView.scrollToPosition(messages.size() - 1);
                                    }

                                }
                                else{
                                    sender = jsonMessage.getString("sender");
                                    messageText = jsonMessage.getString("message");
                                    timeStamp = new SimpleDateFormat("HH:mm").format(new java.util.Date());
                                    Message newMessage = new Message(messageText, sender, timeStamp);
                                    messageListAdapter.addMessage(newMessage);
                                    chatView.scrollToPosition(messages.size() - 1);
                                }
                            }
                            catch (Exception e){
                                Log.i("LOG_MSG", "Exception Chat : " + e.getMessage());
                            }
                        }
                    });

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

            }
        };
        mWebSocketClient.connect();
    }
    private void sendMessageToWebSocket(String messageText) {
        // Create a JSON object for the message
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", "chat");
            jsonMessage.put("message", messageText);
            jsonMessage.put("sender",  sharedPrefManager.getUsername());
            jsonMessage.put("room_id", sharedPrefManager.getUserId());

            // Send the message to the WebSocket
            mWebSocketClient.send(jsonMessage.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}