package com.vladislavmyasnikov.main_android_components;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ContactData implements Parcelable {

    public ContactData(String displayName, List<String> phoneNumbers) {
        mDisplayName = displayName;
        mPhoneNumbers = phoneNumbers;
    }

    public ContactData(Parcel in) {
        this.mPhoneNumbers = new ArrayList<>();
        this.mDisplayName = in.readString();
        in.readStringList(this.mPhoneNumbers);
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public List<String> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mDisplayName);
        parcel.writeStringList(mPhoneNumbers);
    }

    private String mDisplayName;
    private List<String> mPhoneNumbers;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ContactData createFromParcel(Parcel in) {
            return new ContactData(in);
        }

        public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
    };
}
