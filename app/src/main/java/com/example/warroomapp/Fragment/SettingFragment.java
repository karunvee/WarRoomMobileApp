package com.example.warroomapp.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.warroomapp.Activity.HomeActivity;
import com.example.warroomapp.Activity.LoginActivity;
import com.example.warroomapp.Activity.SignUpActivity;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesManager;

public class SettingFragment extends Fragment {
    private SharedPreferencesManager sharedPrefManager;
    private RelativeLayout btnLogout;
    private RelativeLayout btnBookmarks;
    private SwitchCompat NoticeSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        NoticeSwitch = view.findViewById(R.id.switch_notice_setting);
        listenerContainer(view);

        return view;
    }

    private void listenerContainer(View view){
        btnLogout = view.findViewById(R.id.btn_logout_setting);
        btnBookmarks = view.findViewById(R.id.btn_bookmarks_setting);

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
                        ""
                );

                Toast.makeText(getActivity().getApplicationContext(), "Logout!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


//        btnLogout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
////                    btnLogout.setBackgroundResource(R.drawable.card_white_selected_box);
//                } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
////                    btnLogout.setBackgroundColor(Color.parseColor("#FFFFFF"));
////                    btnLogout.setBackgroundResource(R.drawable.card_white_box);
////                    Toast.makeText(getActivity().getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });

    }
}