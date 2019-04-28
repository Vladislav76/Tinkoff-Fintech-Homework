package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.network.FintechService
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.data.repositories_impl.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class, NetworkModule::class, MemoryModule::class])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(localDataSource: LocalDatabase): TaskRepository =
            TaskRepository(localDataSource)

    @Provides
    @Singleton
    fun provideStudentRepository(localDataSource: LocalDatabase, remoteDataSource: FintechService, memory: Memory): StudentRepository =
            StudentRepository(localDataSource, remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideLoginRepository(remoteDataSource: FintechService, memory: Memory): LoginRepository =
            LoginRepository(memory, remoteDataSource)

    @Provides
    @Singleton
    fun provideProfileRepository(remoteDataSource: FintechService, memory: Memory): ProfileRepository =
            ProfileRepository(remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideLectureRepository(taskRepository: TaskRepository, localDataSource: LocalDatabase, remoteDataSource: FintechService, memory: Memory): LectureRepository =
            LectureRepository(taskRepository, localDataSource, remoteDataSource, memory)
}