package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.network.CookieData
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.Login
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
        private val memory: Memory,
        private val remoteDataSource: FintechPortalApi
) : ILoginRepository {

    override fun login(): Observable<Boolean> =
            memory.loadCookieData()
                .map { isTokenNotExpire(it.time) }
                .observeOn(Schedulers.io())

    override fun login(email: String, password: String): Observable<Boolean> =
            createCorrectnessObservable(email, password).concatWith(createApiObservable(email, password))

    private fun createCorrectnessObservable(email: String, password: String) =
            Observable.create<Boolean> { e ->
                when {
                    isEmailNotCorrect(email) -> e.onError(IOException())
                    isPasswordNotCorrect(password) -> e.onError(IOException())
                    else -> e.onComplete()
                }
            }

    private fun createApiObservable(email: String, password: String) =
            remoteDataSource.getAccess(Login(email, password))
                    .map {
                        if (it.isSuccessful) {
                            val headers = it.headers()
                            val cookieData = headers.get("Set-Cookie")!!.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            val token = cookieData.find { it.contains("anygen") }
                            val time = cookieData.find { it.contains("expires") }?.removePrefix("expires=")
                            if (token != null && time != null) {
                                memory.saveCookieData(CookieData(token, time))
                                Log.d("LOGIN_REPO", "Cookie are saved #${Thread.currentThread()}")
                                true
                            } else false
                        } else false
                    }
                    .subscribeOn(Schedulers.io())

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