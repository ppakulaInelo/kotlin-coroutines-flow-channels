package com.example.rxvsco.api

import com.example.rxvsco.api.models.CatFact
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface ICatFactsApiService {

    fun getRandomFactRx(): Single<CatFact>

    suspend fun getRandomFactCoAsync(): Deferred<CatFact>

    fun getRandomFactFlow(): Flow<CatFact>
}