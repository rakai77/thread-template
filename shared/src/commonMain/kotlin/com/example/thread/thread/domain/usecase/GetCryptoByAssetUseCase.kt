package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.data.Result
import com.example.thread.thread.domain.model.CryptoTicker

class GetCryptoByAssetUseCase(
    private val repository: CryptoRepository
) {

    /**
     * Execute use case
     * @param base Base asset (e.g., "BTC", "ETH")
     * @param quote Quote asset (e.g., "USD", "EUR"), defaults to "USD"
     * @return Result with list of CryptoTicker
     */
    suspend operator fun invoke(
        base: String,
        quote: String = "USD"
    ): Result<List<CryptoTicker>> {
        // Validate inputs
        if (base.isBlank()) {
            return Result.Error(
                Exception("Base asset cannot be empty"),
                "Base asset cannot be empty"
            )
        }

        return repository.getCryptoByAsset(
            base = base.trim(),
            quote = quote.trim()
        )
    }
}