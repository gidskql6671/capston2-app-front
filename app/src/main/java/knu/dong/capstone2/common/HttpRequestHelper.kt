package knu.dong.capstone2.common

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import knu.dong.capstone2.BuildConfig
import knu.dong.capstone2.dto.HttpApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestHelper {
    private val TAG = "HttpRequestHelper"
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    private val domain: String = BuildConfig.SERVER_URL

    suspend fun <T> get(path: String, block: HttpRequestBuilder.() -> Unit = {}): T? =
        withContext(Dispatchers.IO) {
            try {
                val url = "$domain/$path"

                val response: HttpResponse = client.get(url, block)
                val responseStatus = response.status

                if (responseStatus == HttpStatusCode.OK) {
                    val jsonObject = JsonParser.parseString(response.bodyAsText()).asJsonObject

                    val type = object : TypeToken<HttpApiResponse<T>>() {}.type
                    val httpApiResponse = Gson().fromJson<HttpApiResponse<T>>(jsonObject, type)

                    httpApiResponse.data
                } else {
                    Log.e("${TAG}_get", "$responseStatus")

                    null
                }
            }
            catch (err: Exception) {
                Log.e("${TAG}_get", err.toString())

                null
            }
        }

    suspend fun <T> post(path: String, block: HttpRequestBuilder.() -> Unit = {}): T? =
        withContext(Dispatchers.IO) {
            try {
                val url = "$domain/$path"

                val response: HttpResponse = client.post(url, block)
                val responseStatus = response.status

                if (responseStatus.value / 100 == 2) {
                    val jsonObject = JsonParser.parseString(response.bodyAsText()).asJsonObject

                    val type = object : TypeToken<HttpApiResponse<T>>() {}.type
                    val httpApiResponse = Gson().fromJson<HttpApiResponse<T>>(jsonObject, type)

                    httpApiResponse.data
                } else {
                    Log.e("${TAG}_get", "$responseStatus")

                    null
                }
            }
            catch (err: Exception) {
                Log.e("${TAG}_get", err.toString())

                null
            }
        }
}
