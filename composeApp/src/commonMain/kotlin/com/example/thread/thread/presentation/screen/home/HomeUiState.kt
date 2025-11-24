package com.example.thread.thread.presentation.screen.home

import com.example.thread.thread.domain.model.CryptoTicker

data class HomeUiState(
    val cryptoList: List<CryptoTicker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)