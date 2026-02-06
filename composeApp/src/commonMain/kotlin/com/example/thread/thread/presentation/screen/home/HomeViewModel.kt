package com.example.thread.thread.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.thread.domain.usecase.CloseWebSocketUseCase
import com.example.thread.thread.domain.usecase.GetCryptoListUseCase
import com.example.thread.thread.domain.usecase.ObserveCryptoStreamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.thread.thread.data.Result
import com.example.thread.thread.data.model.ErrorState
import com.example.thread.thread.domain.model.CryptoTicker


/**
 * ViewModel for Home Screen
 * Manages crypto list data and real-time updates
 */
class HomeViewModel(
    private val getCryptoListUseCase: GetCryptoListUseCase,
    private val observeCryptoStreamUseCase: ObserveCryptoStreamUseCase,
    private val closeWebSocketUseCase: CloseWebSocketUseCase
) : ViewModel() {

    // State flow for UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    // Track WebSocket connection
    private var isWebSocketConnected = false

    init {
        loadCryptoList()
    }

    /**
     * Load initial crypto list from REST API
     */
    fun loadCryptoList() {
        viewModelScope.launch {
            // Show loading
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getCryptoListUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            cryptoList = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    // Start WebSocket for real-time updates
                    observeWebSocketUpdates(result.data)
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = ErrorState(
                                message = result.message ?: "Failed to load crypto data",
                                canRetry = true
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Refresh crypto list (pull-to-refresh)
     */
    fun refreshCryptoList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            when (val result = getCryptoListUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            cryptoList = result.data,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = ErrorState(
                                message = result.message ?: "Failed to refresh data",
                                canRetry = true
                            )
                        )
                    }
                }
            }
        }
    }
    private fun observeWebSocketUpdates(initialList: List<CryptoTicker>) {
        if (isWebSocketConnected || initialList.isEmpty()) return

        // Build subscription list from loaded data
        val subscriptions = initialList.map { crypto ->
            "2~${crypto.market}~${crypto.base}~${crypto.quote}"
        }.distinct().take(10)

        viewModelScope.launch {
            observeCryptoStreamUseCase.invokeWithCustomFormat(subscriptions)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            isWebSocketConnected = true
                            updateCryptoInList(result.data)
                        }

                        is Result.Error -> {
                            // Don't show WebSocket errors on UI as per requirements
                            println("WebSocket Error (silent): ${result.message}")
                        }
                    }
                }
        }
    }

    private fun updateCryptoInList(updatedCrypto: CryptoTicker) {
        _uiState.update { currentState ->
            val updatedList = currentState.cryptoList.map { crypto ->
                if (crypto.instrument == updatedCrypto.instrument &&
                    crypto.market == updatedCrypto.market
                ) {
                    updatedCrypto
                } else {
                    crypto
                }
            }
            currentState.copy(cryptoList = updatedList)
        }
    }

    /**
     * Dismiss error bottom sheet
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Retry loading after error
     */
    fun retryLoading() {
        dismissError()
        loadCryptoList()
    }

    /**
     * Clean up resources
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            closeWebSocketUseCase()
        }
    }
}
