package com.vladislavmyasnikov.courseproject.di.modules

import android.content.Context
import androidx.room.Room
import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class DatabaseModule {

    @Named("database_name")
    @Provides
    @Singleton
    fun provideDatabaseName() = "local_database"

    @Provides
    @Singleton
    fun provideDatabase(context: Context, @Named("database_name") name: String) = Room.databaseBuilder(context, LocalDatabase::class.java, name).build()
}