package com.vladislavmyasnikov.courseproject.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.mapper.ExceptionMapper
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class LoginViewModel(private val loginRepository: ILoginRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val progressEmitter = BehaviorSubject.create<Boolean>()
    private val accessEmitter = BehaviorSubject.create<Unit>()
    private val errorEmitter = PublishSubject.create<Throwable>()
    private var isLoading = false

    val loadingState: Observable<Boolean> = progressEmitter
    val access: Observable<Unit> = accessEmitter
    val errors: Observable<Throwable> = errorEmitter

    init {
        login()
    }

    fun login(email: String, password: String) {
        if (!isLoading) {
            isLoading = true
            progressEmitter.onNext(true)
            disposables.add(loginRepository.login(email, password)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        progressEmitter.onNext(false)
                        isLoading = false
                    }
                    .subscribe({ access ->
                        accessEmitter.onNext(access)
                    }, { error ->
                        errorEmitter.onNext(ExceptionMapper.map(error))
                    })
            )
        }
    }

    private fun login() {
        isLoading = true
        progressEmitter.onNext(true)
        disposables.add(loginRepository.login()
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    progressEmitter.onNext(false)
                    isLoading = false
                }
                .subscribe({ access ->
                    accessEmitter.onNext(access)
                }, { error ->
                    errorEmitter.onNext(error)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

/*
 * Factory class
 */
class LoginViewModelFactory @Inject constructor(private val loginRepository: ILoginRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(loginRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}