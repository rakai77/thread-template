package com.example.thread.thread.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thread.thread.domain.model.coindesk.CoinMarketCap
import com.example.thread.thread.domain.usecase.GetTopMarketUseCase
import com.example.thread.thread.domain.usecase.WatchTopMarketCapUseCase
import com.example.thread.thread.utils.ErrorHandler
import com.example.thread.thread.utils.getCurrentTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTopMarketCapUseCase: GetTopMarketUseCase,
    private val watchTopMarketCapUseCase: WatchTopMarketCapUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CryptoListState())
    val state = _state.asStateFlow()

    private val _uiState = MutableStateFlow<CryptoUiState>(CryptoUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private var webSocketJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 3

    init {
        fetchTopMarketCap()
    }

    fun fetchTopMarketCap(
        currency: String = "IDR",
        limit: Int = 10,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = !isRefresh,
                        isRefreshing = isRefresh,
                        error = null,
                        currency = currency
                    )
                }

                if (_state.value.coins.isEmpty() && !isRefresh) {
                    _uiState.value = CryptoUiState.Loading
                }

                getTopMarketCapUseCase(currency, limit, page = 0)
                    .onSuccess { result ->
                        val coins = result.coins

                        _state.update { currentState ->
                            currentState.copy(
                                coins = coins,
                                isLoading = false,
                                isRefreshing = false,
                                error = null,
                                lastUpdateTime = getCurrentTimeMillis()
                            )
                        }

                        _uiState.value = if (coins.isNotEmpty()) {
                            CryptoUiState.Success(coins)
                        } else {
                            CryptoUiState.Error("No data available")
                        }

                        // ✅ Start WebSocket after successful HTTP fetch
                        if (coins.isNotEmpty()) {
                            startLiveUpdates(currency, limit)
                        }

                        println("✅ Loaded ${coins.size} coins")
                        retryCount = 0
                    }
                    .onFailure { error ->
                        val appError = ErrorHandler.handleException(error)
                        val errorMessage = appError.message

                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = errorMessage
                            )
                        }

                        _uiState.value = CryptoUiState.Error(errorMessage)

                        println("❌ Error: $errorMessage")

                        // ✅ Auto retry logic
                        if (retryCount < maxRetries && !isRefresh) {
                            retryCount++
                            delay(2000 * retryCount.toLong())
                            fetchTopMarketCap(currency, limit, isRefresh)
                        }
                    }

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to fetch data"

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = errorMessage
                    )
                }

                _uiState.value = CryptoUiState.Error(errorMessage)
                println("❌ Exception: $errorMessage")
            }
        }
    }

    fun startLiveUpdates(
        currency: String = "IDR",
        limit: Int = 10
    ) {
        webSocketJob?.cancel()

        webSocketJob = viewModelScope.launch {
            try {
                println("🔌 Starting WebSocket updates for currency: $currency")

                // ✅ Start WebSocket and collect updates
                watchTopMarketCapUseCase(currency, limit)
                    .collectLatest { result ->
                        result.onSuccess { streamUpdates ->
                            if (streamUpdates.isNotEmpty()) {
                                val currentCoins = _state.value.coins.toMutableList()

                                streamUpdates.forEach { update ->
                                    // Find coin by symbol match
                                    val index = currentCoins.indexOfFirst {
                                        it.symbol == update.symbol || it.name == update.symbol
                                    }

                                    if (index != -1) {
                                        val coin = currentCoins[index]
                                        val updatedMarketData = coin.marketData?.copy(
                                            price = update.price,
                                            change24h = update.change,
                                            changePct24h = update.changePct,
                                            volume24h = update.volume,
                                            lastUpdate = update.timestamp
                                        )

                                        currentCoins[index] = coin.copy(
                                            marketData = updatedMarketData
                                        )

                                        println("🔄 Updated ${coin.symbol}: ${update.price}")
                                    }
                                }

                                _state.update { currentState ->
                                    currentState.copy(
                                        coins = currentCoins,
                                        lastUpdateTime = getCurrentTimeMillis(),
                                        error = null
                                    )
                                }

                                println("✅ Live update: ${streamUpdates.size} coins updated")
                            }
                        }.onFailure { error ->
                            println("⚠️ WebSocket error: ${error.message}")
                            // Optionally retry after delay
                            delay(5000)
                            startLiveUpdates(currency, limit)
                        }
                    }
            } catch (e: Exception) {
                println("❌ WebSocket exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun stopLiveUpdates() {
        webSocketJob?.cancel()
        webSocketJob = null
        println("⏹️ Stopped live updates")
    }

    fun refresh() {
        fetchTopMarketCap(
            currency = _state.value.currency,
            isRefresh = true
        )
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopLiveUpdates()
    }
}