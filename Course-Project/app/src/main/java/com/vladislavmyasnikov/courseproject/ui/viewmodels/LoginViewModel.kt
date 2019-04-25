package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.data.models.LoginResponseMessage
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories.LoginRepository
import com.vladislavmyasnikov.courseproject.di.components.DaggerRepositoryComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.ui.main.App
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val loginRepository: LoginRepository by lazy {
        App.instance.repositoryComponent.getLoginRepository()
    }
    private val mutableResponseMessage = MutableLiveData<LoginResponseMessage>()
    val responseMessage: LiveData<LoginResponseMessage> = mutableResponseMessage

    init {
        val cookieData = loginRepository.loadCookieData()
        if (cookieData != null && isTokenNotExpire(cookieData.time)) {
            mutableResponseMessage.value = LoginResponseMessage.SUCCESS
        }
    }

    fun login(email: String, password: String) {
            when {
                isEmailNotCorrect(email) -> { mutableResponseMessage.value = LoginResponseMessage.INCORRECT_EMAIL }
                isPasswordNotCorrect(password) -> { mutableResponseMessage.value = LoginResponseMessage.INCORRECT_PASSWORD }
                else -> {
                    loginRepository.getAccess(email, password, object : LoginRepository.LoadLoginCallback {
                        override fun onResponseReceived(response: ResponseMessage) {
                            when (response) {
                                ResponseMessage.SUCCESS -> mutableResponseMessage.value = LoginResponseMessage.SUCCESS
                                ResponseMessage.LOADING -> mutableResponseMessage.value = LoginResponseMessage.LOADING
                                ResponseMessage.ERROR -> mutableResponseMessage.value = LoginResponseMessage.ERROR
                                ResponseMessage.NO_INTERNET -> mutableResponseMessage.value = LoginResponseMessage.NO_INTERNET
                            }

                        }
                    })
                }
            }
    }

    fun resetResponseMessage() {
        if (mutableResponseMessage.value != LoginResponseMessage.LOADING) {
            mutableResponseMessage.value = null
        }
    }



    companion object {

        private fun isEmailNotCorrect(s: String): Boolean {
            val mask = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$".toRegex()
            return !mask.matches(s)
        }

        private fun isPasswordNotCorrect(s: String): Boolean = s.length < 8

        private fun isTokenNotExpire(s: String): Boolean {
            return try {
                val parser = SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss zzz", Locale.ENGLISH)
                val expiryDate = parser.parse(s)
                val currentDate = Date()
                expiryDate.after(currentDate)
            } catch (e: ParseException) {
                false
            }
        }
    }
}