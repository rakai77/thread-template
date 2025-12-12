# **PLAN**
* Create Crypto Desk using Compose Multiplatform with shared UI
* Implement networking using Ktor also WebSockets
* Implement using Koin DI
* Implement MVVM pattern
* Implement clean architecture with this approach data, domain, presentation
* For the data API or service used Coin Desk open API
* Refactoring existing code

#### API Source and Documentation
Documentation: https://developers.coindesk.com/documentation <br>
Websocket: https://developers.coindesk.com/documentation/legacy-websockets/HowToConnect

<br>

>>Note: Using API KEY to connect websocket, set in parameters

>>API_KEY: a34e5d75e2480aeb566247a339c844221a4eb118b05141263ac159cb7bfc8fbb


#### Structure File and Folder
- data
- domain
- presentation
- utils

# **CONTEXT**

- `build.gradle.kts (:shared)`

```
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "2.2.20"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.ktor.core)
            implementation(libs.ktor.auth)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.encoding)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.websockets)

            api(libs.kotlinx.serialization.json)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.koin.core)
            api(libs.koin.compose.viewmodel)
            api(libs.koin.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "com.example.thread.thread.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

```

- `build.gradle.kts (:composeApp)`
```
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.icon.extended.md3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.example.thread.thread"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.thread.thread"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("io.ktor:ktor-client-okhttp-jvm:3.3.0")
    debugImplementation(compose.uiTooling)
}

```

- `Constant.kt`
```
object Constant {
    //Base Config app
    const val BASE_URL = "min-api.cryptocompare.com"
    const val WS_HOST = "streamer.cryptocompare.com"
    const val API_KEY = "a34e5d75e2480aeb566247a339c844221a4eb118b05141263ac159cb7bfc8fbb"

    // WebSocket Paths
    const val WS_PATH = "/"

    // WebSocket Subscription Types
    const val SUB_TYPE_AGGREGATE = "5" // Aggregate Index (CCCAGG)
    const val SUB_TYPE_TOP_MARKET_CAP = "21" // Top market cap update

    // Default Parameters
    const val DEFAULT_CURRENCY = "IDR"
    const val DEFAULT_LIMIT = 100
    const val DEFAULT_PAGE = 0
}
```

##### `shared/src/commonMain/../remote/api`
`CryptoApiService.kt`

```
interface CryptoApiService {
    suspend fun getTopMarketCap(
        currency: String = Constant.DEFAULT_CURRENCY,
        limit: Int = Constant.DEFAULT_LIMIT,
        page: Int = Constant.DEFAULT_PAGE
    ): Result<TopMarketCapResponse>
}
```

`CryptoApiServiceImpl.kt`

```
class CryptoApiServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : CryptoApiService {

    override suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Result<TopMarketCapResponse> {
        return try {
            val response = httpClient.get(Endpoint.TOP_MARKET_CAP_ENDPOINT) {
                parameter("tsym", currency)
                parameter("limit", limit)
                parameter("page", page)
            }
            Result.success(response.body<TopMarketCapResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
```

`Endpoint.kt`
```
object Endpoint {
    const val TOP_MARKET_CAP_ENDPOINT = "/data/top/mktcapfull"
}
```

##### `shared/src/commonMain/.../remote/response`
`TopMarketCapResponse.kt`

