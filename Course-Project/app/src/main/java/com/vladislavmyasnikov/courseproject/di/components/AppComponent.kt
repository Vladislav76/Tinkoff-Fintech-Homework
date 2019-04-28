package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.data.repositories_impl.*
import com.vladislavmyasnikov.courseproject.di.modules.RepositoryModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RepositoryModule::class])
@Singleton
interface AppComponent {

    fun getLectureRepository(): LectureRepository
    fun getTaskRepository(): TaskRepository
    fun getStudentRepository(): StudentRepository
    fun getLoginRepository(): LoginRepository
    fun getProfileRepository(): ProfileRepository
}