package com.vladislavmyasnikov.courseproject.models;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("user")
    private User mUser;

    @SerializedName("status")
    private String mStatus;

    public User getUser() {
        return mUser;
    }

    public String getStatus() {
        return mStatus;
    }
}
