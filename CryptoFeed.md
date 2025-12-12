# **PLAN**
* Create Crypto Desk using Compose Multiplatform with shared UI
* Implement networking using Ktor also WebSockets
* Implement using Koin DI
* Implement MVVM pattern
* Implement clean architecture with this approach data, domain, presentation
* For the data API or service used Coin Desk open API
* Refactor/adjustment existing code

#### API Source and Documentation
Documentation: https://developers.coindesk.com/documentation <br>
Websocket: https://developers.coindesk.com/documentation/legacy-websockets/HowToConnect

<br>

>>Note: Using API KEY to connect websocket, set in parameters

>>API_KEY: a34e5d75e2480aeb566247a339c844221a4eb118b05141263ac159cb7bfc8fbb


#### Structure File and Folder (:shared)
- data
- domain
- presentation (:commonMain)
- utils

# **Existing Code**

- `build.gralde.kt (:composeApp)`

```
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

- `build.gradle.kt (:shared)`

```
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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

- `shared/src/iosMain/.../Platform.ios.kt`

```
class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return HttpClient(Darwin) {
            engine {
                configureRequest {
                    setAllowsCellularAccess(true)
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
```

- `shared/src/androidMain/.../Platform.android.kt`

```
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return if (isFromMultipart) HttpClient(CIO) else HttpClient(OkHttp) {
            engine {
                config {
                    retryOnConnectionFailure(true)
                    followRedirects(true)
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
```
- `shared/src/commonMain/.../data/remote/HttpClient.kt`

```
fun setupHttpClient(
    baseUrl: String,
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {

    return httpClientProvider.config {
        ContentEncoding()

        expectSuccess = true

        install(HttpTimeout) {
            this.requestTimeoutMillis = 60_000
            this.connectTimeoutMillis = 60_000
            this.socketTimeoutMillis = 60_000
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
                this.user
                protocol = URLProtocol.HTTPS
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
```

- `KoinModule.kt`

```
fun initKoinModule(appDeclaration: KoinAppDeclaration = {}) {
    runCatching { stopKoin() }
    startKoin {
        appDeclaration()
        modules(listOf(remoteModule))
    }
}
```

- `NetworkModule.kt`

```
val remoteModule = module {
    single {
        setupHttpClient(
            baseUrl = "", // Base url for CryptoDesk
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }
}
```

# **CONTEXT**

- Integration API list with stream data (max 10 list data) also websocket too 
- Handle mapping data after create data class response. Put on 'model' directory in domain layer. Create in utils directory for mapper
- Prioritize to make data, and domain layer
- Handle with base error 
- Create a generic class for result like success/error
- use base practice to handle kotlin dispatcher
- don't implement presentation layer, cause i want to use a different file.md with better experience and context