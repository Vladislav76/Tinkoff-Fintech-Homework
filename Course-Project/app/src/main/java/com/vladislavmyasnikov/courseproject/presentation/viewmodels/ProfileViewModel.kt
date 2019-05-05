package com.vladislavmyasnikov.courseproject.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.mapper.ExceptionMapper
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ProfileViewModel(private val profileRepository: IProfileRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val progressEmitter = BehaviorSubject.create<Boolean>()
    private val profileEmitter = BehaviorSubject.create<Profile>()
    private val errorEmitter = PublishSubject.create<Throwable>()
    private var isLoading = false

    val loadingState: Observable<Boolean> = progressEmitter
    val profile: Observable<Profile> = profileEmitter
    val errors: Observable<Throwable> = errorEmitter

    fun fetchProfile() {
        if (!isLoading) {
            isLoading = true
            progressEmitter.onNext(true)
            disposables.add(profileRepository.fetchProfile()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        progressEmitter.onNext(false)
                        isLoading = false
                    }
                    .subscribe({ lectures ->
                        profileEmitter.onNext(lectures)
                    }, { error ->
                        errorEmitter.onNext(ExceptionMapper.map(error))
                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

/*
 * Factory class
 */
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
