package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import io.reactivex.subjects.PublishSubject

interface ILoginRepository {

    val accessFetchOutcome: PublishSubject<Outcome<Unit>>

    fun getAccess()
    fun login(email: String, password: String)
}