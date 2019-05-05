package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.db.entities.LectureWithTasksEntity
import com.vladislavmyasnikov.courseproject.data.mapper.*
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.entities.LectureWithTasks
import com.vladislavmyasnikov.courseproject.domain.repositories.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LectureRepositoryImpl @Inject constructor(
        private val taskRepository: ITaskRepository,
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi,
        private val memory: Memory
) : ILectureRepository {

    private var recentRequestTime: Long = 0

    override fun fetchLectures(): Observable<List<Lecture>> {
        return if (isCacheDirty() && memory.loadCourseUrl() != null) {
            createLectureDatabaseObservable().concatWith(createApiObservable().map(JsonToModelLectureMapper::map))
        } else {
            createLectureDatabaseObservable()
        }
    }

    override fun fetchLecturesWithTasks(): Observable<List<LectureWithTasks>> {
        return if (isCacheDirty() && memory.loadCourseUrl() != null) {
            createLectureWithTasksDatabaseObservable().concatWith(createApiObservable().map(JsonToModelLectureWithTasksMapper::map))
        } else {
            createLectureWithTasksDatabaseObservable()
        }
    }

    override fun deleteLectures(): Completable =
        Completable.fromCallable { localDataSource.lectureDao().deleteLectures() }

    private fun createLectureDatabaseObservable() =
            Observable.fromCallable { localDataSource.lectureDao().loadLectures() }
                    .filter { it.isNotEmpty() }
                    .map(EntityToModelLectureMapper::map)
                    .doAfterNext { Log.d("LECTURE_REPO", "Lectures are loaded from DB (size: ${it.size})") }

    private fun createLectureWithTasksDatabaseObservable() =
            Observable.fromCallable { localDataSource.lectureDao().loadLecturesWithTasks() }
                    .filter { it.isNotEmpty() }
                    .map(EntityToModelLectureWithTasksMapper::map)
                    .doAfterNext { Log.d("LECTURE_REPO", "Lectures with tasks are loaded from DB (size: ${it.size})") }

    private fun createApiObservable() =
            remoteDataSource.getLectures(memory.loadToken(), memory.loadCourseUrl()!!)
                    .map { it.lectures }
                    .doAfterSuccess {
                        saveLectures(it)
                        recentRequestTime = System.currentTimeMillis()
                    }
                    .map { it.sortedBy { lecture -> lecture.id } }

    private fun saveLectures(lectures: List<LectureJson>) {
        localDataSource.lectureDao().insertLectures(JsonToEntityLectureMapper.map(lectures))
        lectures.forEach { taskRepository.saveTasksByLectureId(it.tasks, it.id) }
        Log.d("LECTURE_REPO", "Inserted ${lectures.size} lectures from API in DB. #${Thread.currentThread()}")
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}