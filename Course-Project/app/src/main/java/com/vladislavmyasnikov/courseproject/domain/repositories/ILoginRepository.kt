package com.vladislavmyasnikov.courseproject.domain.repositories

import io.reactivex.Observable

interface ILoginRepository {

    fun login(): Observable<Unit>
    fun login(email: String, password: String): Observable<Unit>
}