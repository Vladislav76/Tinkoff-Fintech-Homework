package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.mapper.ProfileJsonToProfileMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IProfileRepository {

    private var recentRequestTime: Long = 0

    override fun fetchProfile(): Observable<Profile> {
        return if (isCacheDirty()) {
            createCacheObservable().concatWith(createApiObservable())
        } else {
            createCacheObservable()
        }
    }

    private fun createCacheObservable() =
            memory.loadProfile()
                    .map(ProfileJsonToProfileMapper::map)
                    .doAfterNext { Log.d("PROFILE_REPO", "Profile is loaded fro cache") }
                    .subscribeOn(Schedulers.io())

    private fun createApiObservable() =
            remoteDataSource.getProfile(memory.loadToken())
                    .map { it.profile }
                    .doAfterSuccess {
                        saveProfile(it)
                        recentRequestTime = System.currentTimeMillis()
                    }
                    .map(ProfileJsonToProfileMapper::map)
                    .subscribeOn(Schedulers.io())

    private fun saveProfile(profile: ProfileJson) {
        memory.saveProfileData(profile)
        Log.d("PROFILE_REPO", "Inserted profile from API in DB. #${Thread.currentThread()}")
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}