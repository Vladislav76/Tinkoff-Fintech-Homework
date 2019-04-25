package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.di.modules.AdapterModule
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.di.modules.ViewModelModule
import com.vladislavmyasnikov.courseproject.ui.courses.AcademicPerformanceFragment
import com.vladislavmyasnikov.courseproject.ui.courses.LectureListFragment
import com.vladislavmyasnikov.courseproject.ui.courses.StudentListFragment
import com.vladislavmyasnikov.courseproject.ui.courses.TaskListFragment
import com.vladislavmyasnikov.courseproject.ui.profile.ProfileFragment
import dagger.Component

@Component(modules = [ViewModelModule::class, AdapterModule::class])
@FragmentScope
interface FragmentInjector {

    fun injectStudentListFragment(fragment: StudentListFragment)
    fun injectLectureListFragment(fragment: LectureListFragment)
    fun injectTaskListFragment(fragment: TaskListFragment)
    fun injectProfileFragment(fragment: ProfileFragment)
    fun injectAcademicPerformanceFragment(fragment: AcademicPerformanceFragment)
}