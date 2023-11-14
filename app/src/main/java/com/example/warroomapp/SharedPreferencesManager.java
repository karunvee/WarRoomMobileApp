package com.example.warroomapp;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SharedPreferencesManager {
    private static final String KEY_API_URL = "api_url";
    private static final String PREF_NAME = "MyAppPrefs";

    // Keys for user data
    private static final String KEY_TOKEN_ID = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMP_NO = "emp_no";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_REMARK = "remark";
    private static final String KEY_IS_USER = "is_user";
    private static final String KEY_IS_STAFF = "is_staff";
    private static final String KEY_IMAGE = "image";

    private SharedPreferences preferences;

    public SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Methods to save user data
    public void saveUserData(String tokenId, int userId, String username, String name, String empNo, String description, String remark, boolean isUser, boolean isStaff, String image) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN_ID, tokenId);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMP_NO, empNo);
        editor.putString(KEY_DESCRIPTION, description);
        editor.putString(KEY_REMARK, remark);
        editor.putBoolean(KEY_IS_USER, isUser);
        editor.putBoolean(KEY_IS_STAFF, isStaff);
        editor.putString(KEY_IMAGE, image);
        editor.apply();
    }
    public void saveApiUrl(String ApiUrl){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_API_URL, ApiUrl);
        editor.apply();
    }
    // Methods to retrieve user data

    public String getApiUrl(){
        return preferences.getString(KEY_API_URL, "");
    }
    public String getTokenId() {
        return preferences.getString(KEY_TOKEN_ID, "");
    }
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, 0);
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public String getName() {
        return preferences.getString(KEY_NAME, "");
    }

    public String getEmpNo() {
        return preferences.getString(KEY_EMP_NO, "");
    }

    public String getDescription() {
        return preferences.getString(KEY_DESCRIPTION, "");
    }

    public String getRemark() {
        return preferences.getString(KEY_REMARK, "");
    }

    public boolean isUser() {
        return preferences.getBoolean(KEY_IS_USER, false);
    }

    public boolean isStaff() {
        return preferences.getBoolean(KEY_IS_STAFF, false);
    }

    public String getImage() {
        return preferences.getString(KEY_IMAGE, "");
    }
}
