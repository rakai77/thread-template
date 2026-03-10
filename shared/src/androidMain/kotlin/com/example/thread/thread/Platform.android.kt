package com.example.thread.thread

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return HttpClient(OkHttp)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()