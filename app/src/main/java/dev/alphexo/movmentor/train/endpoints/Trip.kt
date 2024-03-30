package dev.alphexo.movmentor.train.endpoints

import dev.alphexo.movmentor.network.NetworkInterface
import dev.alphexo.movmentor.utils.extractResponse
import org.json.JSONObject

enum class EnumStopCard {
    PENDING,
    PASSED,
    WARNING,
    CURRENT
}

class Trip {
    private val apiInfra = URLs.Infra.SELECTED
    private val network = NetworkInterface()

    suspend fun fromTrainNumber(
        trainNumber: Int,
        date: String,
        result: (response: JSONObject) -> Unit
    ) {
        network.sendRequest(
            method = NetworkInterface.RequestMethod.GET,
            url = "$apiInfra/negocios-e-servicos/horarios-ncombio/$trainNumber/$date"
        )
        { statusCode: Int, response: String ->
            extractResponse(statusCode, response) { extractedResponse ->
                result(extractedResponse as JSONObject)
            }
        }
    }
}


