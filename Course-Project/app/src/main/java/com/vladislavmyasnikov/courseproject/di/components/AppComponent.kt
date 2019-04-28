package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.data.repositories_impl.*
import com.vladislavmyasnikov.courseproject.di.modules.RepositoryModule
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RepositoryModule::class])
@Singleton
interface AppComponent {

    fun getLectureRepository(): ILectureRepository
    fun getTaskRepository(): ITaskRepository
    fun getStudentRepository(): StudentRepository
    fun getLoginRepository(): ILoginRepository
    fun getProfileRepository(): ProfileRepository
}