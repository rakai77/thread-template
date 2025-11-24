package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.repository.CryptoRepository

class ObserveCryptoTickerRealTimeUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(symbol: String) = repository.observeTickerRealTime(symbol)

}