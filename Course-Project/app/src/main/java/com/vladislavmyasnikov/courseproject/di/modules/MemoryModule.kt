package com.vladislavmyasnikov.courseproject.di.modules

import android.content.Context
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class MemoryModule {

    @Provides
    @Singleton
    fun provideMemory(context: Context) = Memory(context)
}