package com.vladislavmyasnikov.courseproject.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public class User implements Parcelable, Identifiable {

    private static int NEXT_USER_ID;

    @SerializedName("id")
    private int mId;

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("middle_name")
    private String mMiddleName;

    @SerializedName("avatar")
    private String mAvatar;

    private int mPoints;

    private static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(@NonNull String firstName, @NonNull String lastName, String middleName, int points) {
        mFirstName = firstName;
        mLastName = lastName;
        mMiddleName = middleName;
        mPoints = points;
        mId = generateId();
    }

    public User(User original) {
        mId = original.mId;
        mFirstName = original.mFirstName;
        mLastName = original.mLastName;
        mMiddleName = original.mMiddleName;
        mPoints = original.mPoints;
        mAvatar = original.mAvatar;
    }

    private User(Parcel in) {
        mFirstName = in.readString();
        mLastName = in.readString();
        mMiddleName = in.readString();
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
        parcel.writeString(mFirstName);
        parcel.writeString(mLastName);
        parcel.writeString(mMiddleName);
        parcel.writeInt(mPoints);
    }

    public int getId() {
        return mId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public int getPoints() {
        return mPoints;
    }

    public String getAvatar() {
        return mAvatar;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        User user = (User) other;
        return mId == user.mId &&
                mPoints == user.mPoints &&
                mFirstName.equals(user.mFirstName) &&
                mLastName.equals(user.mLastName) &&
                mMiddleName.equals(user.mMiddleName);
    }
}
