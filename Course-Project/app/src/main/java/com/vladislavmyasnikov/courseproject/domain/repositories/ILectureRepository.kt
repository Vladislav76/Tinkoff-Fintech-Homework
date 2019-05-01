package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import io.reactivex.Observable

interface ILectureRepository {

    fun fetchLectures(): Observable<List<Lecture>>
}