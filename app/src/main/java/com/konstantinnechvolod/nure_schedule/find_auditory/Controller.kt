package com.konstantinnechvolod.nure_schedule.find_auditory

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Controller {
    const val BASE_URL = "http://cist.nure.ua/ias/app/tt/"
    val api: CISTAPI
        get() {
            val gson = GsonBuilder()
                    .setLenient()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(CISTAPI::class.java)
        }
}