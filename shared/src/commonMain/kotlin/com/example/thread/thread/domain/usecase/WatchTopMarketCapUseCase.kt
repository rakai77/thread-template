package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.model.coindesk.CryptoStreamUpdate
import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.utils.Constant
import kotlinx.coroutines.flow.Flow

class WatchTopMarketCapUseCase(
    private val repository: CryptoRepository
) {

    operator fun invoke(
        currency: String = Constant.DEFAULT_CURRENCY,
        limit: Int = 10
    ): Flow<Result<List<CryptoStreamUpdate>>> {  // ✅ Return domain model
        return repository.watchTopMarketCap(currency, limit)
    }
}