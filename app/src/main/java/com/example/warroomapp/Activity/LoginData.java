package com.example.warroomapp.Activity;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("domain")
    private String domain;

    public LoginData(String username, String password, String domain) {
        this.username = username;
        this.password = password;
        this.domain = domain;
    }
}
