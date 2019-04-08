package com.vladislavmyasnikov.courseproject.data.network;

import android.os.SystemClock;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "https://fintech.tinkoff.ru/api/";
    private static NetworkService sInstance;

    private Retrofit mRetrofit;
    private FintechService mFintechService;

    private NetworkService() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                SystemClock.sleep(2000);

                return chain.proceed(original);
            }
        }).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        mFintechService = mRetrofit.create(FintechService.class);
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
        return mFintechService;
    }
}
