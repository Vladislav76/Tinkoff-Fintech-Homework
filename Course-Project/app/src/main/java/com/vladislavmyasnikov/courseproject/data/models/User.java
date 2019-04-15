package com.vladislavmyasnikov.courseproject.data.models

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

class User : Parcelable, Identifiable {

    @SerializedName("id")
    override var id: Int = 0
        private set(value: Int) {
            super.id = value
        }

    @SerializedName("first_name")
    var firstName: String? = null
        private set

    @SerializedName("last_name")
    var lastName: String? = null
        private set

    @SerializedName("middle_name")
    var middleName: String? = null
        private set

    @SerializedName("avatar")
    val avatar: String

    var points: Int = 0
        private set

    constructor(firstName: String, lastName: String, middleName: String, points: Int) {
        this.firstName = firstName
        this.lastName = lastName
        this.middleName = middleName
        this.points = points
        id = generateId()
    }

    constructor(original: User) {
        id = original.id
        firstName = original.firstName
        lastName = original.lastName
        middleName = original.middleName
        points = original.points
        avatar = original.avatar
    }

    private constructor(`in`: Parcel) {
        firstName = `in`.readString()
        lastName = `in`.readString()
        middleName = `in`.readString()
        points = `in`.readInt()
        id = generateId()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(middleName)
        parcel.writeInt(points)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val user = other as User?
        return id == user!!.id &&
                points == user.points &&
                firstName == user.firstName &&
                lastName == user.lastName &&
                middleName == user.middleName
    }

    companion object {

        private var NEXT_USER_ID: Int = 0

        private val CREATOR = object : Parcelable.Creator {
            override fun createFromParcel(`in`: Parcel): User {
                return User(`in`)
            }

            override fun newArray(size: Int): Array<User> {
                return arrayOfNulls(size)
            }
        }

        private fun generateId(): Int {
            return NEXT_USER_ID++
        }
    }
}
