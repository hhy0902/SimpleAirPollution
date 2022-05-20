package com.example.simpleairpollution

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("data/2.5/air_pollution?&appid=3bbea22f826e4eef49dc445bd1114a75")
    fun getAirPollution(
        @Query("lat") lat : String,
        @Query("lon") lon : String
    )
    : Call<Pollution>
}