package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import io.reactivex.subjects.PublishSubject

interface ILectureRepository {

    val lecturesFetchOutcome: PublishSubject<Outcome<List<Lecture>>>

    fun fetchLectures()
    fun refreshLectures()
}