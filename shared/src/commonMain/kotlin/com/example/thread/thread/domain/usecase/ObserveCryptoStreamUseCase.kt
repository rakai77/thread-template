package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import com.example.thread.thread.data.Result
import com.example.thread.thread.domain.model.CryptoTicker

class ObserveCryptoStreamUseCase(
    private val repository: CryptoRepository
) {

    /**
     * Execute use case
     * @param symbols List of trading pairs to subscribe
     * Example: ["BTC-USD", "ETH-USD"]
     * @return Flow of Result<CryptoTicker>
     */
    operator fun invoke(symbols: List<String>): Flow<Result<CryptoTicker>> {
        // Convert symbols to CoinDesk subscription format
        // Format: "2~Exchange~Base~Quote"
        val subscriptions = symbols.map { symbol ->
            val parts = symbol.split("-")
            if (parts.size == 2) {
                "2~Coinbase~${parts[0]}~${parts[1]}"
            } else {
                symbol // Use as-is if format is different
            }
        }

        return repository.observeCryptoStream(subscriptions)
    }

    /**
     * Subscribe with custom format
     */
    fun invokeWithCustomFormat(subscriptions: List<String>): Flow<Result<CryptoTicker>> {
        return repository.observeCryptoStream(subscriptions)
    }
}