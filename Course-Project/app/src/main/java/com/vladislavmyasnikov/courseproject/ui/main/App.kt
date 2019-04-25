package com.vladislavmyasnikov.courseproject.ui.main

import android.app.Application
import com.vladislavmyasnikov.courseproject.di.components.DaggerRepositoryComponent
import com.vladislavmyasnikov.courseproject.di.components.RepositoryComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule

class App: Application() {

    lateinit var repositoryComponent: RepositoryComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        repositoryComponent = DaggerRepositoryComponent.builder().contextModule(ContextModule(this)).build()
    }



    companion object {

        lateinit var instance: App
    }
}