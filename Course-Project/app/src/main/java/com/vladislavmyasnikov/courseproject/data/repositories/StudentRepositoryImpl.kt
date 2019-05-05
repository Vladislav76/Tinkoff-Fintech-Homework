package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.*
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.Observable
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IStudentRepository {

    private var recentRequestTime: Long = 0

    override fun fetchStudents(): Observable<List<Student>> {
        return if (isCacheDirty() && memory.loadCourseUrl() != null) {
            createDatabaseObservable().concatWith(createApiObservable())
        } else {
            createDatabaseObservable()
        }
    }

    private fun createDatabaseObservable() =
            Observable.fromCallable { localDataSource.studentDao().loadStudents() }
                    .filter { it.isNotEmpty() }
                    .map(EntityToModelStudentMapper::map)
                    .doAfterNext { Log.d("STUDENT_REPO", "Students are loaded from DB (size: ${it.size})") }

    private fun createApiObservable() =
            remoteDataSource.getStudents(memory.loadToken(), memory.loadCourseUrl()!!)
                    .map { it[1].students }
                    .doAfterSuccess {
                        saveStudents(it)
                        recentRequestTime = System.currentTimeMillis()
                    }
                    .map(StudentJsonToStudentMapper::map)

    private fun saveStudents(students: List<StudentJson>) {
        localDataSource.studentDao().insertStudents(StudentJsonToStudentEntityMapper.map(students))
        Log.d("STUDENT_REPO", "Inserted ${students.size} students with marks from API in DB. #${Thread.currentThread()}")
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}