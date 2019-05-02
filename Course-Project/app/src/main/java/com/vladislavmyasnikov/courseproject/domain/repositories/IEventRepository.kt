package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Event
import io.reactivex.Observable

interface IEventRepository {

    fun fetchEvents(): Observable<List<Event>>
}