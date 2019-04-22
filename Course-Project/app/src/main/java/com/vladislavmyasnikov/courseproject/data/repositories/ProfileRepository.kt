package com.vladislavmyasnikov.courseproject.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.data.models.Profile
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.ProfileInfo
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ProfileRepository private constructor(application: Application) {

    private val remoteDataSource = NetworkService.getInstance()
    private val memory = Memory.getInstance(application)
    private val executor = Executors.newSingleThreadExecutor()
    private var recentRequestTime: Long = 0
    private val mutableProfile = MutableLiveData<Profile>()
    val profile: LiveData<Profile> = mutableProfile

    init {
        mutableProfile.value = memory.loadProfile()
    }

    fun refreshProfile(callback: LoadProfileCallback) {
        callback.onResponseReceived(ResponseMessage.LOADING)
        if (isCacheNotDirty()) {
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        } else {
            loadRemoteProfile(callback)
        }
    }

    private fun loadRemoteProfile(callback: LoadProfileCallback) {
        remoteDataSource.fintechService.getProfile(memory.loadToken()).enqueue(object : Callback<ProfileInfo> {
            override fun onFailure(call: Call<ProfileInfo>, e: Throwable) {
                callback.onResponseReceived(ResponseMessage.NO_INTERNET)
            }

            override fun onResponse(call: Call<ProfileInfo>, response: Response<ProfileInfo>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data?.profile != null) {
                        recentRequestTime = System.currentTimeMillis()
                        mutableProfile.value = data.profile
                        saveProfile(data.profile, callback)
                    }
                } else {
                    callback.onResponseReceived(ResponseMessage.ERROR)
                }
            }
        })
    }

    private fun saveProfile(profile: Profile, callback: LoadProfileCallback) {
        executor.execute {
            memory.saveUserData(profile)
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        }
    }

    private fun isCacheNotDirty() = System.currentTimeMillis() - recentRequestTime < CASH_LIFE_TIME_IN_MS



    companion object {

        private var INSTANCE: ProfileRepository? = null
        private const val CASH_LIFE_TIME_IN_MS = 10_000

        fun getInstance(application: Application): ProfileRepository =
                ProfileRepository.INSTANCE ?: synchronized(ProfileRepository::class.java) {
                    ProfileRepository.INSTANCE ?: ProfileRepository(application).also { ProfileRepository.INSTANCE = it }
                }
    }



    interface LoadProfileCallback {

        fun onResponseReceived(response: ResponseMessage)
    }
}