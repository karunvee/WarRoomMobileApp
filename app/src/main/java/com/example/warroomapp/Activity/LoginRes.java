package com.example.warroomapp.Activity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginRes implements Serializable {
    @SerializedName("status")
    public String status;
    @SerializedName("token")
    public String token;

    @SerializedName("user")
    public User user;

    public String getStatus() {
        return status;
    }
    public String getToken() {
        return token;
    }
    public User getUser() {
        return user;
    }
}

class User implements Serializable{
    @SerializedName("image")
    public String image;
    @SerializedName("id")
    public int id;
    @SerializedName("username")
    public String username;
    @SerializedName("name")
    public String name;
    @SerializedName("emp_no")
    public String empNo;
    @SerializedName("description")
    public String description;
    @SerializedName("remark")
    public String remark;
    @SerializedName("is_user")
    public boolean isUser;
    @SerializedName("is_staff")
    public boolean isStaff;
    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getDescription() {
        return description;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isStaff() {
        return isStaff;
    }
}
