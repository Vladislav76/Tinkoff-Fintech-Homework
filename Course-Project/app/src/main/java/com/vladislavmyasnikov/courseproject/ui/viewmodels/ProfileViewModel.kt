package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ProfileViewModel(private val profileRepository: IProfileRepository) : ViewModel() {

    val profileFetchOutcome: Observable<Outcome<Profile>> = profileRepository.profileFetchOutcome
    var profile: Profile? = null
    var isLoading: Boolean = false
    private val disposables = CompositeDisposable()

    init {
        disposables.add(profileFetchOutcome.subscribe {
            when (it) {
                is Outcome.Success -> {
                    profile = it.data
                    Log.d("PROFILE_VM", "Profile is fetched")
                }
                is Outcome.Progress -> isLoading = it.loading
            }
        })
    }

    fun fetchProfile() {
        if (!isLoading) profileRepository.fetchProfile()
    }

    fun refreshProfile() {
        if (!isLoading) profileRepository.refreshProfile()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}



class ProfileViewModelFactory @Inject constructor(private val profileRepository: IProfileRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            ProfileViewModel(profileRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
