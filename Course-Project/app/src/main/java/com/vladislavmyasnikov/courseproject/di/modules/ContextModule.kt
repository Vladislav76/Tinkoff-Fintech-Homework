package com.vladislavmyasnikov.courseproject.di.modules

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.vladislavmyasnikov.courseproject.di.annotations.FragmentScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ContextModule(private val context: Context) {

    @Provides
    @Named("application_context")
    fun provideApplicationContext(): Context = context.applicationContext

    @Provides
    fun provideContext(): Context = context
}