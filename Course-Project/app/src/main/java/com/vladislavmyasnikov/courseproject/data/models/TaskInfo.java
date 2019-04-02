package com.vladislavmyasnikov.courseproject.data.models;

import com.google.gson.annotations.SerializedName;

public class TaskInfo {

    @SerializedName("id")
    public int id;

    @SerializedName("status")
    public String status;

    @SerializedName("mark")
    public double mark;

    @SerializedName("task")
    public Task task;
}
