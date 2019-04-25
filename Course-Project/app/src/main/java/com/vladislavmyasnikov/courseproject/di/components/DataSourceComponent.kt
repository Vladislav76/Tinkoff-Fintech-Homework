package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.network.FintechService
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.data.repositories.LectureRepository
import com.vladislavmyasnikov.courseproject.data.repositories.TaskRepository
import com.vladislavmyasnikov.courseproject.di.modules.DatabaseModule
import com.vladislavmyasnikov.courseproject.di.modules.MemoryModule
import com.vladislavmyasnikov.courseproject.di.modules.NetworkModule
import com.vladislavmyasnikov.courseproject.di.modules.RepositoryModule
import dagger.Component
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, MemoryModule::class])
interface DataSourceComponent {

    fun getMemory(): Memory
    fun getNetworkService(): FintechService
    fun getDatabase(): LocalDatabase
}