```
@Serializable
data class TopMarketCapResponse(
    @SerialName("Message") val message: String? = null,
    @SerialName("Type") val type: Int? = null,
    @SerialName("MetaData") val metaData: MarketCapMetaData? = null,
    @SerialName("SponsoredData") val sponsoredData: List<SponsoredCoin>? = null,
    @SerialName("Data") val data: List<CoinMarketCapData>? = null,
    @SerialName("RateLimit") val rateLimit: RateLimit? = null,
    @SerialName("HasWarning") val hasWarning: Boolean? = null
)

@Serializable
data class MarketCapMetaData(
    @SerialName("Count") val count: Int? = null
)

@Serializable
data class SponsoredCoin(
    @SerialName("CoinInfo") val coinInfo: CoinInfo? = null
)

@Serializable
data class CoinMarketCapData(
    @SerialName("CoinInfo") val coinInfo: CoinInfo,
    @SerialName("RAW") val raw: Map<String, RawMarketData>? = null,
    @SerialName("DISPLAY") val display: Map<String, DisplayMarketData>? = null
)

@Serializable
data class CoinInfo(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String,
    @SerialName("FullName") val fullName: String,
    @SerialName("Internal") val internal: String? = null,
    @SerialName("ImageUrl") val imageUrl: String? = null,
    @SerialName("Url") val url: String? = null,
    @SerialName("Algorithm") val algorithm: String? = null,
    @SerialName("ProofType") val proofType: String? = null,
    @SerialName("Rating") val rating: RatingInfo? = null,
    @SerialName("NetHashesPerSecond") val netHashesPerSecond: Double? = null,
    @SerialName("BlockNumber") val blockNumber: Long? = null,
    @SerialName("BlockTime") val blockTime: Double? = null,
    @SerialName("BlockReward") val blockReward: Double? = null,
    @SerialName("AssetLaunchDate") val assetLaunchDate: String? = null,
    @SerialName("MaxSupply") val maxSupply: Double? = null,
    @SerialName("Type") val type: Int? = null,
    @SerialName("DocumentType") val documentType: String? = null
)

@Serializable
data class RatingInfo(
    @SerialName("Weiss") val weiss: WeissRating? = null
)

@Serializable
data class WeissRating(
    @SerialName("Rating") val rating: String? = null,
    @SerialName("TechnologyAdoptionRating") val technologyAdoptionRating: String? = null,
    @SerialName("MarketPerformanceRating") val marketPerformanceRating: String? = null
)

@Serializable
data class RawMarketData(
    @SerialName("TYPE") val type: String? = null,
    @SerialName("MARKET") val market: String? = null,
    @SerialName("FROMSYMBOL") val fromSymbol: String? = null,
    @SerialName("TOSYMBOL") val toSymbol: String? = null,
    @SerialName("FLAGS") val flags: String? = null,
    @SerialName("PRICE") val price: Double? = null,
    @SerialName("LASTUPDATE") val lastUpdate: Long? = null,
    @SerialName("MEDIAN") val median: Double? = null,
    @SerialName("LASTVOLUME") val lastVolume: Double? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: Double? = null,
    @SerialName("LASTTRADEID") val lastTradeId: String? = null,
    @SerialName("VOLUMEDAY") val volumeDay: Double? = null,
    @SerialName("VOLUMEDAYTO") val volumeDayTo: Double? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: Double? = null,
    @SerialName("VOLUME24HOURTO") val volume24HourTo: Double? = null,
    @SerialName("OPENDAY") val openDay: Double? = null,
    @SerialName("HIGHDAY") val highDay: Double? = null,
    @SerialName("LOWDAY") val lowDay: Double? = null,
    @SerialName("OPEN24HOUR") val open24Hour: Double? = null,
    @SerialName("HIGH24HOUR") val high24Hour: Double? = null,
    @SerialName("LOW24HOUR") val low24Hour: Double? = null,
    @SerialName("LASTMARKET") val lastMarket: String? = null,
    @SerialName("VOLUMEHOUR") val volumeHour: Double? = null,
    @SerialName("VOLUMEHOURTO") val volumeHourTo: Double? = null,
    @SerialName("OPENHOUR") val openHour: Double? = null,
    @SerialName("HIGHHOUR") val highHour: Double? = null,
    @SerialName("LOWHOUR") val lowHour: Double? = null,
    @SerialName("TOPTIERVOLUME24HOUR") val topTierVolume24Hour: Double? = null,
    @SerialName("TOPTIERVOLUME24HOURTO") val topTierVolume24HourTo: Double? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: Double? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: Double? = null,
    @SerialName("CHANGEDAY") val changeDay: Double? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: Double? = null,
    @SerialName("CHANGEHOUR") val changeHour: Double? = null,
    @SerialName("CHANGEPCTHOUR") val changePctHour: Double? = null,
    @SerialName("CONVERSIONTYPE") val conversionType: String? = null,
    @SerialName("CONVERSIONSYMBOL") val conversionSymbol: String? = null,
    @SerialName("SUPPLY") val supply: Double? = null,
    @SerialName("MKTCAP") val mktCap: Double? = null,
    @SerialName("MKTCAPPENALTY") val mktCapPenalty: Int? = null,
    @SerialName("CIRCULATINGSUPPLY") val circulatingSupply: Double? = null,
    @SerialName("CIRCULATINGSUPPLYMKTCAP") val circulatingSupplyMktCap: Double? = null,
    @SerialName("TOTALVOLUME24H") val totalVolume24H: Double? = null,
    @SerialName("TOTALVOLUME24HTO") val totalVolume24HTo: Double? = null,
    @SerialName("TOTALTOPTIERVOLUME24H") val totalTopTierVolume24H: Double? = null,
    @SerialName("TOTALTOPTIERVOLUME24HTO") val totalTopTierVolume24HTo: Double? = null,
    @SerialName("IMAGEURL") val imageUrl: String? = null
)

@Serializable
data class DisplayMarketData(
    @SerialName("FROMSYMBOL") val fromSymbol: String? = null,
    @SerialName("TOSYMBOL") val toSymbol: String? = null,
    @SerialName("MARKET") val market: String? = null,
    @SerialName("PRICE") val price: String? = null,
    @SerialName("LASTUPDATE") val lastUpdate: String? = null,
    @SerialName("LASTVOLUME") val lastVolume: String? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: String? = null,
    @SerialName("VOLUMEDAY") val volumeDay: String? = null,
    @SerialName("VOLUMEDAYTO") val volumeDayTo: String? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: String? = null,
    @SerialName("VOLUME24HOURTO") val volume24HourTo: String? = null,
    @SerialName("OPENDAY") val openDay: String? = null,
    @SerialName("HIGHDAY") val highDay: String? = null,
    @SerialName("LOWDAY") val lowDay: String? = null,
    @SerialName("OPEN24HOUR") val open24Hour: String? = null,
    @SerialName("HIGH24HOUR") val high24Hour: String? = null,
    @SerialName("LOW24HOUR") val low24Hour: String? = null,
    @SerialName("LASTMARKET") val lastMarket: String? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: String? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: String? = null,
    @SerialName("CHANGEDAY") val changeDay: String? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: String? = null,
    @SerialName("SUPPLY") val supply: String? = null,
    @SerialName("MKTCAP") val mktCap: String? = null,
    @SerialName("TOTALVOLUME24H") val totalVolume24H: String? = null,
    @SerialName("TOTALVOLUME24HTO") val totalVolume24HTo: String? = null,
    @SerialName("TOTALTOPTIERVOLUME24H") val totalTopTierVolume24H: String? = null,
    @SerialName("TOTALTOPTIERVOLUME24HTO") val totalTopTierVolume24HTo: String? = null,
    @SerialName("IMAGEURL") val imageUrl: String? = null
)

@Serializable
data class RateLimit(
    @SerialName("calls_made") val callsMade: CallsInfo? = null,
    @SerialName("calls_left") val callsLeft: CallsInfo? = null
)

@Serializable
data class CallsInfo(
    @SerialName("second") val second: Int? = null,
    @SerialName("minute") val minute: Int? = null,
    @SerialName("hour") val hour: Int? = null,
    @SerialName("day") val day: Int? = null,
    @SerialName("month") val month: Int? = null
)

// WebSocket Top Market Cap Update
@Serializable
data class TopMarketCapWSMessage(
    @SerialName("TYPE") val type: String,
    @SerialName("MESSAGE") val message: String? = null,
    @SerialName("INFO") val info: String? = null,
    @SerialName("PARAMETER") val parameter: String? = null,
    @SerialName("STREAMING_DATA") val streamingData: List<CoinMarketCapData>? = null
)

```

