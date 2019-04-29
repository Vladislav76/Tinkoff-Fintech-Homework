package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.LectureEntityToLectureMapper
import com.vladislavmyasnikov.courseproject.data.mapper.LectureJsonToLectureEntityMapper
import com.vladislavmyasnikov.courseproject.data.mapper.LectureJsonToLectureMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.Lectures
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class LectureRepositoryImpl @Inject constructor(
        private val taskRepository: ITaskRepository,
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : ILectureRepository {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var recentRequestTime: Long = 0

    override val lecturesFetchOutcome: PublishSubject<Outcome<List<Lecture>>> = PublishSubject.create<Outcome<List<Lecture>>>()

    override fun fetchLectures() {
        compositeDisposable.add(localDataSource.lectureDao().loadLectures()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { lecturesFetchOutcome.onNext(Outcome.loading(true)) }
                .map(LectureEntityToLectureMapper::map)
                .subscribe({ data ->
                    lecturesFetchOutcome.onNext(Outcome.success(data))
                    if (isCacheDirty()) loadLectures() else lecturesFetchOutcome.onNext(Outcome.loading(false))
                }, {
                    loadLectures()
                }, {
                    loadLectures()
                }))
    }

    override fun refreshLectures() {
        lecturesFetchOutcome.onNext(Outcome.loading(true))
        if (isCacheDirty()) {
            loadLectures()
        } else {
            lecturesFetchOutcome.onNext(Outcome.loading(false))
        }
    }

    private fun loadLectures() {
        compositeDisposable.add(remoteDataSource.getLectures(memory.loadToken())
                .subscribeOn(Schedulers.io())
                .doFinally {
                    lecturesFetchOutcome.onNext(Outcome.loading(false))
                    recentRequestTime = System.currentTimeMillis()
                }
                .subscribe({
                    response -> onResponseReceived(response)
                }, {
                    error -> onFailureReceived(error)
                }))
    }

    private fun onResponseReceived(response: Response<Lectures>) {
        if (response.isSuccessful) {
            val lectures = response.body()?.lectures
            if (lectures != null) {
                lecturesFetchOutcome.onNext(Outcome.success(LectureJsonToLectureMapper.map(lectures).sortedBy { it.id }))
                localDataSource.lectureDao().insertLectures(LectureJsonToLectureEntityMapper.map(lectures))
                lectures.forEach { taskRepository.saveTasksByLectureId(it.tasks, it.id)}
                Log.d("LECTURE_REPO", "Inserted ${lectures.size} lectures from API in DB")
            }
            Log.d("LECTURE_REPO", Thread.currentThread().toString())
        } else {
            onFailureReceived(IllegalStateException())
        }
    }

    private fun onFailureReceived(e: Throwable) {
        lecturesFetchOutcome.onNext(Outcome.failure(e))
        Log.d("LECTURE_REPO", Thread.currentThread().toString())
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}