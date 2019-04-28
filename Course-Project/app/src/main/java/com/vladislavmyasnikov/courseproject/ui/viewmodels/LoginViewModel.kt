package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.repositories_impl.LoginRepository
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import javax.inject.Inject

class LoginViewModel(private val loginRepository: ILoginRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    val accessFetchOutcome: PublishSubject<Outcome<Unit>> = loginRepository.accessFetchOutcome
    var isLoading: Boolean = false

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
        when {
            isEmailNotCorrect(email) -> accessFetchOutcome.onNext(Outcome.failure(IOException()))
            isPasswordNotCorrect(password) -> accessFetchOutcome.onNext(Outcome.failure(IOException()))
            else -> if (!isLoading) loginRepository.login(email, password)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }



    companion object {

        private fun isEmailNotCorrect(s: String): Boolean {
            val mask = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$".toRegex()
            return !mask.matches(s)
        }

        private fun isPasswordNotCorrect(s: String): Boolean = s.length < 8
    }
}



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