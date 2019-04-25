package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.models.Profile
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories.ProfileRepository
import javax.inject.Inject

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

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



class ProfileViewModelFactory @Inject constructor(private val profileRepository: ProfileRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            ProfileViewModel(profileRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
