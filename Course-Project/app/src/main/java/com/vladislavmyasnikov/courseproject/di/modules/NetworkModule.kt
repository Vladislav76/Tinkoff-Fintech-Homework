package com.vladislavmyasnikov.courseproject.di.modules

import com.vladislavmyasnikov.courseproject.data.network.FintechPortalApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
    fun provideRxJava2CallAdapterFactory(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(url: String, client: OkHttpClient, gsonConverterFactory: GsonConverterFactory,
                        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(client)
                .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): FintechPortalApi = retrofit.create(FintechPortalApi::class.java)
}