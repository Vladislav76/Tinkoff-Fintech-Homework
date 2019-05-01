package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import io.reactivex.Observable

interface ILectureRepository {

    fun fetchLectures(): Observable<List<Lecture>>
}