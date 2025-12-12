package com.example.thread.thread.domain.usecase

import com.example.thread.thread.data.Result
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.repository.CryptoRepository


class GetCryptoListUseCase(
    private val repository: CryptoRepository
) {
    /**
     * Execute use case
     * @param market Optional market filter
     * @param instrument Optional instrument filter
     * @return Result with list of CryptoTicker (max 10 items)
     */
    suspend operator fun invoke(
        market: String? = null,
        instrument: String? = null
    ): Result<List<CryptoTicker>> {
        return repository.getCryptoList(market, instrument)
    }
}
