package com.vladislavmyasnikov.courseproject.data.repositories_impl

import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.Login
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import io.reactivex.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class LoginRepository @Inject constructor(
        private val memory: Memory,
        private val remoteDataSource: FintechPortalApi
) : ILoginRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val callback = object : Callback<Unit> {
        override fun onFailure(call: Call<Unit>, e: Throwable) {
            accessFetchOutcome.onNext(Outcome.loading(false))
            accessFetchOutcome.onNext(Outcome.failure(e))
        }

        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            accessFetchOutcome.onNext(Outcome.loading(false))
            if (response.isSuccessful) {
                val headers = response.headers()

                val cookieData = headers.get("Set-Cookie")!!.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val token = cookieData.find { it.contains("anygen") }
                val time = cookieData.find { it.contains("expires") }?.removePrefix("expires=")

                if (token != null && time != null) {
                    saveCookieData(CookieData(token, time))
                    accessFetchOutcome.onNext(Outcome.success(Unit))
                }
            } else {
                accessFetchOutcome.onNext(Outcome.failure(IllegalStateException()))
            }
        }
    }

    override val accessFetchOutcome: PublishSubject<Outcome<Unit>> = PublishSubject.create<Outcome<Unit>>()

    override fun getAccess() {
        val cookieData = memory.loadCookieData()
        if (cookieData != null && isTokenNotExpire(cookieData.time)) {
            accessFetchOutcome.onNext(Outcome.success(Unit))
        }
    }

    override fun login(email: String, password: String) {
        accessFetchOutcome.onNext(Outcome.loading(true))
        remoteDataSource.getAccess(Login(email, password)).enqueue(callback)
    }

    private fun saveCookieData(data: CookieData) {
        executor.execute { memory.saveCookieData(data) }
    }



    companion object {

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