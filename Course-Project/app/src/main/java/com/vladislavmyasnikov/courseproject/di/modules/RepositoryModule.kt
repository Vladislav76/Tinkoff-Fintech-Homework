package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.data.repositories.*
import com.vladislavmyasnikov.courseproject.domain.repositories.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class, NetworkModule::class, MemoryModule::class])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(localDataSource: LocalDatabase): ITaskRepository =
            TaskRepositoryImpl(localDataSource)

    @Provides
    @Singleton
    fun provideStudentRepository(localDataSource: LocalDatabase, remoteDataSource: FintechPortalApi, memory: Memory): IStudentRepository =
            StudentRepositoryImpl(localDataSource, remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideLoginRepository(remoteDataSource: FintechPortalApi, memory: Memory): ILoginRepository =
            LoginRepositoryImpl(memory, remoteDataSource)

    @Provides
    @Singleton
    fun provideProfileRepository(remoteDataSource: FintechPortalApi, memory: Memory): IProfileRepository =
            ProfileRepositoryImpl(remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideLectureRepository(taskRepository: ITaskRepository, localDataSource: LocalDatabase, remoteDataSource: FintechPortalApi, memory: Memory): ILectureRepository =
            LectureRepositoryImpl(taskRepository, localDataSource, remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideCourseRepository(remoteDataSource: FintechPortalApi, memory: Memory): ICourseRepository =
            CourseRepositoryImpl(remoteDataSource, memory)

    @Provides
    @Singleton
    fun provideEventRepository(localDataSource: LocalDatabase, remoteDataSource: FintechPortalApi): IEventRepository =
            EventRepositoryImpl(localDataSource, remoteDataSource)
}