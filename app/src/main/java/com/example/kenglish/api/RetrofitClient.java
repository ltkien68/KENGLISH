package com.example.kenglish.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

//    private static final String BASE_URL = "http://15.15.12.137:3000/";
      private static final String BASE_URL = "https://desktops-merit-congressional-mount.trycloudflare.com/";


    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {

            OkHttpClient okHttpClient =
                    new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit;
    }
}