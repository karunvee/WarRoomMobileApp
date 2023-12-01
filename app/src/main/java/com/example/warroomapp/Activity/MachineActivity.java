package com.example.warroomapp.Activity;

import static com.example.warroomapp.Adaptor.ImageCustom.getRoundedCornerBitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.warroomapp.Fragment.AllJobsFragment;
import com.example.warroomapp.Fragment.MachineActionFragment;
import com.example.warroomapp.Fragment.MachineHistoryFragment;
import com.example.warroomapp.Fragment.MyJobsFragment;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MachineActivity extends AppCompatActivity {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesMachine sharedPreferencesMachine;
    private Button btnBackToHome;
    private Button btnCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnCameraView = findViewById(R.id.btnCameraView);
        TextView txtMachineName = findViewById(R.id.txtMachineName);
        TextView txtMachineEquipId = findViewById(R.id.txtMachineEquipId);
        TextView txtLine = findViewById(R.id.txtLine);
        TextView txtEquipType = findViewById(R.id.txtEquipType);

        sharedPrefSetting = new SharedPreferencesSetting(getApplicationContext());
        sharedPreferencesMachine = new SharedPreferencesMachine(getApplicationContext());
//        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        txtMachineName.setText(sharedPreferencesMachine.getMachine());
        txtMachineEquipId.setText(sharedPreferencesMachine.getEquipCode());
        txtLine.setText(sharedPreferencesMachine.getLine());
        txtEquipType.setText(sharedPreferencesMachine.getEquipType());



        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MachineActivity.this, HomeActivity.class);
                finish();
            }
        });
        btnCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MachineActivity.this, CCTVActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        TabLayout tabLayout = findViewById(R.id.machine_tab);
        ViewPager viewPager = findViewById(R.id.machineView_pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new MachineActionFragment(), "Action");
        adapter.AddFragment(new MachineHistoryFragment(), "History");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        String imageURL = "http://10.150.192.16/images/" +sharedPreferencesMachine.getEquipType() + ".jpg";

        Log.i("LOG_MSG", "Image : " + imageURL);

        if (imageURL.trim().isEmpty() || imageURL == null ){
            imageURL = globalVariable.api_url + sharedPrefSetting.getApiUrl() + "/media/images/person_1.jpg";
        }

        ImageView imageView = findViewById(R.id.ImageMachine);
        // Load the image from the URL using Picasso
        Picasso.get()
                .load(imageURL)
                .resize(220,220)
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // The image has been loaded, now apply rounding
                        Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Bitmap roundedBitmap = getRoundedCornerBitmap(originalBitmap);
                        imageView.setImageBitmap(roundedBitmap);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}