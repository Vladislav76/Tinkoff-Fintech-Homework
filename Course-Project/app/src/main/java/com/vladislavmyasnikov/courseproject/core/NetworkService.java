package com.vladislavmyasnikov.courseproject.core;

import com.vladislavmyasnikov.courseproject.ui.callbacks.FintechService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "https://fintech.tinkoff.ru/api/";
    private static NetworkService sInstance;

    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance() {
        if (sInstance == null) {
            synchronized (NetworkService.class) {
                if (sInstance == null) {
                    sInstance = new NetworkService();
                }
            }
        }
        return sInstance;
    }

    public FintechService getFintechService() {
        return mRetrofit.create(FintechService.class);
    }
}
