package com.vladislavmyasnikov.courseproject.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    fun provideApplicationContext(): Context = context.applicationContext
}