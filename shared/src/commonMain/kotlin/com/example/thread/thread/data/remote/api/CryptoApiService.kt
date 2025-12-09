package com.example.thread.thread.data.remote.api

import com.example.thread.thread.data.remote.response.coindesk.TopMarketCapResponse
import com.example.thread.thread.utils.Constant

interface CryptoApiService {
    suspend fun getTopMarketCap(
        currency: String = Constant.DEFAULT_CURRENCY,
        limit: Int = Constant.DEFAULT_LIMIT,
        page: Int = Constant.DEFAULT_PAGE
    ): Result<TopMarketCapResponse>
}