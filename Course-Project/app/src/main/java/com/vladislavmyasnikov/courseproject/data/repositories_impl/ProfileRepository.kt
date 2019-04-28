package com.vladislavmyasnikov.courseproject.data.repositories_impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.ProfileInfo
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class ProfileRepository @Inject constructor(
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IProfileRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private var recentRequestTime: Long = 0
    private val mutableProfile = MutableLiveData<ProfileJson>()
    val profile: LiveData<ProfileJson> = mutableProfile

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
        remoteDataSource.getProfile(memory.loadToken()).enqueue(object : Callback<ProfileInfo> {
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

    private fun saveProfile(profile: ProfileJson, callback: LoadProfileCallback) {
        executor.execute {
            memory.saveProfileData(profile)
            callback.onResponseReceived(ResponseMessage.SUCCESS)
        }
    }

    private fun isCacheNotDirty() = System.currentTimeMillis() - recentRequestTime < CASH_LIFE_TIME_IN_MS



    companion object {

        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }



    interface LoadProfileCallback {

        fun onResponseReceived(response: ResponseMessage)
    }
}