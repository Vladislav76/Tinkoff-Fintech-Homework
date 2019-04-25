package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.repositories.*
import com.vladislavmyasnikov.courseproject.di.annotations.ActivityScope
import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.ui.viewmodels.*
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    fun provideLoginViewModelFactory(repository: LoginRepository) = LoginViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideTaskListViewModelFactory(repository: TaskRepository) = TaskListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideLectureListViewModelFactory(repository: LectureRepository) = LectureListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideStudentListViewModelFactory(repository: StudentRepository) = StudentListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideProfileViewModelFactory(repository: ProfileRepository) = ProfileViewModelFactory(repository)
}