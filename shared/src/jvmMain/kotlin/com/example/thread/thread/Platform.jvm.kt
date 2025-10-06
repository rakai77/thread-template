package com.example.thread.thread

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return HttpClient(CIO) {
            engine {
                // Opsi-opsi engine level (opsional)
                pipelining = true
                maxConnectionsCount = 1000

                // Tuning endpoint (opsional)
                endpoint {
                    connectTimeout = 60_000
                    keepAliveTime = 5_000
                    maxConnectionsPerRoute = 100
                    pipelineMaxSize = 20
                }

                // Untuk upload multipart besar, kadang enak dimodif dikit
                if (isFromMultipart) {
                    // Biarkan HttpTimeout plugin yang ngatur request timeout;
                    // di CIO, requestTimeout=0 artinya "no timeout" di level engine.
                    requestTimeout = 0
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = JVMPlatform()