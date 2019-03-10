package com.vladislavmyasnikov.courseproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private String mName;
    private String mSurname;
    private int mPoints;

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
    }

    private User(Parcel in) {
        mName = in.readString();
        mSurname = in.readString();
        mPoints = in.readInt();
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
