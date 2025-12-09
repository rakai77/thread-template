package com.example.thread.thread.utils

object Constant {
    const val BASE_URL = "min-api.cryptocompare.com"

    // ✅ FIX: Use v2 WebSocket endpoint
    const val WS_HOST = "streamer.cryptocompare.com"
    const val WS_PATH = "/v2"  // Changed from "/"

    const val API_KEY = "a34e5d75e2480aeb566247a339c844221a4eb118b05141263ac159cb7bfc8fbb"

    // WebSocket Subscription Types
    const val SUB_TYPE_TRADE = "0"           // Individual trades
    const val SUB_TYPE_AGGREGATE = "5"       // Aggregate Index (CCCAGG)
    const val SUB_TYPE_TICKER = "2"          // Current aggregate
    const val SUB_TYPE_TOP_MARKET_CAP = "21" // Not available in v2 streaming

    // Default Parameters
    const val DEFAULT_CURRENCY = "IDR"
    const val DEFAULT_LIMIT = 100
    const val DEFAULT_PAGE = 0
}