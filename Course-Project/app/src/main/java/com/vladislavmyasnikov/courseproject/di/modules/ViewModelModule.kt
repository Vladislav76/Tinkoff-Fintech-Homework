package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.repositories_impl.*
import com.vladislavmyasnikov.courseproject.di.annotations.ActivityScope
import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ILoginRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ITaskRepository
import com.vladislavmyasnikov.courseproject.ui.viewmodels.*
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    fun provideLoginViewModelFactory(repository: ILoginRepository) = LoginViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideTaskListViewModelFactory(repository: ITaskRepository) = TaskListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideLectureListViewModelFactory(repository: ILectureRepository) = LectureListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideStudentListViewModelFactory(repository: StudentRepository) = StudentListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideProfileViewModelFactory(repository: ProfileRepository) = ProfileViewModelFactory(repository)
}