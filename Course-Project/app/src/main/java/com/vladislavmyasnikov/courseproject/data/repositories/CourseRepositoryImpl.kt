package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Course
import com.vladislavmyasnikov.courseproject.domain.repositories.ICourseRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : ICourseRepository {

    private var recentRequestTime: Long = 0

    override fun fetchCourseUrlAndTitle(): Observable<Pair<String, String>> {
        return if (isCacheDirty()) {
            memory.loadCourseUrlAndTitle().concatWith(createApiObservable())
        } else {
            memory.loadCourseUrlAndTitle()
        }
    }

    override fun fetchCourse(): Observable<Course> = memory.loadCourse()

    override fun saveCourse(data: Course) {
        memory.saveCourse(data)
        Log.d("COURSE_REPO", "Inserted course content in cache. #${Thread.currentThread()}")
    }

    private fun createApiObservable() =
    remoteDataSource.getCourses(memory.loadToken())
            .map { it.courses[0] }
            .map { it.url to it.title }
            .doAfterSuccess {
                memory.saveCourseUrlAndTitle(it.first, it.second)
                recentRequestTime = System.currentTimeMillis()
                Log.d("COURSE_REPO", "Inserted course url and title from API in cache. #${Thread.currentThread()}")
            }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}


