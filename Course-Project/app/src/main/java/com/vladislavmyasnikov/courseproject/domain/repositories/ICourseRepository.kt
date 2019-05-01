package com.vladislavmyasnikov.courseproject.domain.repositories

import com.vladislavmyasnikov.courseproject.domain.entities.Course
import io.reactivex.Observable

interface ICourseRepository {

    fun fetchCourseUrlAndTitle(): Observable<Pair<String, String>>
    fun fetchCourse(): Observable<Course>
    fun saveCourse(data: Course)
}