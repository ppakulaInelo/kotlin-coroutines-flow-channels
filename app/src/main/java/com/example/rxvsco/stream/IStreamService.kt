package com.example.rxvsco.stream

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

@ExperimentalCoroutinesApi
interface IStreamService {

    val numberProducer: ReceiveChannel<Int>

    val signProducer: BroadcastChannel<String>
}