package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Student
import io.reactivex.Completable
import io.reactivex.Observable

interface IStudentRepository {

    fun fetchStudents(): Observable<List<Student>>
    fun deleteStudents(): Completable
}