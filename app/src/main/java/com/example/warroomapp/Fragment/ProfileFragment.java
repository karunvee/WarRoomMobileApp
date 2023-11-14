package com.example.warroomapp.Fragment;
import com.example.warroomapp.Activity.LoginActivity;

import static com.example.warroomapp.Adaptor.ImageCustom.getRoundedCornerBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.warroomapp.Activity.User;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesSetting;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesSetting sharedPrefSetting;
    private String token = "";
    private Integer id = 0;
    private String username = "";
    private String name = "";
    private String emp_no = "";
    private String description = "";
    private String image = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPrefSetting = new SharedPreferencesSetting(getContext());
        ImageView imageView = view.findViewById(R.id.profile_image); // Replace with your ImageView ID
        TextView txtProfileName = view.findViewById(R.id.txtProfileName);
        TextView txtProfileDetail = view.findViewById(R.id.txtProfileDetail);

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            id = args.getInt("id");
            username = args.getString("username");
            name = args.getString("name");
            emp_no = args.getString("emp_no");
            description = args.getString("description");
            image = args.getString("image");
            Log.i("LOG_MSG", "Image : " + image);
            txtProfileName.setText(name);
            txtProfileDetail.setText((emp_no + " " + description));
//            Toast.makeText(getActivity().getApplicationContext(), Image, Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        Log.i("LOG_MSG", "Image : " + globalVariable.api_url + sharedPrefSetting.getApiUrl() + image);
        String imageUrl = globalVariable.api_url + sharedPrefSetting.getApiUrl() + image; // Replace with your image URL

        if (image.trim().isEmpty() || image == null ){
            imageUrl += "/media/images/person_1.jpg";
        }

        // Load the image from the URL using Picasso
        Picasso.get()
                .load(imageUrl)
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



        return view;
    }

}