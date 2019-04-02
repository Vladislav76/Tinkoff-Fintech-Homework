package com.vladislavmyasnikov.courseproject.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Lecture {

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("tasks")
    public List<TaskInfo> tasks;
}
