package com.vladislavmyasnikov.courseproject.di.modules

import android.content.Context
import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import com.vladislavmyasnikov.courseproject.ui.adapters.LectureAdapter
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.adapters.TaskAdapter
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class AdapterModule {

    @Provides
    @FragmentScope
    fun provideStudentAdapter(context: Context): StudentAdapter = StudentAdapter(context)

    @Provides
    @FragmentScope
    fun provideLectureAdapter(): LectureAdapter = LectureAdapter()

    @Provides
    @FragmentScope
    fun provideTaskAdapter(): TaskAdapter = TaskAdapter()
}