package com.vladislavmyasnikov.courseproject.data.repositories_impl

import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.FintechService
import com.vladislavmyasnikov.courseproject.data.network.Login
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class LoginRepository @Inject constructor(
        private val memory: Memory,
        private val remoteDataSource: FintechService
) : ILoginRepository {

    private val executor = Executors.newSingleThreadExecutor()

    fun getAccess(email: String, password: String, callback: LoadLoginCallback) {
        callback.onResponseReceived(ResponseMessage.LOADING)
        remoteDataSource.getAccess(Login(email, password)).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, e: Throwable) {
                callback.onResponseReceived(ResponseMessage.NO_INTERNET)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.message() == "OK") {
                    val headers = response.headers()

                    val cookieData = headers.get("Set-Cookie")!!.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val token = cookieData.find { it.contains("anygen") }
                    val time = cookieData.find { it.contains("expires") }?.removePrefix("expires=")

                    if (token != null && time != null) {
                        saveCookieData(CookieData(token, time))
                        callback.onResponseReceived(ResponseMessage.SUCCESS)
                    }
                } else {
                    callback.onResponseReceived(ResponseMessage.ERROR)
                }
            }
        })
    }

    fun loadCookieData(): CookieData? = memory.loadCookieData()

    private fun saveCookieData(data: CookieData) {
        executor.execute { memory.saveCookieData(data) }
    }



    interface LoadLoginCallback {

        fun onResponseReceived(response: ResponseMessage)
    }
}