package dev.alphexo.movmentor.network

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


class NetworkInterface {
    enum class RequestMethod {
        GET,
        POST
    }

    private var client: OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(LoggingInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(1, TimeUnit.DAYS)
        .readTimeout(1, TimeUnit.DAYS)
        .writeTimeout(1, TimeUnit.DAYS)
        .callTimeout(1, TimeUnit.DAYS)
        .build()

    private val androidVersion = Build.VERSION.RELEASE
    private val deviceDevice = Build.DEVICE
    private val deviceBrand = Build.BRAND
    private val deviceModel = Build.MODEL

    private fun buildRequest(
        method: RequestMethod,
        url: String,
        authorization: Pair<String, String>? = null,
        json: String? = null,
        formUrlEncoded: List<Pair<String, String>>? = null
    ): Request {
        val requestBuilder = Request.Builder()
            .url(url.toHttpUrl())
            .header(
                "User-Agent",
                "Movmentor/2 (Android ${androidVersion}) (${deviceDevice}@${deviceBrand}/${deviceModel})"
            )
            .header("DNT", "1")
            .apply {
                if (authorization != null) {
                    header("Authorization", "${authorization.first} ${authorization.second}")
                }
            }

        return when (method) {
            RequestMethod.POST -> requestBuilder
                .post(
                    json?.toRequestBody("application/json".toMediaType())
                        ?: formUrlEncoded?.toFormBody()!!
                )
                .build()

            else -> requestBuilder.build() // Handle other request methods as needed
        }
    }

    private fun List<Pair<String, String>>?.toFormBody(): RequestBody? =
        this?.let { formBody ->
            val builder = FormBody.Builder()
            formBody.forEach { item -> builder.add(item.first, item.second) }
            builder.build()
        }

    suspend fun sendRequest(
        method: RequestMethod,
        url: String,
        dataJSON: String? = null,
        dataFormURL: List<Pair<String, String>>? = null,
        authorization: Pair<String, String>? = null,
        response: (statusCode: Int, response: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest(
                method = method,
                url = url,
                json = dataJSON,
                formUrlEncoded = dataFormURL,
                authorization = authorization
            )
            client.newCall(request).execute().use {
                return@withContext response(it.code, it.body!!.string())
            }
        } catch (error: Throwable) {  // Catch any Throwable
            return@withContext response(1000, error.message ?: error.toString())
        }
    }
}