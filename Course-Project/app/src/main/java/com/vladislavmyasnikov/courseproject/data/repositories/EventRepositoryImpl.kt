package com.vladislavmyasnikov.courseproject.data.repositories

import android.util.Log
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.mapper.EntityToModelEventMapper
import com.vladislavmyasnikov.courseproject.data.mapper.JsonToEntityEventMapper
import com.vladislavmyasnikov.courseproject.data.mapper.JsonToModelEventMapper
import com.vladislavmyasnikov.courseproject.data.network.EventInfo
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.domain.entities.Event
import com.vladislavmyasnikov.courseproject.domain.repositories.IEventRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
        private val localDataSource: LocalDatabase,
        private val remoteDataSource: FintechPortalApi
) : IEventRepository {

    private var recentRequestTime: Long = 0

    override fun fetchEvents(): Observable<List<Event>> {
        return if (isCacheDirty()) {
            createDatabaseObservable().concatWith(createApiObservable())
        } else {
            createDatabaseObservable()
        }
    }

    private fun createDatabaseObservable() =
            Observable.fromCallable { localDataSource.eventDao().loadEvents() }
                    .filter { it.isNotEmpty() }
                    .map(EntityToModelEventMapper::map)
                    .doAfterNext { Log.d("EVENT_REPO", "Events are loaded from DB (size: ${it.size})") }
                    .subscribeOn(Schedulers.io())

    private fun createApiObservable() =
            remoteDataSource.getEvents()
                    .doAfterSuccess {
                        saveEvents(it)
                        recentRequestTime = System.currentTimeMillis()
                    }
                    .map {
                        JsonToModelEventMapper.isActualEvent = true
                        val actualEvents = JsonToModelEventMapper.map(it.actualEvents)
                        JsonToModelEventMapper.isActualEvent = false
                        val pastEvents = JsonToModelEventMapper.map(it.pastEvents)
                        listOf(actualEvents, pastEvents).flatten()
                    }
                    .subscribeOn(Schedulers.io())

    private fun saveEvents(data: EventInfo) {
        JsonToEntityEventMapper.isActualEvent = true
        val actualEvents = JsonToEntityEventMapper.map(data.actualEvents)

        JsonToEntityEventMapper.isActualEvent = false
        val pastEvents = JsonToEntityEventMapper.map(data.pastEvents)

        localDataSource.eventDao().insertEvents(actualEvents)
        localDataSource.eventDao().insertEvents(pastEvents)

        Log.d("EVENT_REPO", "Inserted ${actualEvents.size} actual and ${pastEvents.size} past " +
                "events from API in DB. #${Thread.currentThread()}")
    }

    private fun isCacheDirty() = System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME_IN_MS

    companion object {
        private const val CASH_LIFE_TIME_IN_MS = 10_000
    }
}