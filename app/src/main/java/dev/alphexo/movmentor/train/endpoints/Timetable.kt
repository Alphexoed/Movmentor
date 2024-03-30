package dev.alphexo.movmentor.train.endpoints

import dev.alphexo.movmentor.network.NetworkInterface
import dev.alphexo.movmentor.network.NetworkInterface.RequestMethod
import dev.alphexo.movmentor.utils.calculateNode
import dev.alphexo.movmentor.utils.extractResponse
import org.json.JSONArray
import org.json.JSONObject


enum class FromToDateKey {
    DATE,
    HOUR
}

class FromToDate {
    lateinit var from: Map<FromToDateKey, String>
    lateinit var to: Map<FromToDateKey, String>
}


class Timetable {
    private val apiInfra = URLs.Infra.SELECTED
    private val apiCP = URLs.CP.SELECTED
    private val network = NetworkInterface()

    suspend fun getTimetable(
        nodeId: Int,
        fromToDate: FromToDate,
        result: (response: JSONObject) -> Unit
    ) {
        // From: 2024-02-09%2018:56
        // To: 2024-02-09%2023:56

        val url = "$apiInfra/negocios-e-servicos/partidas-chegadas/$nodeId/" +
                "${fromToDate.from.getValue(FromToDateKey.DATE)}%20${
                    fromToDate.from.getValue(
                        FromToDateKey.HOUR
                    )
                }/" +
                "${fromToDate.to.getValue(FromToDateKey.DATE)}%20${
                    fromToDate.to.getValue(
                        FromToDateKey.HOUR
                    )
                }/" +
                "INTERNACIONAL,%20ALFA,%20IC,%20IR,%20REGIONAL,%20URB%7CSUBUR,%20ESPECIAL,%20MERCADORIAS,%20SERVI%C3%87O"


        val final = JSONObject()

        network.sendRequest(
            method = RequestMethod.GET,
            url = url
        ) { statusCode: Int, response: String ->
            extractResponse(statusCode, response) { extractedResponse ->
                final.put(
                    "resp:infra",
                    JSONObject((extractedResponse as JSONArray)[0].toString()).getJSONArray("NodesComboioTabelsPartidasChegadas")
                )
            }
        }

        network.sendRequest(
            method = RequestMethod.GET,
            url = "$apiCP/siv/stations/${calculateNode(nodeId.toString())}/timetable/" +
                    fromToDate.from.getValue(FromToDateKey.DATE) +
                    "?start=" +
                    fromToDate.from.getValue(FromToDateKey.HOUR),
            authorization = Pair("Bearer", Auth.getToken()!!)
        ) { _: Int, response: String ->
            final.put("resp:cp", JSONObject(response).getJSONArray("stationStops"))
        }

        return result(final)
    }
}
