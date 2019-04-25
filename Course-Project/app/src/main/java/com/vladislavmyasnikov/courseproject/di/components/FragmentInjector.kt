package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.di.modules.AdapterModule
import com.vladislavmyasnikov.courseproject.di.modules.ViewModelModule
import com.vladislavmyasnikov.courseproject.ui.courses.AcademicPerformanceFragment
import com.vladislavmyasnikov.courseproject.ui.courses.LectureListFragment
import com.vladislavmyasnikov.courseproject.ui.courses.StudentListFragment
import com.vladislavmyasnikov.courseproject.ui.courses.TaskListFragment
import com.vladislavmyasnikov.courseproject.ui.profile.ProfileFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class, AdapterModule::class])
@FragmentScope
interface StudentListFragmentInjector {

    fun injectStudentListFragment(fragment: StudentListFragment)
}



@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class, AdapterModule::class])
@FragmentScope
interface LectureListFragmentInjector {

    fun injectLectureListFragment(fragment: LectureListFragment)
}



@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class, AdapterModule::class])
@FragmentScope
interface TaskListFragmentInjector {

    fun injectTaskListFragment(fragment: TaskListFragment)
}



@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class])
@FragmentScope
interface ProfileFragmentInjector {

    fun injectProfileFragment(fragment: ProfileFragment)
}



@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class, AdapterModule::class])
@FragmentScope
interface AcademicPerformanceFragmentInjector {

    fun injectAcademicPerformanceFragment(fragment: AcademicPerformanceFragment)
}