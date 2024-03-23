package dev.alphexo.movmentor.train.endpoints

import android.util.Log
import dev.alphexo.movmentor.network.NetworkInterface
import dev.alphexo.movmentor.network.NetworkInterface.RequestMethod
import org.json.JSONObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


var AuthCPToken: String? = null


object Auth {
    private val apiCP = URLs.CP.SELECTED
    private val network = NetworkInterface()

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getToken(): String? {
        AuthCPToken?.let { return it } // Return early if token already exists

        network.sendRequest(
            method = RequestMethod.POST,
            url = "$apiCP/oauth/token",
            dataFormURL = listOf("grant_type" to "client_credentials"),
            authorization = Pair("Basic", Base64.encode("cp-mobile:pass".toByteArray()))
        ) { statusCode: Int, response: String ->
            if (statusCode == 200) {
                AuthCPToken = JSONObject(response)
                    .getString("access_token")
                Log.v("Auth.getToken", "Value set to $AuthCPToken")
            } else {
                Log.w("Auth.getToken", "Error: $statusCode $response")
            }
        }

        return AuthCPToken
    }
}
