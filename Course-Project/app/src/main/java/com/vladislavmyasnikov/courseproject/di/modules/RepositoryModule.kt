package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.data.repositories_impl.*
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class, NetworkModule::class, MemoryModule::class])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(localDataSource: LocalDatabase): ITaskRepository =
            TaskRepository(localDataSource)

    @Provides
    @Singleton
    fun provideStudentRepository(localDataSource: LocalDatabase, remoteDataSource: FintechPortalApi, memory: Memory): StudentRepository =
            StudentRepository(localDataSource, remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideLoginRepository(remoteDataSource: FintechPortalApi, memory: Memory): ILoginRepository =
            LoginRepository(memory, remoteDataSource)

    @Provides
    @Singleton
    fun provideProfileRepository(remoteDataSource: FintechPortalApi, memory: Memory): ProfileRepository =
            ProfileRepository(remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideLectureRepository(taskRepository: ITaskRepository, localDataSource: LocalDatabase, remoteDataSource: FintechPortalApi, memory: Memory): ILectureRepository =
            LectureRepository(taskRepository, localDataSource, remoteDataSource, memory)
}