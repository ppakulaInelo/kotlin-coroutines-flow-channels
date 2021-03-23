package com.example.rxvsco.api

import com.example.rxvsco.api.models.CatFact
import retrofit2.Call
import retrofit2.http.GET

interface CatFactsEndpoints {

    @GET("/facts/random")
    fun getRandom(): Call<CatFact>
}

