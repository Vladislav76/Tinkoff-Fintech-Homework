package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginViewModel(private val loginRepository: ILoginRepository) : ViewModel() {

    val accessFetchOutcome: Observable<Outcome<Unit>> = loginRepository.accessFetchOutcome
    var isLoading: Boolean = false
    private val disposables = CompositeDisposable()

    init {
        disposables.add(accessFetchOutcome.subscribe {
            when (it) {
                is Outcome.Success -> Log.d("LOGIN_VM", "Access is enabled")
                is Outcome.Progress -> isLoading = it.loading
            }
        })
    }

    fun getAccess() {
        loginRepository.getAccess()
    }

    fun login(email: String, password: String) {
        if (!isLoading) loginRepository.login(email, password)
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