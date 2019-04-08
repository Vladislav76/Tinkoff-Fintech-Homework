package com.vladislavmyasnikov.courseproject.data.models;

import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("max_score")
    public double maxScore;

    @SerializedName("deadline_date")
    public String deadlineDate;
}
