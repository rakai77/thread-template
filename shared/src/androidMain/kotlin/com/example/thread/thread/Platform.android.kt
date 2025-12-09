package com.example.thread.thread

import android.os.Build
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import java.util.concurrent.TimeUnit

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return if (isFromMultipart) {
            HttpClient(CIO)
        } else {
            HttpClient(OkHttp) {
                engine {
                    config {
                        // ✅ Critical timeout settings
                        connectTimeout(30, TimeUnit.SECONDS)
                        readTimeout(60, TimeUnit.SECONDS)
                        writeTimeout(60, TimeUnit.SECONDS)
                        callTimeout(90, TimeUnit.SECONDS)

                        // ✅ Enable retries
                        retryOnConnectionFailure(true)
                        followSslRedirects(true)
                        followRedirects(true)
                    }
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()