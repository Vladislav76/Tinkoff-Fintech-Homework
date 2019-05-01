package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.di.annotations.ActivityScope
import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.domain.repositories.*
import com.vladislavmyasnikov.courseproject.ui.viewmodels.*
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    fun provideLoginViewModelFactory(repository: ILoginRepository) =
            LoginViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideTaskListViewModelFactory(repository: ITaskRepository) =
            TaskListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideLectureListViewModelFactory(repository: ILectureRepository) =
            LectureListViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideStudentListViewModelFactory(repository: IStudentRepository, repository2: IProfileRepository) =
            StudentListViewModelFactory(repository, repository2)

    @Provides
    @FragmentScope
    fun provideProfileViewModelFactory(repository: IProfileRepository) =
            ProfileViewModelFactory(repository)

    @Provides
    @FragmentScope
    fun provideCourseViewModelFactory(repo1: ICourseRepository, repo2: IProfileRepository, repo3: IStudentRepository, repo4: ILectureRepository) =
            CourseViewModelFactory(repo1, repo2, repo3, repo4)
}