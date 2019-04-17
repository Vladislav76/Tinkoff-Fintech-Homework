package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.models.Profile
import com.vladislavmyasnikov.courseproject.data.network.ProfileInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val mApplication = application
    private val dataRepository = DataRepository.getInstance(application)
    private val failMessage = application.resources.getString(R.string.not_ok_status_message)
    private val mutableProfileData = MutableLiveData<Profile>()
    private val mutableMessageState = MutableLiveData<String>()
    val profileData: LiveData<Profile> = mutableProfileData
    val messageState: LiveData<String> = mutableMessageState
    var profileAvatar: RequestBuilder<Drawable>? = null
    var recentRequestTime: Long = 0

    fun uploadProfile() {
        val profile = dataRepository.loadProfile()
        if (profile != null) {
            profileAvatar = Glide.with(mApplication).load("https://fintech.tinkoff.ru${profile.avatarUrl}")
            mutableProfileData.value = profile
        }
    }

    fun updateProfile() {
        if (System.currentTimeMillis() - recentRequestTime > DataRepository.CASH_LIFE_TIME_IN_MILLISECONDS) {
            loadData()
        } else {
            mutableMessageState.value = ""
        }
    }

    fun resetMessageState() {
        mutableMessageState.value = null
    }

    private fun loadData() {
        dataRepository.loadProfile(object : Callback<ProfileInfo> {
            override fun onFailure(call: Call<ProfileInfo>, e: Throwable) {
                mutableMessageState.value = failMessage
            }

            override fun onResponse(call: Call<ProfileInfo>, response: Response<ProfileInfo>) {
                val result = response.body()
                if (response.message() == "OK" && result != null && result.profile != null) {
                    dataRepository.saveUserData(result.profile)
                    profileAvatar = Glide.with(mApplication).load("https://fintech.tinkoff.ru${result.profile.avatarUrl}")
                    mutableProfileData.value = result.profile
                    recentRequestTime = System.currentTimeMillis()
                    mutableMessageState.value = ""
                } else {
                    mutableMessageState.value = failMessage
                }
            }
        })
    }
}
