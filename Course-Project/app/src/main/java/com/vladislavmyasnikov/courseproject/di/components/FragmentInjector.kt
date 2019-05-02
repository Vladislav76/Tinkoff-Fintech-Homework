package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.di.modules.AdapterModule
import com.vladislavmyasnikov.courseproject.di.modules.ViewModelModule
import com.vladislavmyasnikov.courseproject.ui.courses.*
import com.vladislavmyasnikov.courseproject.ui.events.EventListFragment
import com.vladislavmyasnikov.courseproject.ui.events.EventsFragment
import com.vladislavmyasnikov.courseproject.ui.events.EventsNestedFragment
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

    fun injectAcademicPerformanceFragment(fragment: TopStudentsFragment)
}

@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class])
@FragmentScope
interface CoursesFragmentInjector {

    fun inject(fragment: CoursesFragment)
}

@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class, AdapterModule::class])
@FragmentScope
interface EventsFragmentInjector {

    fun inject(fragment: EventsNestedFragment)
    fun inject(fragment: EventsFragment)
    fun inject(fragment: EventListFragment)
}