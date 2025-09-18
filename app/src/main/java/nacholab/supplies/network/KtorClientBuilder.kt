package nacholab.supplies.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*

private const val TIME_OUT = 60_000

object KtorClientBuilder {
    fun buildKtorClient() = HttpClient(Android) {

        install(ContentNegotiation) {
            json()
        }
//    install(JsonFeature) {
//        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
//            prettyPrint = true
//            isLenient = true
//            ignoreUnknownKeys = true
//        })
//
//        engine {
//            connectTimeout = TIME_OUT
//            socketTimeout = TIME_OUT
//        }
//    }
//
//    install(Logging) {
//        logger = object : Logger {
//            override fun log(message: String) {
//                Log.v("Logger Ktor =>", message)
//            }
//
//        }
//        level = LogLevel.ALL
//    }
//
//    install(ResponseObserver) {
//        onResponse { response ->
//            Log.d("HTTP status:", "${response.status.value}")
//        }
//    }
//
//    install(DefaultRequest) {
//        header(HttpHeaders.ContentType, ContentType.Application.Json)
//    }
    }

}