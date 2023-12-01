package com.example.warroomapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSetting {
    private static final String PREF_NAME = "PrefsSetting";
    private static final String KEY_API_URL = "api_url";
    private SharedPreferences preferences;
    public SharedPreferencesSetting(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public void saveApiUrl(String ApiUrl){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_API_URL, ApiUrl);
        editor.apply();
    }
    public String getApiUrl(){
        return preferences.getString(KEY_API_URL, "");
    }
}
