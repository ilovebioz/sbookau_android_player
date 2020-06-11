package com.sectic.sbookau.ultils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bioz on 9/23/2014.
 */
public class RestAPIServiceBuilder {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();
/*
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(DefSetting.sRestServerUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit retrofitForDownload = new Retrofit.Builder()
            .baseUrl(DefSetting.sRestServerUrl)
            .client(okHttpClient)
            .build();

    public static void ReInitRetrofit(String iSServerUrl)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(iSServerUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitForDownload = new Retrofit.Builder()
                .baseUrl(iSServerUrl)
                .client(okHttpClient)
                .build();
    }
*/
    public static Retrofit CreatRetrofitForAPI(){
        return new Retrofit.Builder()
                .baseUrl(DefSetting.sRestServerUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit CreatRetrofitForAPI(String iSServerUrl){
        return new Retrofit.Builder()
                .baseUrl(iSServerUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit CreatRetrofitForDownload(){
        return new Retrofit.Builder()
                .baseUrl(DefSetting.sRestServerUrl)
                .client(okHttpClient)
                .build();
    }

    public static Retrofit CreatRetrofitForDownload(String iSServerUrl){
        return new Retrofit.Builder()
                .baseUrl(iSServerUrl)
                .client(okHttpClient)
                .build();
    }
}
