package com.vladislavmyasnikov.courseproject.data.repositories_impl

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.StudentEntityToStudentMapper
import com.vladislavmyasnikov.courseproject.data.mapper.StudentJsonToStudentEntityMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executors
import javax.inject.Inject

class StudentRepository @Inject constructor(
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IStudentRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val compositeDisposable = CompositeDisposable()
    private var recentRequestTime: Long = 0

    override val studentsFetchOutcome: PublishSubject<Outcome<List<Student>>> = PublishSubject.create<Outcome<List<Student>>>()

    override fun fetchStudents() {
        studentsFetchOutcome.onNext(Outcome.loading(true))

        compositeDisposable.add(localDataSource.studentDao().loadStudents()
                .subscribeOn(Schedulers.io())
                .map(StudentEntityToStudentMapper::map)
                .doAfterSuccess {
                    refreshStudents()
                    Log.d("STUDENT_REPO", "Refreshing data...")
                }
                .subscribe({
                    studentsFetchOutcome.onNext(Outcome.success(it))
                    studentsFetchOutcome.onNext(Outcome.loading(false))
                }, {
                    studentsFetchOutcome.onNext(Outcome.failure(it))
                    studentsFetchOutcome.onNext(Outcome.loading(false))
                }))
    }

    override fun refreshStudents() {
        if (isCacheDirty()) {
            studentsFetchOutcome.onNext(Outcome.loading(true))
            compositeDisposable.add(remoteDataSource.getStudents(memory.loadToken())
                    .subscribeOn(Schedulers.io())
                    .map { it[1].students }
                    .doAfterSuccess { saveStudents(it) }
                    .map(StudentJsonToStudentEntityMapper::map)
                    .map(StudentEntityToStudentMapper::map)
                    .map { it.sortedBy { it.id } }
                    .subscribe({
                        recentRequestTime = System.currentTimeMillis()
                        studentsFetchOutcome.onNext(Outcome.success(it))
                        studentsFetchOutcome.onNext(Outcome.loading(false))
                    }, {
                        recentRequestTime = System.currentTimeMillis()
                        studentsFetchOutcome.onNext(Outcome.failure(it))
                        studentsFetchOutcome.onNext(Outcome.loading(false))
                    }))
        } else {
            studentsFetchOutcome.onNext(Outcome.loading(false))
        }
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    private fun saveStudents(students: List<StudentJson>) {
        executor.execute {
            val entities = StudentJsonToStudentEntityMapper.map(students)
            localDataSource.studentDao().insertStudents(entities)
            Log.d("STUDENT_REPO", "Inserted ${students.size} students from API in DB")
        }
    }



    companion object {

        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}