- `WebSocketService.kt`
```
WebSocketService {
    fun subscribeToTopMarketCap(
        currency: String = "IDR",
        limit: Int = 10
    ): Flow<Result<List<CoinMarketCapData>>>

    fun subscribeToAggregateIndex(
        fromSymbol: String,
        toSymbol: String
    ): Flow<Result<CoinMarketCapData>>
    suspend fun unsubscribe()
}

class WebSocketServiceImpl(
    private val webSocketClient: WebSocketClient,
    private val json: Json
) : WebSocketService {
    override fun subscribeToTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CoinMarketCapData>>> {
        return webSocketClient.observeMessages(
            host = Constant.WS_HOST,
            path = Constant.WS_PATH,
            parser = { message ->
                try {
                    val wsMessage = json.decodeFromString<TopMarketCapWSMessage>(message)
                    // Filter hanya message type untuk top market cap updates
                    if (wsMessage.type == Constant.SUB_TYPE_TOP_MARKET_CAP &&
                        wsMessage.streamingData != null) {
                        wsMessage.streamingData.take(limit)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    println("Parse error: ${e.message}")
                    null
                }
            },
            onConnect = {
                // Subscribe to top market cap stream
                val subscriptionMessage = buildJsonObject {
                    put("action", "SubAdd")
                    putJsonArray("subs") {
                        add("21~CCCAGG~*~$currency") // 21 = Top market cap type
                    }
                }
                send(Frame.Text(json.encodeToString(subscriptionMessage)))
            }
        )
    }

    override fun subscribeToAggregateIndex(
        fromSymbol: String,
        toSymbol: String
    ): Flow<Result<CoinMarketCapData>> {
        return webSocketClient.observeMessages(
            host = Constant.WS_HOST,
            path = Constant.WS_PATH,
            parser = { message ->
                try {
                    // Parse aggregate index message (type 5)
                    val data = json.decodeFromString<CoinMarketCapData>(message)
                    data
                } catch (e: Exception) {
                    println("Parse error: ${e.message}")
                    null
                }
            },
            onConnect = {
                // Subscribe to aggregate index
                val subscriptionMessage = buildJsonObject {
                    put("action", "SubAdd")
                    putJsonArray("subs") {
                        add("${Constant.SUB_TYPE_AGGREGATE}~CCCAGG~$fromSymbol~$toSymbol")
                    }
                }
                send(Frame.Text(json.encodeToString(subscriptionMessage)))
            }
        )
    }

    override suspend fun unsubscribe() {
        webSocketClient.closeConnection()
    }

}
```

