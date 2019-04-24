package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.network.FintechService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [OkHttpClientModule::class])
class NetworkModule {

    @Provides
    @Singleton
    fun provideUrl(): String = "https://fintech.tinkoff.ru/api/"

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(url: String, client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit) = retrofit.create(FintechService::class.java)
}