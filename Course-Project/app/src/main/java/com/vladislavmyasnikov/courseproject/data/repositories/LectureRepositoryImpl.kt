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
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject

class LectureRepositoryImpl @Inject constructor(
        private val taskRepository: ITaskRepository,
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : ILectureRepository {

    private var recentRequestTime: Long = 0

    override fun fetchLectures(): Observable<List<Lecture>> {
        return if (isCacheDirty()) {
            createDatabaseObservable().concatWith(createApiObservable())
        } else {
            createDatabaseObservable()
        }
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    private fun createDatabaseObservable() =
            Observable.fromCallable { localDataSource.lectureDao().loadLectures() }
                    .map(LectureEntityToLectureMapper::map)
                    .doAfterNext { Log.d("LECTURE_REPO", "Lectures are loaded from DB (size: ${it.size})") }
                    .subscribeOn(Schedulers.io())

    private fun createApiObservable() =
            remoteDataSource.getLectures(memory.loadToken())
                    .map { it.lectures }
                    .doAfterSuccess {
                        saveLectures(it)
                        recentRequestTime = System.currentTimeMillis()
                    }
                    .map(LectureJsonToLectureMapper::map)
                    .map {it.sortedBy {it.id}}
                    .subscribeOn(Schedulers.io())

    private fun saveLectures(lectures: List<LectureJson>) {
        localDataSource.lectureDao().insertLectures(LectureJsonToLectureEntityMapper.map(lectures))
        lectures.forEach { taskRepository.saveTasksByLectureId(it.tasks, it.id) }
        Log.d("LECTURE_REPO", "Inserted ${lectures.size} lectures from API in DB. #${Thread.currentThread()}")
    }

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}