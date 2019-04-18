package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.models.Either
import com.vladislavmyasnikov.courseproject.data.models.Left
import com.vladislavmyasnikov.courseproject.data.models.Right
import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val dataRepository = DataRepository.getInstance(application)
    private val noInternetMessage = application.resources.getString(R.string.no_internet_message)
    private val incorrectDataMessage = application.resources.getString(R.string.incorrect_authorization_data_message)
    private val incorrectEmailMessage = application.resources.getString(R.string.incorrect_input_email_message)
    private val incorrectPasswordMessage = application.resources.getString(R.string.incorrect_input_password_message)
    private val mutableLoginState = MutableLiveData<Either<String, Unit>>()
    private val isRequestAllowed = AtomicBoolean(true)
    val loginState: LiveData<Either<String, Unit>> = mutableLoginState

    init {
        val cookieData = dataRepository.loadCookieData()
        if (cookieData != null && isTokenNotExpire(cookieData.time)) {
            mutableLoginState.value = Right(Unit)
        }
    }

    fun login(email: String, password: String) {
        if (isRequestAllowed.compareAndSet(true, false)) {
            when {
                isEmailNotCorrect(email) -> {
                    mutableLoginState.value = Left(incorrectEmailMessage)
                    isRequestAllowed.set(true)
                }
                isPasswordNotCorrect(password) -> {
                    mutableLoginState.value = Left(incorrectPasswordMessage)
                    isRequestAllowed.set(true)
                }
                else -> {
                    dataRepository.getAccess(Login(email, password), object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, e: Throwable) {
                            mutableLoginState.value = Left(noInternetMessage)
                            isRequestAllowed.set(true)
                        }

                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.message() == "OK") {
                                val headers = response.headers()

                                val cookieData = headers.get("Set-Cookie")!!.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                val token = cookieData.find { it.contains("anygen") }
                                val time = cookieData.find { it.contains("expires") }?.removePrefix("expires=")

                                if (token != null && time != null) {
                                    dataRepository.saveCookieData(CookieData(token, time))
                                    mutableLoginState.value = Right(Unit)
                                }
                            } else {
                                mutableLoginState.value = Left(incorrectDataMessage)
                            }
                            isRequestAllowed.set(true)
                        }
                    })
                }
            }
        }
    }

    fun resetLoginState() {
        mutableLoginState.value = null
    }

    private fun isEmailNotCorrect(s: String): Boolean {
        val mask = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$".toRegex()
        return !mask.matches(s)
    }

    private fun isPasswordNotCorrect(s: String): Boolean = s.length < DataRepository.MINIMAL_PASSWORD_LENGTH

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