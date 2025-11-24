package com.example.thread.thread.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.usecase.GetAllCryptoTickerUseCase
import com.example.thread.thread.domain.usecase.ObserveCryptoTickerRealTimeUseCase
import com.example.thread.thread.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllCryptoTickerUseCase: GetAllCryptoTickerUseCase,
    private val observeCryptoTickerRealTimeUseCase: ObserveCryptoTickerRealTimeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    private val watchList = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "ADAUSDT")

    init {
        loadInitialData()
//        observeRealTimeTicker()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when(val result = getAllCryptoTickerUseCase.invoke()) {
                is Resource.Success -> {
                    val filteredList = result.data?.filter { crypto ->
                        watchList.contains(crypto.symbol)
                    } ?: emptyList()

                    _state.value = _state.value.copy(
                        cryptoList = filteredList,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value.copy(isLoading = false, error = result.error?.message)
                }
            }

        }
    }

    private fun observeRealTimeTicker() {
        watchList.forEach { symbol ->
            viewModelScope.launch {
                observeCryptoTickerRealTimeUseCase.invoke(symbol).collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { ticker ->
                                updateTickerInList(ticker)
                            }
                        }
                        is Resource.Error -> {
                            _state.value.copy(error = result.error?.message)
                        }
                    }
                }
            }
        }

    }

    private fun updateTickerInList(newTicker: CryptoTicker) {
        val currentList = _state.value.cryptoList.toMutableList()
        val index = currentList.indexOfFirst { it.symbol == newTicker.symbol }

        if (index != -1) {
            currentList[index] = newTicker
            _state.value = _state.value.copy(cryptoList = currentList)
        } else {
            currentList.add(newTicker)
            _state.value = _state.value.copy(cryptoList = currentList)
        }
    }

    fun onRefresh() {
        loadInitialData()
    }
}