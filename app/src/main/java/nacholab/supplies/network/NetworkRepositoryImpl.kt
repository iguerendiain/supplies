package nacholab.supplies.network

import android.content.Context
import android.provider.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import nacholab.supplies.domain.NetworkRespository
import nacholab.supplies.domain.PreferencesRepository
import nacholab.supplies.network.model.SessionAPI
import nacholab.supplies.network.model.SupplyAPI

class NetworkRepositoryImpl(
    private val httpClient: HttpClient,
    private val preferencesRepository: PreferencesRepository
): NetworkRespository {

    companion object {
        private const val BASE_URL = "https://apisupplies.nacholab.net:8047"
//        private const val BASE_URL = "http://192.168.1.2:8047"
        private const val HEADER_SESSION_ID = "session_id"
        private const val HEADER_DEVICE_ID = "device_id"
        private const val HEADER_DEVICE_TYPE = "device_type"
        private const val PARAMETER_EMAIL = "email"
        private const val PARAMETER_VERIFICATIONCODE = "verification_code"
    }


    override suspend fun getCurrentSession(): SessionAPI {
        return httpClient.get("$BASE_URL/session"){
            header(HEADER_SESSION_ID, preferencesRepository.getSessionId())
        }.body<SessionAPI>()
    }

    override suspend fun getMySupplies(): List<SupplyAPI> {
        return httpClient.get("$BASE_URL/supplies"){
            header(HEADER_SESSION_ID, preferencesRepository.getSessionId())
        }.body<List<SupplyAPI>>()
    }

    override suspend fun saveMySupplies(supplies: List<SupplyAPI>) {
        httpClient.post("$BASE_URL/supplies"){
            header(HEADER_SESSION_ID, preferencesRepository.getSessionId())
            contentType(ContentType.Application.Json)
            setBody(supplies)
        }
    }

    override suspend fun authenticateUser(email: String): Boolean {
        val authenticationResult = httpClient.put("$BASE_URL/user") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append(PARAMETER_EMAIL, email)
            }))
        }

        return authenticationResult.status == HttpStatusCode.Created
    }

    override suspend fun createSession(
        email: String,
        verificationCode: String
    ): SessionAPI? {
        val sessionCreationResult = httpClient.post("$BASE_URL/session") {
            header(HEADER_DEVICE_ID, Settings.Secure.ANDROID_ID)
            header(HEADER_DEVICE_TYPE, "mobile")
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append(PARAMETER_EMAIL, email)
                append(PARAMETER_VERIFICATIONCODE, verificationCode)
            }))
        }

        return if (sessionCreationResult.status == HttpStatusCode.Created){
            sessionCreationResult.body<SessionAPI>()
        }else{
            null
        }
    }
}