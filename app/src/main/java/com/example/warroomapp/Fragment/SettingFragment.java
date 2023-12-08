package com.example.warroomapp.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.warroomapp.Activity.HomeActivity;
import com.example.warroomapp.Activity.LoginActivity;
import com.example.warroomapp.Activity.SignUpActivity;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;

public class SettingFragment extends Fragment {
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private RelativeLayout btnLogout;
    private RelativeLayout btnBookmarks;
    private RelativeLayout btnUrlServer;
    private SwitchCompat NoticeSwitch;

    private Dialog url_dialog;
    private Button btnSaveUrl;
    private Button btnCancelUrl;
    private EditText txtUrlServer;
    private TextView txtUrlDisplay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url_dialog = new Dialog(getContext());
        url_dialog.setContentView(R.layout.dialog_url_setting);
        url_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        url_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.dialog_background));
        url_dialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        NoticeSwitch = view.findViewById(R.id.switch_notice_setting);
        txtUrlDisplay = view.findViewById(R.id.txtUrlDisplay);

        listenerContainer(view);

        sharedPrefSetting = new SharedPreferencesSetting(getActivity().getApplicationContext());
        SettingUpdate();

        return view;
    }

    private void listenerContainer(View view){
        btnLogout = view.findViewById(R.id.btn_logout_setting);
        btnUrlServer = view.findViewById(R.id.btn_urlserver_setting);
        btnBookmarks = view.findViewById(R.id.btn_bookmarks_setting);
        txtUrlServer = url_dialog.findViewById(R.id.txtUrlSetting);

        btnSaveUrl = url_dialog.findViewById(R.id.btnSaveUrlSetting);
        btnCancelUrl = url_dialog.findViewById(R.id.btnCancelUrlSetting);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPrefManager = new SharedPreferencesManager(getActivity().getApplicationContext());
                sharedPrefManager.saveUserData(
                        "",
                        0,
                        "",
                        "",
                        "",
                        "",
                        "",
                        false,
                        false,
                        "",
                        "",
                        "",
                        ""
                );

                Toast.makeText(getActivity().getApplicationContext(), "Logout!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btnUrlServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_dialog.show();
            }
        });

        btnCancelUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_dialog.dismiss();
            }
        });

        btnSaveUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPrefSetting.saveApiUrl(txtUrlServer.getText().toString());
                Toast.makeText(getActivity().getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                url_dialog.dismiss();
                SettingUpdate();
            }
        });

    }
    private void SettingUpdate(){
        txtUrlServer.setHint(sharedPrefSetting.getApiUrl());
        txtUrlDisplay.setText(sharedPrefSetting.getApiUrl());
    }
}