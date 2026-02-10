package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.model.coindesk.TopMarketCapResult
import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.utils.Constant
import kotlinx.coroutines.flow.Flow

class GetTopMarketUseCase(
    private val repository: CryptoRepository
) {

    suspend operator fun invoke(
        currency: String = Constant.DEFAULT_CURRENCY,
        limit: Int = Constant.DEFAULT_LIMIT,
        page: Int = Constant.DEFAULT_PAGE
    ): Flow<TopMarketCapResult> {  // ✅ Return domain model
        return repository.getTopMarketCap(currency, limit, page)
    }
}