- `WebSocketClient.kt`
```
interface WebSocketClient{

    fun <T> observeMessages(
        host: String,
        path: String,
        parser: (String) -> T?,
        onConnect: suspend DefaultClientWebSocketSession.() -> Unit = {}
    ) : Flow<Result<T>>

    suspend fun sendMessage(path: String, message: String)
    suspend fun closeConnection()
}

class WebSocketClientImpl(
    private val client: HttpClient,
    private val json: Json
) : WebSocketClient {

    private var session: DefaultClientWebSocketSession? = null

    override fun <T> observeMessages(
        host: String,
        path: String,
        parser: (String) -> T?,
        onConnect: suspend DefaultClientWebSocketSession.() -> Unit
    ): Flow<Result<T>> {
        return flow {
            try {
                client.webSocket(
                    host = host,
                    path = path
                ) {
                    session = this

                    // Execute connection callback (for subscribing)
                    onConnect()

                    while (isActive) {
                        when (val frame = incoming.receive()) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                parser(text)?.let {
                                    emit(Result.success(it))
                                }
                            }
                            is Frame.Close -> {
                                emit(Result.failure(Exception("WebSocket closed")))
                                break
                            }
                            else -> {
                                println("Unknown frame type: ${frame.frameType}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            } finally {
                session = null
            }
        }
    }

    override suspend fun sendMessage(path: String, message: String) {
        session?.send(Frame.Text(message))
    }

    override suspend fun closeConnection() {
        session?.close()
        session = null
    }

}
```

- `HttpClient.kt`
```
fun setupHttpClient(
    baseUrl: String,
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {
    return httpClientProvider.config {
        expectSuccess = true

        install(WebSockets) {
            pingIntervalMillis = 20_000
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(
                createJsonParser()
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }

        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
                useAlternativeNames = false
                explicitNulls = false
            })
        }

        install(HttpRedirect) {
            checkHttpMethod = true
        }

        defaultRequest {
            host = baseUrl
            url {
                protocol = URLProtocol.HTTPS
                parameters.append("api_key", Constant.API_KEY)
            }
        }

        if (isDebugMode) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }
}

fun createJsonParser(): Json {
    return Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
        useAlternativeNames = false
        explicitNulls = false
    }
}

```

##### `shared/src/commonMain/kotlin/com/example/thread/thread/di`
- `KoinModule.kt`
```
fun initKoinModule(
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
) {
    runCatching { stopKoin() }
    startKoin {
        appDeclaration()
        modules(listOf(remoteModule) + additionalModules)
    }
}
```

- `NetworkModule.kt`

```
val remoteModule = module {

    single { createJsonParser() }

    single {
        setupHttpClient(
            baseUrl = Constant.BASE_URL,
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }

    single<WebSocketClient> {
        WebSocketClientImpl(
            client = get(),
            json = get()
        )
    }

    single<CryptoApiService> { CryptoApiServiceImpl(httpClient = get(), json = get()) }

    single<BinanceWebSocketService> {
        BinanceWebSocketServiceImpl(
            webSocketClient = get(),
            json = get()
        )
    }

    single<CryptoRepository> { CryptoRepositoryImpl(apiService = get(), webSocketService = get()) }

    singleOf(::GetTopMarketUseCase)
    singleOf(::WatchTopMarketCapUseCase)
}
```

##### `shared/src/commonMain/.../domain`

