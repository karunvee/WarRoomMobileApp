package com.example.warroomapp.Activity;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("ad_server")
    private String ad_server;

    public LoginData(String username, String password, String ad_server) {
        this.username = username;
        this.password = password;
        this.ad_server = ad_server;
    }
}
