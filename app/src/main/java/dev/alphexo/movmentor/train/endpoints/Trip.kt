package dev.alphexo.movmentor.train.endpoints

import dev.alphexo.movmentor.network.NetworkInterface
import dev.alphexo.movmentor.train.models.data.TripCalculate
import dev.alphexo.movmentor.train.models.data.buildTripCalculate
import dev.alphexo.movmentor.utils.extractResponse
import org.json.JSONArray
import org.json.JSONObject

enum class EnumStopCard {
    PENDING,
    PASSED,
    WARNING,
    CURRENT
}

class Trip {
    private val apiCP = URLs.CP.SELECTED
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

    suspend fun calculateTrip(
        nodeIds: Pair<String, String>,
        travelDate: FromToDate,
        result: (statusCode: Int, response: TripCalculate) -> Unit
    ) {
        val data = JSONObject()

//        Example
//        {
//            "arrivalStationCode": "94-2006",
//            "classes": [1, 2],
//            "departureStationCode": "94-61051",
//            "searchType": 3,
//            "timeLimit": {
//              "limitType": 3,
//              "startTime": "19:34"
//            },
//            "travelDate": "2024-05-26"
//        }

        data.put("arrivalStationCode", nodeIds.second)
        data.put("classes", JSONArray().put(1).put(2))
        data.put("departureStationCode", nodeIds.first)
        data.put("searchType", 3)
        data.put("timeLimit", JSONObject()
            .put("limitType", 3)
            .put("startTime", travelDate.single!![FromToDateKey.HOUR])
        )
        data.put("travelDate", travelDate.single!![FromToDateKey.DATE])

        network.sendRequest(
            method = NetworkInterface.RequestMethod.POST,
            url = "${apiCP}/siv/travel/search?lang=EN",
            authorization = Pair("Bearer", Auth.getToken()!!),
            dataJSON = data.toString()
        ) { statusCode: Int, response: String ->
            result(statusCode, buildTripCalculate(JSONObject(response)))
        }
    }
}


