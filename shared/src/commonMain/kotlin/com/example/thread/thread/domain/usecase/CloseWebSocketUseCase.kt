package com.example.thread.thread.domain.usecase

import com.example.thread.thread.domain.repository.CryptoRepository

class CloseWebSocketUseCase(
    private val repository: CryptoRepository,
) {

    suspend operator fun invoke() {
        repository.closeWebSocket()
    }
}