package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import io.reactivex.Observable

interface IProfileRepository {

    fun fetchProfile(): Observable<Profile>
}