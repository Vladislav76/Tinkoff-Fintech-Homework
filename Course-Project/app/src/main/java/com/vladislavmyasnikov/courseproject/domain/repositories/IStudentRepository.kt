package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import io.reactivex.Observable

interface IStudentRepository {

    val studentsFetchOutcome: Observable<Outcome<List<Student>>>

    fun fetchStudents()
    fun refreshStudents()
}