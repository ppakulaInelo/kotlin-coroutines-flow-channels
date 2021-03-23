package com.example.rxvsco

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

fun Disposable.addTo(composite: CompositeDisposable) {
    composite.addAll(this)
}

fun <T> Observable<T>.asCo(composite: CompositeDisposable): Deferred<T> {
    val deferred = CompletableDeferred<T>()

    subscribe({ deferred.complete(it) }, { deferred.completeExceptionally(it) })
            .addTo(composite)

    return deferred
}

fun <T> Single<T>.asCo(composite: CompositeDisposable): Deferred<T> {
    val deferred = CompletableDeferred<T>()

    subscribe({ deferred.complete(it) }, { deferred.completeExceptionally(it) })
            .addTo(composite)

    return deferred
}

suspend fun <T> Deferred<T>.asRx(): Observable<T> {
    return try {
        Observable.just(await())
    } catch (e: Exception) {
        Observable.error(e)
    }
}