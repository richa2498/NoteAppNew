package com.rudrik.noteapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static Api instance = null;
    private static Retrofit retrofit = null;
    public static String BASE_URL = "https://maps.googleapis.com/maps/api/";

    public com.rudrik.noteapp.retrofit.services services;

    // Keep your services here, build them in buildRetrofit method later

    public static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }

        return instance;
    }

    // Build retrofit once when creating a single instance
    private Api() {
        // Implement a method to build your retrofit
        buildRetrofit();
    }

    private static void buildRetrofit() {
/*

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
*/

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static Retrofit getClient() {
        return retrofit;
    }

    public com.rudrik.noteapp.retrofit.services getServices() {
        return getClient().create(com.rudrik.noteapp.retrofit.services.class);
    }
}

