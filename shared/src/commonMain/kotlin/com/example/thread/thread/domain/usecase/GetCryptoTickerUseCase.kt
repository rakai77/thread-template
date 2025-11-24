package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.repository.CryptoRepository

class GetCryptoTickerUseCase(
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke(symbol: String) = cryptoRepository.get24HourTicker(symbol)
}