package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.mapper.ProfileJsonToProfileMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.ProfileInfo
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IProfileRepository {

    private val compositeDisposable = CompositeDisposable()
    private var recentRequestTime: Long = 0

    override val profileFetchOutcome: PublishSubject<Outcome<Profile>> = PublishSubject.create<Outcome<Profile>>()

    override fun fetchProfile() {
        compositeDisposable.add(Maybe.fromCallable { memory.loadProfile() }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { profileFetchOutcome.onNext(Outcome.loading(true)) }
                .map(ProfileJsonToProfileMapper::map)
                .subscribe({ data ->
                    profileFetchOutcome.onNext(Outcome.success(data))
                    if (isCacheDirty()) loadProfile() else profileFetchOutcome.onNext(Outcome.loading(false))
                }, {
                    loadProfile()
                }, {
                    loadProfile()
                }))
    }

    override fun refreshProfile() {
        profileFetchOutcome.onNext(Outcome.loading(true))
        if (isCacheDirty()) {
            loadProfile()
        } else {
            profileFetchOutcome.onNext(Outcome.loading(false))
        }
    }

    private fun loadProfile() {
        compositeDisposable.add(remoteDataSource.getProfile(memory.loadToken())
                .subscribeOn(Schedulers.io())
                .doFinally {
                    profileFetchOutcome.onNext(Outcome.loading(false))
                    recentRequestTime = System.currentTimeMillis()
                }
                .subscribe({
                    response -> onResponseReceived(response)
                }, {
                    error -> onFailureReceived(error)
                }))
    }

    private fun onResponseReceived(response: Response<ProfileInfo>) {
        if (response.isSuccessful) {
            val profile = response.body()?.profile
            if (profile != null) {
                profileFetchOutcome.onNext(Outcome.success(ProfileJsonToProfileMapper.map(profile)))
                memory.saveProfileData(profile)
                Log.d("PROFILE_REPO", "Saved profile from API in prefs")
            }
            Log.d("PROFILE_REPO", Thread.currentThread().toString())
        } else {
            onFailureReceived(IllegalStateException())
        }
    }

    private fun onFailureReceived(e: Throwable) {
        profileFetchOutcome.onNext(Outcome.failure(e))
        Log.d("PROFILE_REPO", Thread.currentThread().toString())
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}