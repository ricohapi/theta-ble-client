package com.ricoh360.thetableclient.thetaBleClientDemo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val ENDPOINT = "http://192.168.1.1:80"

@OptIn(ExperimentalSerializationApi::class)
fun newHttpClient(): HttpClient {
    return HttpClient(Android) {
        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
        install(ContentNegotiation) {
            json(
                Json {
                    encodeDefaults = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }
}

suspend fun get(path: String): HttpResponse {
    val httpClient = newHttpClient()
    val response = httpClient.get(ENDPOINT + path)
    return response
}

@Serializable
data class ThetaInfo(
    val model: String,
    val serialNumber: String,
)

suspend fun getThetaInfoApi(): ThetaInfo {
    return get("/osc/info").body()
}
