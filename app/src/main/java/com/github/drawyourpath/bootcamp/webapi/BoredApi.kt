package com.github.drawyourpath.bootcamp.webapi

import retrofit2.Call
import retrofit2.http.GET

interface BoredApi {
    @GET("activity")
    fun getActivity(): Call<BoredActivity>
}