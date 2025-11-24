package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.repository.CryptoRepository

class GetAllCryptoTickerUseCase(
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke() = cryptoRepository.getAllTickers()
}