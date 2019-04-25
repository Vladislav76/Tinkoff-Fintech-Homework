package com.vladislavmyasnikov.courseproject.di.modules

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.vladislavmyasnikov.courseproject.di.annotations.ActivityScope
import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.ui.viewmodels.*
import dagger.Module
import dagger.Provides

@Module(includes = [FragmentActivityModule::class, FragmentModule::class])
class ViewModelModule {

    @Provides
    @ActivityScope
    fun provideLoginViewModel(fragmentActivity: FragmentActivity): LoginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)

    @Provides
    @FragmentScope
    fun provideStudentListViewModel(fragment: Fragment): StudentListViewModel = ViewModelProviders.of(fragment).get(StudentListViewModel::class.java)

    @Provides
    @FragmentScope
    fun provideTaskListViewModel(fragment: Fragment): TaskListViewModel = ViewModelProviders.of(fragment).get(TaskListViewModel::class.java)

    @Provides
    @FragmentScope
    fun provideLectureListViewModel(fragment: Fragment): LectureListViewModel = ViewModelProviders.of(fragment).get(LectureListViewModel::class.java)

    @Provides
    @FragmentScope
    fun provideProfileViewModel(fragment: Fragment): ProfileViewModel = ViewModelProviders.of(fragment).get(ProfileViewModel::class.java)
}