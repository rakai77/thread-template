package com.example.thread.thread.presentation.screen.home

import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.coindesk.CoinMarketCap

data class HomeUiState(
    val cryptoList: List<CryptoTicker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CryptoUiState {
    data object Init : CryptoUiState()
    data class Loading(val isLoading: Boolean) : CryptoUiState()
    data class Success(val coins: List<CoinMarketCap>) : CryptoUiState()
    data class Error(val message: String) : CryptoUiState()
}

data class CryptoListState(
    val coins: List<CoinMarketCap> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currency: String = "IDR",
    val lastUpdateTime: Long = 0L
)