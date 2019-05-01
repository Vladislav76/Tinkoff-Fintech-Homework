package com.vladislavmyasnikov.courseproject.domain.repositories

import io.reactivex.Observable

interface ILoginRepository {

    fun login(): Observable<Boolean>
    fun login(email: String, password: String): Observable<Boolean>
}