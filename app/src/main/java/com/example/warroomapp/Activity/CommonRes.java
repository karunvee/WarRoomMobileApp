package com.example.warroomapp.Activity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommonRes implements Serializable {
    @SerializedName("detail")
    public String detail;
    public String getDetail() {
        return detail;
    }
}
