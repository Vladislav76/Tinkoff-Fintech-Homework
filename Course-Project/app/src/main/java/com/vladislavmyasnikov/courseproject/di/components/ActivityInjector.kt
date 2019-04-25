package com.vladislavmyasnikov.courseproject.di.components

import com.vladislavmyasnikov.courseproject.di.annotations.ActivityScope
import com.vladislavmyasnikov.courseproject.di.modules.ViewModelModule
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import dagger.Component

@Component(modules = [ViewModelModule::class])
@ActivityScope
interface ActivityInjector {

    fun injectAuthorizationActivity(activity: AuthorizationActivity)
}