- `TopMarketCap.kt`

```
data class TopMarketCapResult(
    val coins: List<CoinMarketCap>,
    val metaData: MarketCapMetaData?,
    val hasWarning: Boolean
)

data class CoinMarketCap(
    val id: String,
    val name: String,
    val fullName: String,
    val symbol: String,
    val imageUrl: String?,
    val algorithm: String?,
    val proofType: String?,
    val rating: CoinRating?,
    val marketData: MarketData?,
    val displayData: DisplayData?
)

data class CoinRating(
    val weissRating: String?,
    val technologyRating: String?,
    val marketPerformanceRating: String?
)

data class MarketData(
    val price: Double,
    val marketCap: Double,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val volume24h: Double,
    val volumeDay: Double,
    val change24h: Double,
    val changePct24h: Double,
    val changeDay: Double,
    val changePctDay: Double,
    val high24h: Double,
    val low24h: Double,
    val open24h: Double,
    val highDay: Double,
    val lowDay: Double,
    val openDay: Double,
    val lastUpdate: Long,
    val fromSymbol: String,
    val toSymbol: String
)

data class DisplayData(
    val price: String,
    val marketCap: String,
    val volume24h: String,
    val change24h: String,
    val changePct24h: String,
    val high24h: String,
    val low24h: String
)

data class MarketCapMetaData(
    val totalCount: Int
)

// WebSocket streaming model
data class CryptoStreamUpdate(
    val coinId: String,
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePct: Double,
    val volume: Double,
    val timestamp: Long
)

```

- `CryptoRepository.kt`

```
interface CryptoRepository {
    suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Result<TopMarketCapResult>

    fun watchTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CryptoStreamUpdate>>>

    fun watchCoinPrice(
        fromSymbol: String,
        toSymbol: String
    ): Flow<Result<CryptoStreamUpdate>>
     suspend fun stopObserving()
 }
```

- `CryptoRepositoryImpl.kt`

```
class CryptoRepositoryImpl(
    private val apiService: CryptoApiService,
    private val webSocketService: BinanceWebSocketService
) : CryptoRepository {
    override suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Result<TopMarketCapResult> {
        return apiService.getTopMarketCap(currency, limit, page)
            .map { response ->
                // ✅ Map response DTO ke domain model
                mapTopMarketCapResponseToDomain(response, currency)
            }
    }

    override fun watchTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CryptoStreamUpdate>>> {
        return webSocketService.subscribeToTopMarketCap(currency, limit)
            .map { result ->
                result.map { coinDataList ->
                    // ✅ Map WebSocket data ke domain model
                    mapCoinDataListToStreamUpdates(coinDataList, currency)
                }
            }
    }

    override fun watchCoinPrice(
        fromSymbol: String,
        toSymbol: String
    ): Flow<Result<CryptoStreamUpdate>> {
        return webSocketService.subscribeToAggregateIndex(fromSymbol, toSymbol)
            .map { result ->
                result.mapCatching { coinData ->
                    // ✅ Map single coin data ke domain model
                    mapCoinDataToStreamUpdate(coinData, toSymbol)
                        ?: throw IllegalStateException("Failed to map coin data")
                }
            }
    }


    override suspend fun stopObserving() {
        webSocketService.unsubscribe()
    }
}
```

- `GetTopMarketUseCase.kt`

```
class GetTopMarketUseCase(
    private val repository: CryptoRepository
) {

    suspend operator fun invoke(
        currency: String = Constant.DEFAULT_CURRENCY,
        limit: Int = Constant.DEFAULT_LIMIT,
        page: Int = Constant.DEFAULT_PAGE
    ): Result<TopMarketCapResult> {  // ✅ Return domain model
        return repository.getTopMarketCap(currency, limit, page)
    }
}
```

- `WatchCoinPriceUseCase.kt`

```
class WatchCoinPriceUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(
        fromSymbol: String,
        toSymbol: String = Constant.DEFAULT_CURRENCY
    ): Flow<Result<CryptoStreamUpdate>> {
        return repository.watchCoinPrice(fromSymbol, toSymbol)
    }
}
```

