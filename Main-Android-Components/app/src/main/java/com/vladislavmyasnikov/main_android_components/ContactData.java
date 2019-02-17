package com.vladislavmyasnikov.main_android_components;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ContactData implements Parcelable {

    public ContactData(String displayName, ArrayList<String> phoneNumbers) {
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

    public ArrayList<String> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public String getPhoneNumbersAsString() {
        if (mPhoneNumbers.size() > 0) {
            return mPhoneNumbers.toString();
        }
        return EMPTY_PHONE_NUMBERS;
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
    private ArrayList<String> mPhoneNumbers;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ContactData createFromParcel(Parcel in) {
            return new ContactData(in);
        }

        public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
    };

    private static final String EMPTY_PHONE_NUMBERS = "No phone numbers";
}
