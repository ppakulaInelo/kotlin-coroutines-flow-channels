package com.example.rxvsco

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rxvsco.api.CatFactsApiService
import com.example.rxvsco.stream.StreamService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class MainViewModel @ExperimentalCoroutinesApi constructor(
    private val apiService: CatFactsApiService,
    private val streamService: StreamService
) : ViewModel() {

    fun viewModelScopeExample() = viewModelScope.launch {
        apiService.getRandomFactFlow()
    }
}