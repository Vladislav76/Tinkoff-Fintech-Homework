package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.di.modules.RepositoryModule
import com.vladislavmyasnikov.courseproject.domain.repositories.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RepositoryModule::class])
@Singleton
interface AppComponent {

    fun getLectureRepository(): ILectureRepository
    fun getTaskRepository(): ITaskRepository
    fun getStudentRepository(): IStudentRepository
    fun getLoginRepository(): ILoginRepository
    fun getProfileRepository(): IProfileRepository
    fun getCourseRepository(): ICourseRepository
    fun getEventRepository(): IEventRepository
    fun getMemory(): Memory
}