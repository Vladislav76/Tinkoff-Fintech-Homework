package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.StudentEntityToStudentMapper
import com.vladislavmyasnikov.courseproject.data.mapper.StudentJsonToStudentEntityMapper
import com.vladislavmyasnikov.courseproject.data.mapper.StudentJsonToStudentMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.Students
import com.vladislavmyasnikov.courseproject.data.network.entities.StudentJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : IStudentRepository {

    private val compositeDisposable = CompositeDisposable()
    private var recentRequestTime: Long = 0

    override val studentsFetchOutcome: PublishSubject<Outcome<List<Student>>> = PublishSubject.create<Outcome<List<Student>>>()

    override fun fetchStudents() {
        compositeDisposable.add(localDataSource.studentDao().loadStudents()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { studentsFetchOutcome.onNext(Outcome.loading(true)) }
                .map(StudentEntityToStudentMapper::map)
                .subscribe({ data ->
                    studentsFetchOutcome.onNext(Outcome.success(data))
                    if (isCacheDirty()) loadStudents() else studentsFetchOutcome.onNext(Outcome.loading(false))
                }, {
                    loadStudents()
                }, {
                    loadStudents()
                }))
    }

    override fun refreshStudents() {
        studentsFetchOutcome.onNext(Outcome.loading(true))
        if (isCacheDirty()) {
            loadStudents()
        } else {
            studentsFetchOutcome.onNext(Outcome.loading(false))
        }
    }

    private fun loadStudents() {
        compositeDisposable.add(remoteDataSource.getStudents(memory.loadToken())
                .subscribeOn(Schedulers.io())
                .doFinally {
                    studentsFetchOutcome.onNext(Outcome.loading(false))
                    recentRequestTime = System.currentTimeMillis()
                }
                .subscribe({
                    response -> onResponseReceived(response)
                }, {
                    error -> onFailureReceived(error)
                }))
    }

    private fun onResponseReceived(response: Response<List<Students>>) {
        if (response.isSuccessful) {
            val students = response.body()?.get(1)?.students
            if (students != null) {
                studentsFetchOutcome.onNext(Outcome.success(StudentJsonToStudentMapper.map(students).sortedByDescending { it.mark }))
                localDataSource.studentDao().insertStudents(StudentJsonToStudentEntityMapper.map(students))
                Log.d("STUDENT_REPO", "Inserted ${students.size} students from API in DB")
            }
            Log.d("STUDENT_REPO", Thread.currentThread().toString())
        } else {
            onFailureReceived(IllegalStateException())
        }
    }

    private fun onFailureReceived(e: Throwable) {
        studentsFetchOutcome.onNext(Outcome.failure(e))
        Log.d("STUDENT_REPO", Thread.currentThread().toString())
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}