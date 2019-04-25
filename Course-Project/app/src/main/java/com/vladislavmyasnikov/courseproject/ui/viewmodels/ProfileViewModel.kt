package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.data.models.Profile
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories.ProfileRepository
import com.vladislavmyasnikov.courseproject.di.components.DaggerRepositoryComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.ui.main.App

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepository: ProfileRepository by lazy {
        App.instance.repositoryComponent.getProfileRepository()
    }
    private val mutableResponseMessage = MutableLiveData<ResponseMessage>()
    val responseMessage: LiveData<ResponseMessage> = mutableResponseMessage
    val profile: LiveData<Profile> = profileRepository.profile

    fun updateProfile() {
        profileRepository.refreshProfile(object : ProfileRepository.LoadProfileCallback {
            override fun onResponseReceived(response: ResponseMessage) {
                mutableResponseMessage.postValue(response)
            }
        })
    }

    fun resetResponseMessage() {
        if (mutableResponseMessage.value != ResponseMessage.LOADING) {
            mutableResponseMessage.value = null
        }
    }
}
