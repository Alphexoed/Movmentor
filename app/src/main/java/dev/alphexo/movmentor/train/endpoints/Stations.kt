package dev.alphexo.movmentor.train.endpoints

import android.util.Log
import dev.alphexo.movmentor.network.NetworkInterface
import dev.alphexo.movmentor.network.NetworkInterface.RequestMethod
import dev.alphexo.movmentor.train.models.data.Station
import dev.alphexo.movmentor.train.models.data.Stations
import org.json.JSONObject


class Stations {
    private val apiCP = URLs.CP.PRODUCTION //URLs.CP.SELECTED
    private val network = NetworkInterface()
    private val stations = Stations()

    suspend fun getAll(): List<Station> = buildList {
        network.sendRequest(
            method = RequestMethod.GET,
            url = "$apiCP/siv/stations",
            authorization = Pair("Bearer", Auth.getToken()!!)
        ) { statusCode: Int, response: String ->
            if (statusCode == 200) {
                addAll(response.map { stations.build(JSONObject(it.toString())) })
            } else {
                Log.w("Stations.getAll", "Error: $statusCode $response")
                addAll(stations.getOffline().map { stations.build(JSONObject(it.toString())) })
            }
        }
    }
}
