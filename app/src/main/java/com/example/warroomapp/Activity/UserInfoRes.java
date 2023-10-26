package com.example.warroomapp.Activity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfoRes implements Serializable {
    @SerializedName("user")
    public UserInfo user;
    public UserInfo getUser() {
        return user;
    }
}
class UserInfo implements Serializable{
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
    @SerializedName("image")
    public String image;

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
    public String getImage() {
        return image;
    }
}
