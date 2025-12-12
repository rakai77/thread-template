package com.example.thread.thread.utils

object Constants {
    // Base URLs
    const val BASE_URL_REST = "data-api.coindesk.com"
    const val BASE_URL_WEBSOCKET = "wss://data-streamer.coindesk.com"
    const val BASE_URL_AUTH = "https://auth-api.coindesk.com"

    // API Key (from your documentation)
    const val API_KEY = "a34e5d75e2480aeb566247a339c844221a4eb118b05141263ac159cb7bfc8fbb"

    // Endpoints
    const val ENDPOINT_SPOT_LATEST_TICK = "/spot/v1/latest/tick"
    const val ENDPOINT_SPOT_LATEST_TICK_ASSET = "/spot/v1/latest/tick/asset"

    // WebSocket Endpoints
    const val WS_SPOT_LATEST_TICK_UNMAPPED = "/spot/v1/latest/tick/unmapped"

    // Request Timeout
    const val REQUEST_TIMEOUT = 60_000L
    const val WEBSOCKET_PING_INTERVAL = 20_000L
}