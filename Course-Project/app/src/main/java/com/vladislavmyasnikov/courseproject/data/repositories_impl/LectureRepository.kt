package com.vladislavmyasnikov.courseproject.data.repositories_impl

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.LectureEntityToLectureMapper
import com.vladislavmyasnikov.courseproject.data.mapper.LectureJsonToLectureEntityMapper
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executors
import javax.inject.Inject

class LectureRepository @Inject constructor(
        private val taskRepository: ITaskRepository,
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : ILectureRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val jsonToEntityMapper = LectureJsonToLectureEntityMapper
    private val entityToLectureMapper = LectureEntityToLectureMapper
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var recentRequestTime: Long = 0

    override val lecturesFetchOutcome: PublishSubject<Outcome<List<Lecture>>> = PublishSubject.create<Outcome<List<Lecture>>>()

    override fun fetchLectures() {
        lecturesFetchOutcome.onNext(Outcome.loading(true))
        compositeDisposable.add(localDataSource.lectureDao().loadLectures()
                .subscribeOn(Schedulers.io())
                .map(entityToLectureMapper::map)
                .doAfterSuccess { refreshLectures() }
                .subscribe({
                    lecturesFetchOutcome.onNext(Outcome.success(it))
                    lecturesFetchOutcome.onNext(Outcome.loading(false))
                }, {
                    lecturesFetchOutcome.onNext(Outcome.failure(it))
                    lecturesFetchOutcome.onNext(Outcome.loading(false))
                }))
    }

    override fun refreshLectures() {
        if (isCacheDirty()) {
            lecturesFetchOutcome.onNext(Outcome.loading(true))
            compositeDisposable.add(
                    remoteDataSource.getLectures(memory.loadToken())
                    .subscribeOn(Schedulers.io())
                    .map { it.lectures }
                    .doAfterSuccess { saveLecturesAndTasks(it) }
                    .map(jsonToEntityMapper::map)
                    .map(entityToLectureMapper::map)
                    .map { it.sortedBy { it.id } }
                    .subscribe({
                        recentRequestTime = System.currentTimeMillis()
                        lecturesFetchOutcome.onNext(Outcome.success(it))
                        lecturesFetchOutcome.onNext(Outcome.loading(false))
                    }, {
                        recentRequestTime = System.currentTimeMillis()
                        lecturesFetchOutcome.onNext(Outcome.failure(it))
                        lecturesFetchOutcome.onNext(Outcome.loading(false))
                    }))
        } else {
            lecturesFetchOutcome.onNext(Outcome.loading(false))
        }
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    private fun saveLecturesAndTasks(lectures: List<LectureJson>) {
        executor.execute {
            localDataSource.lectureDao().insertLectures(jsonToEntityMapper.map(lectures))
            lectures.forEach { taskRepository.saveTasksByLectureId(it.tasks, it.id)}
            Log.d("LECTURE_REPO", "Inserted ${lectures.size} lectures from API in DB")
        }
    }



    companion object {

        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}