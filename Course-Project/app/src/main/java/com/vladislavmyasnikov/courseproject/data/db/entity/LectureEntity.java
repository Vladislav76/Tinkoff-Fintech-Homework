package com.vladislavmyasnikov.courseproject.data.db.entity;

import com.vladislavmyasnikov.courseproject.data.models.Identifiable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lectures")
public class LectureEntity implements Identifiable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "title")
    private String mTitle;

    @Override
    public int getId() {
        return mId;
    }

    public LectureEntity(int id, String title) {
        mId = id;
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        LectureEntity lecture = (LectureEntity) other;
        return mId == lecture.mId &&
                mTitle.equals(lecture.mTitle);
    }
}
