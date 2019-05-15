package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import io.reactivex.Observable

interface IProfileRepository {

    fun fetchProfile(): Observable<Profile>
}