package com.vladislavmyasnikov.courseproject.models;

import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("email")
    private String mEmail;

    @SerializedName("password")
    private String mPassword;

    public Login(String email, String password) {
         mEmail = email;
         mPassword = password;
    }
}
