package com.vladislavmyasnikov.courseproject.data.repositories_impl

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.mapper.ProfileJsonToProfileMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executors
import javax.inject.Inject

class ProfileRepository @Inject constructor(
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IProfileRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val compositeDisposable = CompositeDisposable()
    private var recentRequestTime: Long = 0

    override val profileFetchOutcome: PublishSubject<Outcome<Profile>> = PublishSubject.create<Outcome<Profile>>()

    override fun fetchProfile() {
        profileFetchOutcome.onNext(Outcome.loading(true))
        compositeDisposable.add(Single.fromCallable { memory.loadProfile() }
                .subscribeOn(Schedulers.io())
                .map(ProfileJsonToProfileMapper::map)
                .doFinally{
                    refreshProfile()
                    Log.d("PROFILE_REPO", "Refreshing data...")
                }
                .subscribe({
                    profileFetchOutcome.onNext(Outcome.success(it))
                    profileFetchOutcome.onNext(Outcome.loading(false))
                }, {
                    profileFetchOutcome.onNext(Outcome.failure(it))
                    profileFetchOutcome.onNext(Outcome.loading(false))
                }))
    }

    override fun refreshProfile() {
        if (isCacheDirty()) {
            profileFetchOutcome.onNext(Outcome.loading(true))
            compositeDisposable.add(remoteDataSource.getProfile(memory.loadToken())
                    .subscribeOn(Schedulers.io())
                    .map { it.profile }
                    .doAfterSuccess { saveProfile(it) }
                    .map(ProfileJsonToProfileMapper::map)
                    .subscribe({
                        recentRequestTime = System.currentTimeMillis()
                        profileFetchOutcome.onNext(Outcome.success(it))
                        profileFetchOutcome.onNext(Outcome.loading(false))
                    }, {
                        recentRequestTime = System.currentTimeMillis()
                        profileFetchOutcome.onNext(Outcome.failure(it))
                        profileFetchOutcome.onNext(Outcome.loading(false))
                    }))
        } else {
            profileFetchOutcome.onNext(Outcome.loading(false))
        }
    }

    private fun saveProfile(profile: ProfileJson) {
        executor.execute {
            memory.saveProfileData(profile)
            Log.d("PROFILE_REPO", "Saved profile from API in prefs")
        }
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS



    companion object {

        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}