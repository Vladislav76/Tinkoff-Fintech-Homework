package com.vladislavmyasnikov.courseproject.data.db.entity;

import com.vladislavmyasnikov.courseproject.data.models.Identifiable;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "tasks", foreignKeys = {@ForeignKey(entity = LectureEntity.class,
                                                        parentColumns = "id",
                                                        childColumns = "lecture_id",
                                                        onDelete = CASCADE)})
public class TaskEntity implements Identifiable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "lecture_id", index = true)
    private int mLectureId;

    @ColumnInfo(name = "status")
    private String mStatus;

    @ColumnInfo(name = "mark")
    private double mMark;

    @ColumnInfo(name = "max_score")
    private double mMaxScore;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "deadline_date")
    private Date mDeadline;

    public TaskEntity(int id, String title, String status, Date deadline, double mark, double maxScore, int lectureId) {
        mId = id;
        mTitle = title;
        mStatus = status;
        mDeadline = deadline;
        mMark = mark;
        mMaxScore = maxScore;
        mLectureId = lectureId;
    }

    @Override
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getStatus() {
        return mStatus;
    }

    public double getMark() {
        return mMark;
    }

    public int getLectureId() {
        return mLectureId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDeadline() {
        return mDeadline;
    }

    public double getMaxScore() {
        return mMaxScore;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        TaskEntity task = (TaskEntity) other;
        return mId == task.mId &&
                mLectureId == task.mLectureId &&
                mTitle.equals(task.mTitle) &&
                mStatus.equals(task.mStatus) &&
                mDeadline.equals(task.mDeadline) &&
                mMark == task.mMark &&
                mMaxScore == task.mMaxScore;
    }
}
