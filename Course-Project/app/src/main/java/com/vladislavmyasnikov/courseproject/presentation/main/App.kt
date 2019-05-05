package com.vladislavmyasnikov.courseproject.presentation.main

import android.app.Application
import android.content.Intent
import android.util.Log
import com.vladislavmyasnikov.courseproject.di.components.AppComponent
import com.vladislavmyasnikov.courseproject.di.components.DaggerAppComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

class App: Application() {

    private val disposables = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appComponent = DaggerAppComponent.builder().contextModule(ContextModule(this)).build()

        RxJavaPlugins.setErrorHandler { error ->
            if (error is UndeliverableException) {
                return@setErrorHandler
            }
            throw error
        }
    }

    fun logout() {
        val intent = Intent(applicationContext, AuthorizationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        appComponent.getMemory().clear()
        disposables.add(
                appComponent.getLectureRepository().deleteLectures()
                        .subscribeOn(Schedulers.io())
                        .mergeWith(
                                appComponent.getStudentRepository().deleteStudents().subscribeOn(Schedulers.io())
                        ).observeOn(AndroidSchedulers.mainThread())
                        .subscribe { Log.d("APP", "Data are deleted from DB") }
        )
    }

    companion object {
        lateinit var INSTANCE: App
        lateinit var appComponent: AppComponent
    }
}