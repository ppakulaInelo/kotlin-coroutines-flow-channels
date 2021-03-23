package com.example.rxvsco

import com.example.rxvsco.api.CatFactsApiService
import com.example.rxvsco.api.models.CatFact
import com.example.rxvsco.stream.StreamService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*

@ExperimentalCoroutinesApi
class CoUnitTests {

    private val apiService = mockk<CatFactsApiService>()

    private val streamService = StreamService()

    @Test
    fun `Given example When getRandomFactCo Then success`() = runBlockingTest {
        // Given
        coEvery { apiService.getRandomFactCoAsync().await() } returns mockCatFact

        // When
        val randomCatFact = apiService.getRandomFactCoAsync().await()

        // Then
        assertEquals("text", randomCatFact.text)
        coVerify { apiService.getRandomFactCoAsync().await() }
    }

    @Test
    fun `Given example When getRandomFactFlow Then success`() = runBlockingTest {
        // Given
        coEvery { apiService.getRandomFactFlow() } returns flow { emit(mockCatFact) }

        // When
        val randomCatFact = apiService.getRandomFactFlow()

        // Then
        assertEquals("text", randomCatFact.single().text)
        coVerify { apiService.getRandomFactFlow() }
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `Given example When signProducer running Then success`() = runBlockingTest {
        // Given
        launch(Dispatchers.IO) {
            streamService.signProducer.consumeEach { println("co")  }
            delay(5000)
        }
        streamService.signProducer.cancel()
        joinAll() // Tak jak by wątek nie chciał poczekać

        // When

        // Then
        coVerify(atLeast = 3) { println("co") }
    }

    companion object {
        val mockCatFact = CatFact("1", "2", "3", "text")
    }
}