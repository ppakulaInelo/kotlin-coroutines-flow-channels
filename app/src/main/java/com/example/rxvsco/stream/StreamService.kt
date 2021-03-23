package com.example.rxvsco.stream

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce

@ExperimentalCoroutinesApi
class StreamService : IStreamService {

    override val numberProducer = CoroutineScope(Dispatchers.IO).produce<Int> {
        var x = 1
        while (true) {
            delay(1000)
            send(x++)
        }
    }

    override val signProducer = BroadcastChannel<String>(10)
            .also {
                CoroutineScope(Dispatchers.IO).launch {
                    var x = 1
                    while (true) {
                        delay(1000)
                        it.send(x++.toString())
                    }
                }
            }

}