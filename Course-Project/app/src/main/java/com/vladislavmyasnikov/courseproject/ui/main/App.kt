package com.vladislavmyasnikov.courseproject.ui.main

import android.app.Application
import android.content.Intent
import com.vladislavmyasnikov.courseproject.data.prefs.Memory
import com.vladislavmyasnikov.courseproject.di.components.AppComponent
import com.vladislavmyasnikov.courseproject.di.components.DaggerAppComponent
import com.vladislavmyasnikov.courseproject.di.components.DaggerAppInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import javax.inject.Inject

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appComponent = DaggerAppComponent.builder().contextModule(ContextModule(this)).build()
    }

    fun logout() {
        val intent = Intent(applicationContext, AuthorizationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        appComponent.getMemory().clear()
    }

    companion object {
        lateinit var INSTANCE: App
        lateinit var appComponent: AppComponent
    }
}