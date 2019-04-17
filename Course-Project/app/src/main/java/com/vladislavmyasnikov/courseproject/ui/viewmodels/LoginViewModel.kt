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
import java.util.concurrent.atomic.AtomicBoolean

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val dataRepository = DataRepository.getInstance(application)
    private val noInternetMessage = application.resources.getString(R.string.no_internet_message)
    private val incorrectDataMessage = application.resources.getString(R.string.incorrect_authorization_data_message)
    private val mutableLoginState = MutableLiveData<Either<String, Unit>>()
    private val isRequestAllowed = AtomicBoolean(true)
    val loginState: LiveData<Either<String, Unit>> = mutableLoginState

    init {
        val cookieData = dataRepository.loadCookieData()
        if (cookieData != null && tokenIsNotExpire(cookieData.time)) {
            mutableLoginState.value = Right(Unit)
        }
    }

    fun login(email: String, password: String) {
        if (isRequestAllowed.compareAndSet(true, false)) {
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
                        val time = cookieData.find { it.contains("expires") }

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

    fun resetLoginState() {
        mutableLoginState.value = null
    }

    private fun tokenIsNotExpire(time: String): Boolean {
        //Checking will be in the future...
        return false
    }
}