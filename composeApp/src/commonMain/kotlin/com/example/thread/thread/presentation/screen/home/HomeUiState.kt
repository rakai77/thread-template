package com.example.thread.thread.presentation.screen.home

import com.example.thread.thread.data.model.ErrorState
import com.example.thread.thread.domain.model.CryptoTicker

data class HomeUiState(
    val cryptoList: List<CryptoTicker> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorState? = null,
    val isRefreshing: Boolean = false
)