- `DataMapper.kt`
```
fun mapTopMarketCapResponseToDomain(
    response: TopMarketCapResponse,
    currency: String
): TopMarketCapResult {
    return TopMarketCapResult(
        coins = response.data?.mapNotNull {
            mapCoinDataToDomain(it, currency)
        } ?: emptyList(),
        metaData = response.metaData?.let {
            MarketCapMetaData(totalCount = it.count ?: 0)
        },
        hasWarning = response.hasWarning ?: false
    )
}

fun mapCoinDataToDomain(
    data: CoinMarketCapData,
    currency: String
): CoinMarketCap {
    val coinInfo = data.coinInfo
    val rawData = data.raw?.get(currency)
    val displayData = data.display?.get(currency)

    return CoinMarketCap(
        id = coinInfo.id,
        name = coinInfo.name,
        fullName = coinInfo.fullName,
        symbol = coinInfo.name,
        imageUrl = coinInfo.imageUrl?.let {
            "https://www.cryptocompare.com$it"
        },
        algorithm = coinInfo.algorithm,
        proofType = coinInfo.proofType,
        rating = coinInfo.rating?.let { mapRatingToDomain(it) },
        marketData = rawData?.let { mapRawDataToDomain(it) },
        displayData = displayData?.let { mapDisplayDataToDomain(it) }
    )
}

private fun mapRatingToDomain(rating: RatingInfo): CoinRating {
    return CoinRating(
        weissRating = rating.weiss?.rating,
        technologyRating = rating.weiss?.technologyAdoptionRating,
        marketPerformanceRating = rating.weiss?.marketPerformanceRating
    )
}

private fun mapRawDataToDomain(raw: RawMarketData): MarketData {
    return MarketData(
        price = raw.price ?: 0.0,
        marketCap = raw.mktCap ?: 0.0,
        circulatingSupply = raw.circulatingSupply,
        totalSupply = raw.supply,
        volume24h = raw.volume24Hour ?: 0.0,
        volumeDay = raw.volumeDay ?: 0.0,
        change24h = raw.change24Hour ?: 0.0,
        changePct24h = raw.changePct24Hour ?: 0.0,
        changeDay = raw.changeDay ?: 0.0,
        changePctDay = raw.changePctDay ?: 0.0,
        high24h = raw.high24Hour ?: 0.0,
        low24h = raw.low24Hour ?: 0.0,
        open24h = raw.open24Hour ?: 0.0,
        highDay = raw.highDay ?: 0.0,
        lowDay = raw.lowDay ?: 0.0,
        openDay = raw.openDay ?: 0.0,
        lastUpdate = raw.lastUpdate ?: 0L,
        fromSymbol = raw.fromSymbol ?: "",
        toSymbol = raw.toSymbol ?: ""
    )
}

private fun mapDisplayDataToDomain(display: DisplayMarketData): DisplayData {
    return DisplayData(
        price = display.price ?: "N/A",
        marketCap = display.mktCap ?: "N/A",
        volume24h = display.volume24Hour ?: "N/A",
        change24h = display.change24Hour ?: "N/A",
        changePct24h = display.changePct24Hour ?: "N/A",
        high24h = display.high24Hour ?: "N/A",
        low24h = display.low24Hour ?: "N/A"
    )
}


fun mapCoinDataToStreamUpdate(
    data: CoinMarketCapData,
    currency: String
): CryptoStreamUpdate? {
    val rawData = data.raw?.get(currency) ?: return null

    return CryptoStreamUpdate(
        coinId = data.coinInfo.id,
        symbol = data.coinInfo.name,
        price = rawData.price ?: 0.0,
        change = rawData.change24Hour ?: 0.0,
        changePct = rawData.changePct24Hour ?: 0.0,
        volume = rawData.volume24Hour ?: 0.0,
        timestamp = rawData.lastUpdate ?: 0
    )
}

fun mapCoinDataListToStreamUpdates(
    dataList: List<CoinMarketCapData>,
    currency: String
): List<CryptoStreamUpdate> {
    return dataList.mapNotNull { mapCoinDataToStreamUpdate(it, currency) }
}
```

- `ErrorHandler.kt`
```
sealed class AppError(open val message: String) {
    data class NetworkError(override val message: String = "No internet connection") : AppError(message)
    data class ServerError(override val message: String = "Internal server error") : AppError(message)
    data class TimeoutError(override val message: String = "Request timeout") : AppError(message)
    data class UnknownError(override val message: String = "Something went wrong") : AppError(message)
    data class ValidationError(override val message: String) : AppError(message)
    data class WebSocketError(override val message: String = "WebSocket connection error") : AppError(message)
}

object ErrorHandler {
    fun handleException(exception: Throwable): AppError {
        return when (exception) {
            is TimeoutCancellationException -> AppError.TimeoutError()
            is ClientRequestException -> {
                when (exception.response.status.value) {
                    400 -> AppError.ValidationError("Bad request")
                    401 -> AppError.ValidationError("Unauthorized")
                    403 -> AppError.ValidationError("Forbidden")
                    404 -> AppError.ValidationError("Not found")
                    else -> AppError.UnknownError(exception.message)
                }
            }
            is ServerResponseException -> {
                AppError.ServerError(exception.message)
            }
            else -> AppError.UnknownError(exception.message ?: "Unknown error")
        }
    }
}

```

