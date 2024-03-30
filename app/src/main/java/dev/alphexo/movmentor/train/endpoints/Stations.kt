package dev.alphexo.movmentor.train.endpoints

import dev.alphexo.movmentor.network.NetworkInterface
import dev.alphexo.movmentor.network.NetworkInterface.RequestMethod
import dev.alphexo.movmentor.train.models.data.Station
import dev.alphexo.movmentor.train.models.data.Stations
import dev.alphexo.movmentor.utils.extractResponse
import org.json.JSONObject


class Stations {
    private val apiInfra = URLs.Infra.SELECTED
    private val apiCP = URLs.CP.SELECTED
    private val network = NetworkInterface()
    private val stations = Stations()

    // TODO : For "SearchTab"
    suspend fun getAll(): List<Station> = buildList {
        network.sendRequest(
            method = RequestMethod.GET,
            url = "$apiCP/siv/stations",
            authorization = Pair("Bearer", Auth.getToken()!!)
        ) { statusCode: Int, response: String ->
            if (statusCode == 200) {
                addAll(response.map { stations.build(JSONObject(it.toString())) })
            } else {
                addAll(stations.getOffline().map { stations.build(JSONObject(it.toString())) })
            }
        }
    }

    suspend fun fromName(name: String, result: (response: Any) -> Unit) {
        network.sendRequest(
            method = RequestMethod.GET,
            url = "$apiInfra/negocios-e-servicos/estacao-nome/$name"
        )
        { statusCode: Int, response: String ->
            extractResponse(statusCode, response) { extractedResponse ->
                result(extractedResponse)
            }
        }
    }
}
