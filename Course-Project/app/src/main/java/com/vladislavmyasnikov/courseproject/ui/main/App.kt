package com.vladislavmyasnikov.courseproject.ui.main

import android.app.Application
import com.vladislavmyasnikov.courseproject.di.components.DaggerAppComponent
import com.vladislavmyasnikov.courseproject.di.components.AppComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().contextModule(ContextModule(this)).build()
    }



    companion object {

        lateinit var appComponent: AppComponent
    }
}