- `Resource.kt`
```
sealed class Resource<T>(
    val data: T? = null,
    val error: AppError? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(error: AppError, data: T? = null) : Resource<T>(data, error)
}
```

### `:composeApp`

###### `composeApp/src/commonMain/.../presentation`

- `HomeViewModel.kt`

```
class HomeViewModel(
    private val getTopMarketCapUseCase: GetTopMarketUseCase,
    private val watchTopMarketCapUseCase: WatchTopMarketCapUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CryptoListState())
    val state= _state.asStateFlow()

    private val _uiState = MutableStateFlow<CryptoUiState>(CryptoUiState.Initial)
    val uiState= _uiState.asStateFlow()

    private var webSocketJob: Job? = null

    init {
        fetchTopMarketCap()
        startLiveUpdates()
    }

    fun fetchTopMarketCap(
        currency: String = "IDR",
        limit: Int = 10,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                // Update loading state
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = !isRefresh,
                        isRefreshing = isRefresh,
                        error = null,
                        currency = currency
                    )
                }

                if (_state.value.coins.isEmpty()) {
                    _uiState.value = CryptoUiState.Loading
                }

                // Call use case
                getTopMarketCapUseCase(currency, limit, page = 0)
                    .onSuccess { result ->
                        val coins = result.coins

                        _state.update { currentState ->
                            currentState.copy(
                                coins = coins,
                                isLoading = false,
                                isRefreshing = false,
                                error = null,
                                lastUpdateTime = getCurrentTimeMillis()
                            )
                        }

                        _uiState.value = if (coins.isNotEmpty()) {
                            CryptoUiState.Success(coins)
                        } else {
                            CryptoUiState.Error("No data available")
                        }

                        // Log untuk debugging
                        println("✅ Loaded ${coins.size} coins")
                        coins.take(3).forEach { coin ->
                            println("${coin.symbol}: ${coin.marketData?.price}")
                        }
                    }
                    .onFailure { error ->
                        val errorMessage = error.message ?: "Unknown error occurred"

                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = errorMessage
                            )
                        }

                        _uiState.value = CryptoUiState.Error(errorMessage)

                        println("❌ Error: $errorMessage")
                    }

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to fetch data"

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = errorMessage
                    )
                }

                _uiState.value = CryptoUiState.Error(errorMessage)

                println("❌ Exception: $errorMessage")
            }
        }
    }

    fun refresh() {
        fetchTopMarketCap(
            currency = _state.value.currency,
            isRefresh = true
        )
    }

    fun startLiveUpdates(
        currency: String = "IDR",
        limit: Int = 10
    ) {
        webSocketJob?.cancel()

        webSocketJob = viewModelScope.launch {
            try {
                watchTopMarketCapUseCase(currency, limit)
                    .collect { result ->
                        result.onSuccess { updates ->
                            val currentCoins = _state.value.coins.toMutableList()

                            updates.forEach { update ->
                                val index = currentCoins.indexOfFirst {
                                    it.id == update.coinId
                                }

                                if (index != -1) {
                                    // Update existing coin
                                    val coin = currentCoins[index]
                                    val updatedMarketData = coin.marketData?.copy(
                                        price = update.price,
                                        change24h = update.change,
                                        changePct24h = update.changePct,
                                        volume24h = update.volume,
                                        lastUpdate = update.timestamp
                                    )

                                    currentCoins[index] = coin.copy(
                                        marketData = updatedMarketData
                                    )
                                }
                            }

                            _state.update { currentState ->
                                currentState.copy(
                                    coins = currentCoins,
                                    lastUpdateTime = getCurrentTimeMillis()
                                )
                            }

                            println("🔄 Live update: ${updates.size} coins updated")
                        }.onFailure { error ->
                            println("⚠️ WebSocket error: ${error.message}")
                        }
                    }
            } catch (e: Exception) {
                println("❌ WebSocket exception: ${e.message}")
            }
        }
    }

    fun stopLiveUpdates() {
        webSocketJob?.cancel()
        webSocketJob = null
        println("⏹️ Stopped live updates")
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopLiveUpdates()
    }
}
```

