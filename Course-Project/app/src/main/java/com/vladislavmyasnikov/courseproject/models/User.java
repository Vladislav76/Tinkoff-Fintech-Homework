package com.vladislavmyasnikov.courseproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private int mId;
    private String mName;
    private String mSurname;
    private int mPoints;

    private static int NEXT_USER_ID;

    private static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(@NonNull String name, @NonNull String surname, int points) {
        mName = name;
        mSurname = surname;
        mPoints = points;
        mId = generateId();
    }

    public User(User original) {
        mId = original.getId();
        mName = original.getName();
        mSurname = original.getSurname();
        mPoints = original.getPoints();
    }

    private User(Parcel in) {
        mName = in.readString();
        mSurname = in.readString();
        mPoints = in.readInt();
        mId = generateId();
    }

    private static int generateId() {
        return NEXT_USER_ID++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mName);
        parcel.writeString(mSurname);
        parcel.writeInt(mPoints);
    }

    /* GETTERS */
    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getSurname() {
        return mSurname;
    }

    public int getPoints() {
        return mPoints;
    }

    /* SETTERS */
    public void setName(String name) {
        mName = name;
    }

    public void setSurname(String surname) {
        mSurname = surname;
    }

    public void setPoints(int points) {
        mPoints = points;
    }
}
