package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.Login
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
        private val memory: Memory,
        private val remoteDataSource: FintechPortalApi
) : ILoginRepository {

    override val accessFetchOutcome: PublishSubject<Outcome<Unit>> = PublishSubject.create<Outcome<Unit>>()

    override fun getAccess() {
        val cookieData = memory.loadCookieData()
        if (cookieData != null && isTokenNotExpire(cookieData.time)) {
            accessFetchOutcome.onNext(Outcome.success(Unit))
        }
    }

    override fun login(email: String, password: String) {
        Maybe.create<Response<Unit>> { emitter ->
            when {
                isEmailNotCorrect(email) -> emitter.onError(IOException())
                isPasswordNotCorrect(password) -> emitter.onError(IOException())
                else -> emitter.onComplete()
            }
        }.concatWith(remoteDataSource.getAccess(Login(email, password)).toMaybe()
                .doOnSubscribe { accessFetchOutcome.onNext(Outcome.loading(true)) }
                .doFinally { accessFetchOutcome.onNext(Outcome.loading(false)) }
        ).subscribeOn(Schedulers.io())
         .subscribe({
             response -> onResponseReceived(response)
         }, {
            error -> onFailureReceived(error)
         })
    }

    private fun onResponseReceived(response: Response<Unit>) {
        if (response.isSuccessful) {
            val headers = response.headers()

            val cookieData = headers.get("Set-Cookie")!!.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val token = cookieData.find { it.contains("anygen") }
            val time = cookieData.find { it.contains("expires") }?.removePrefix("expires=")

            if (token != null && time != null) {
                memory.saveCookieData(CookieData(token, time))
                accessFetchOutcome.onNext(Outcome.success(Unit))
            }
        } else {
            accessFetchOutcome.onNext(Outcome.failure(IllegalStateException()))
        }
        Log.d("LOGIN_REPO", Thread.currentThread().toString())
    }

    private fun onFailureReceived(e: Throwable) {
        accessFetchOutcome.onNext(Outcome.failure(e))
        Log.d("LOGIN_REPO", Thread.currentThread().toString())
    }

    /*
     * Check functions
     */
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

        private fun isEmailNotCorrect(s: String): Boolean {
            val mask = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$".toRegex()
            return !mask.matches(s)
        }

        private fun isPasswordNotCorrect(s: String): Boolean = s.length < 8
    }
}