- `HomeUiState.kt`

```
sealed class CryptoUiState {
    data object Initial : CryptoUiState()
    data object Loading : CryptoUiState()
    data class Success(val coins: List<CoinMarketCap>) : CryptoUiState()
    data class Error(val message: String) : CryptoUiState()
}

data class CryptoListState(
    val coins: List<CoinMarketCap> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currency: String = "IDR",
    val lastUpdateTime: Long = 0L
)
```

- `HomeScreen.kt`
```
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
) {

    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000) // 30 seconds
            if (state.coins.isNotEmpty() && !state.isLoading) {
                viewModel.refresh()
            }
        }
    }

    Scaffold(
        topBar = {
            CryptoTopBar(
                onRefresh = { viewModel.refresh() },
                isRefreshing = state.isRefreshing,
                lastUpdateTime = state.lastUpdateTime
            )
        },
        containerColor = CryptoColors.AppBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content based on UI state
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                }
            ) { currentUiState ->
                when (currentUiState) {
                    is CryptoUiState.Initial -> Unit
                    is CryptoUiState.Loading -> {
                        LoadingContent()
                    }

                    is CryptoUiState.Success -> {
                        if (currentUiState.coins.isEmpty()) {
                            EmptyContent(
                                onRetry = { viewModel.fetchTopMarketCap() }
                            )
                        } else {
                            CryptoListContent(
                                coins = currentUiState.coins,
                                listState = listState,
                                isRefreshing = state.isRefreshing,
                                onCoinClick = { coin ->
                                    println("Clicked: ${coin.name}")
                                    // Navigate to detail
                                }
                            )
                        }
                    }

                    is CryptoUiState.Error -> {
                        ErrorContent(
                            message = currentUiState.message,
                            onRetry = { viewModel.fetchTopMarketCap() }
                        )
                    }
                    else -> Unit
                }
            }

            // Snackbar untuk error yang tidak mengganggu
            state.error?.let { errorMessage ->
                LaunchedEffect(errorMessage) {
                    delay(3000)
                    viewModel.clearError()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CryptoTopBar(
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    lastUpdateTime: Long,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Crypto Market",
                    fontWeight = FontWeight.Bold
                )
                if (lastUpdateTime > 0) {
                    val timeAgo = remember(lastUpdateTime) {
                        val diff = getCurrentTimeMillis() - lastUpdateTime
                        when {
                            diff < 60_000 -> "Just now"
                            diff < 3600_000 -> "${diff / 60_000}m ago"
                            else -> "${diff / 3600_000}h ago"
                        }
                    }
                    Text(
                        text = "Updated $timeAgo",
                        style = MaterialTheme.typography.bodySmall,
                        color = CryptoColors.TextSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CryptoColors.AppBackground,
            titleContentColor = CryptoColors.TextPrimary
        ),
        actions = {
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = if (isRefreshing)
                        CryptoColors.TextSecondary
                    else
                        CryptoColors.AccentGold
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(10) {
            ShimmerCoinCard()
        }
    }
}

@Composable
private fun CryptoListContent(
    coins: List<CoinMarketCap>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    isRefreshing: Boolean,
    onCoinClick: (CoinMarketCap) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Refreshing indicator di top
        if (isRefreshing) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CryptoColors.AccentGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Updating...",
                            style = MaterialTheme.typography.bodySmall,
                            color = CryptoColors.TextSecondary
                        )
                    }
                }
            }
        }

        // List of coins dengan animated items
        itemsIndexed(
            items = coins,
            key = { _, coin -> coin.id }
        ) { index, coin ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CryptoCoinCard(
                    coin = coin,
                    rank = index + 1,
                    onClick = { onCoinClick(coin) }
                )
            }
        }

        // Footer spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EmptyContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        emoji = "📊",
        title = "No Cryptocurrencies",
        message = "No cryptocurrency data available at the moment",
        actionText = "Refresh",
        onAction = onRetry,
        modifier = modifier
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        emoji = "❌",
        title = "Oops!",
        message = message,
        actionText = "Try Again",
        onAction = onRetry,
        modifier = modifier
    )
}
```


# PROBLEMS
- The Web Sockets fail cannot connect
- Android cannot fetch the data list
- iOS works, but the websocket still failure
- Error handler not yet implemented


# Expected Output
- Data updated live streams
- Fixing logic, flow case when init the websocket implementation