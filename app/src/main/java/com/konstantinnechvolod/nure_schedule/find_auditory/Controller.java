package com.konstantinnechvolod.nure_schedule.find_auditory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {
    static final String BASE_URL = "http://cist.nure.ua/ias/app/tt/";

    public static CISTAPI getApi(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CISTAPI cistapi = retrofit.create(CISTAPI.class);

        return cistapi;
    }
}
