package com.example.rxvsco.api

import com.example.rxvsco.addTo
import com.example.rxvsco.api.models.CatFact
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatFactsApiService : ICatFactsApiService {

    private val BASE_URL = "https://cat-fact.herokuapp.com"
    private var enpoints: CatFactsEndpoints

    init {
        val gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        enpoints = retrofit.create(CatFactsEndpoints::class.java)
    }

    override fun getRandomFactRx(): Single<CatFact> {
        return Single.create { emitter ->
            enpoints.getRandom().enqueue(object : Callback<CatFact> {
                override fun onResponse(call: Call<CatFact>, response: Response<CatFact>) {
                    emitter.onSuccess(response.body()!!)
                }

                override fun onFailure(call: Call<CatFact>, t: Throwable) {
                    emitter.onError(t)
                }
            })
        }
    }

    override suspend fun getRandomFactCoAsync(): Deferred<CatFact> {
        val deferred = CompletableDeferred<CatFact>()

        enpoints.getRandom().enqueue(object : Callback<CatFact> {
            override fun onResponse(call: Call<CatFact>, response: Response<CatFact>) {
                deferred.complete(response.body()!!)
            }

            override fun onFailure(call: Call<CatFact>, t: Throwable) {
                deferred.completeExceptionally(t)
            }
        })

        return deferred
    }

    override fun getRandomFactFlow(): Flow<CatFact> {
        return flow {
            emit(getRandomFactCoAsync().await())
        }
    }
}


