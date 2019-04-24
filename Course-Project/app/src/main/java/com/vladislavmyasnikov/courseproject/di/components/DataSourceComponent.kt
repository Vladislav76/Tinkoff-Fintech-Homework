package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.network.FintechService
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.di.modules.DatabaseModule
import com.vladislavmyasnikov.courseproject.di.modules.MemoryModule
import com.vladislavmyasnikov.courseproject.di.modules.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, MemoryModule::class])
interface DataSourceComponent {

    fun getMemory(): Memory
    fun getNetworkService(): FintechService
    fun getDatabase(): LocalDatabase
}