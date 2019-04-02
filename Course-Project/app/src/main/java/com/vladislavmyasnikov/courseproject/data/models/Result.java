package com.vladislavmyasnikov.courseproject.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("homeworks")
    private List<Lecture> mLectures;

    @SerializedName("user")
    private User mUser;

    public User getUser() {
        return mUser;
    }

    public List<Lecture> getLectures() {
        return mLectures;
    }
}
