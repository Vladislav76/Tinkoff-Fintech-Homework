package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.di.annotations.ActivityScope
import com.vladislavmyasnikov.courseproject.di.modules.MemoryModule
import com.vladislavmyasnikov.courseproject.di.modules.ViewModelModule
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.AuthorizationActivity
import dagger.Component
import javax.inject.Singleton

@Component(dependencies = [AppComponent::class], modules = [ViewModelModule::class])
@ActivityScope
interface AuthorizationActivityInjector {

    fun injectAuthorizationActivity(activity: AuthorizationActivity)
}

@Component(modules = [MemoryModule::class])
@Singleton
interface AppInjector {

    fun inject(app: App)
}