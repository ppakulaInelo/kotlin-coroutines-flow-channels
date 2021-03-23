package com.example.rxvsco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rxvsco.api.CatFactsApiService
import com.example.rxvsco.stream.StreamService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

@FlowPreview
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val composite = CompositeDisposable()
    private lateinit var job: Job

    private val apiService = CatFactsApiService()
    private val streamService = StreamService()

    private val viewModel = MainViewModel(apiService, streamService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxExample()
        coExample()
        flowExample()
        viewModel.viewModelScopeExample()
        rxAdapterExample()
        coAdapterExample()
        defaultChannelExample()
        broadcastChannelExample()
    }

    override fun onDestroy() {
        composite.clear()
        job.cancel()
        super.onDestroy()
    }

    private fun rxExample() {
        apiService.getRandomFactRx()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ catFact ->
                    println("Rx Cat fact: ${catFact.text}")
                }, { error ->
                    println("Rx error: $error")
                })
                .addTo(composite)
    }

    private fun coExample() {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            println("Co error: $exception")
        }

        job = GlobalScope.launch(errorHandler) {
            val catFact = apiService.getRandomFactCoAsync().await()
            println("Co Cat fact: ${catFact.text}")
        }
    }

    private fun rxAdapterExample() {
        val deferred = apiService.getRandomFactRx()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .asCo(composite)

        GlobalScope.launch {
            println("Co Cat fact: ${deferred.await().text}")
        }
    }

    private fun coAdapterExample() {
        GlobalScope.launch {
            apiService
                    .getRandomFactCoAsync()
                    .asRx()
                    .subscribe({ catFact ->
                        println("Rx Cat fact: ${catFact.text}")
                    }, { error ->
                        println("Rx error: $error")
                    })
                    .addTo(composite)
        }
    }

    private fun flowExample() {
        GlobalScope.launch() {
            apiService
                    .getRandomFactFlow()
                    .flowOn(Dispatchers.Main)
                    .catch { println("Co error: $it") }
                    .collect { println("Co Cat fact: ${it.text}") }
        }
    }

    private fun defaultChannelExample() {
        GlobalScope.launch {
            streamService.numberProducer.consumeEach { println("A numberProducer: $it") }
        }
        GlobalScope.launch {
            streamService.numberProducer.consumeEach { println("B numberProducer: $it") }
        }
    }

    private fun broadcastChannelExample() {
        GlobalScope.launch {
            streamService.signProducer.consumeEach { println("C numberProducer: $it") }
        }
        GlobalScope.launch {
            streamService.signProducer.asFlow().collect { println("D numberProducer: $it") }
        }
    }
}

