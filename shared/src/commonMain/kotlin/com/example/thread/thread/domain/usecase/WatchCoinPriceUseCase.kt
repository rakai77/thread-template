package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.model.coindesk.CryptoStreamUpdate
import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.utils.Constant
import kotlinx.coroutines.flow.Flow

class WatchCoinPriceUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(
        fromSymbol: String,
        toSymbol: String = Constant.DEFAULT_CURRENCY
    ): Flow<Result<CryptoStreamUpdate>> {
        return repository.watchCoinPrice(fromSymbol, toSymbol)
    }
}