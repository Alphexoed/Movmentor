package dev.alphexo.movmentor.utils

import dev.alphexo.movmentor.train.models.data.SearchQuery
import dev.alphexo.movmentor.train.models.data.TimetableResult
import dev.alphexo.movmentor.train.models.data.buildTimetableResult
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()


fun extractResponse(
    statusCode: Int,
    response: String,
    result: (response: Any) -> Unit
) {
    if (statusCode == 200 && response.startsWith("{")) {
        JSONObject(response).optJSONArray("response")?.let { result(it) }
            ?: JSONObject(response).optJSONObject("response")?.let { result(it) }
    }
}

fun <T> JSONArray.customMap(transform: (JSONObject) -> T): List<T> {
    val resultList = mutableListOf<T>()
    for (i in 0 until length()) {
        val jsonObject = getJSONObject(i)
        resultList.add(transform(jsonObject))
    }
    return resultList
}

fun JSONArray.toJSONObjectList(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }


object DateFormats {
    const val dayMonthYear = "dd-MM-yyyy"
    const val yearMonthDay = "yyyy-MM-dd"
}

fun convertDateFormat(originalFormat: String, desiredFormat: String, dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern(originalFormat)
    val localDate = LocalDate.parse(dateString, formatter)
    return localDate.format(DateTimeFormatter.ofPattern(desiredFormat))
}

// Calculates a formatted node string based on the provided node ID,
// with a hyphen after the first two digits and all leading zeros removed
// from the second part.
fun calculateNode(nodeId: String): String {
    return "${nodeId.substring(0, 2)}-${nodeId.substring(2).trimStart('0')}"
}


class MiscTrains {
    fun searchQueryLogic(
        searchQueryList: MutableList<SearchQuery>,
        stationsEndpointResult: JSONArray
    ) {
        searchQueryList.clear()

        stationsEndpointResult.customMap { station ->
            val distancia = station.optInt("Distancia", 0)
            val nodeId = station.optInt("NodeID", 0)
            val nome = station.optString("Nome", "undefined")
            searchQueryList.add(SearchQuery(distancia, nodeId, nome))
        }
    }

    fun searchTripResultLogic(
        timetableResultList: MutableList<TimetableResult>, stationsTripsResult: JSONObject
    ) {
        timetableResultList.clear()

        val tripResultInfra =
            stationsTripsResult.optJSONArray("resp:infra")?.toJSONObjectList() ?: emptyList()
        val tripResultCP = stationsTripsResult.getJSONArray("resp:cp").toJSONObjectList()

        val cpPlatforms = tripResultCP.map { jsonObject ->
            mapOf(
                "trainNumber" to jsonObject.getString("trainNumber"),
                "platform" to jsonObject.getString("platform")
            )
        }.associateBy { it["trainNumber"] } // Map for faster lookup

        tripResultInfra.forEach { infraObject ->
            val cpPlatform = cpPlatforms[infraObject.getString("NComboio1")]
            infraObject.put("CP:Plataforma", cpPlatform?.get("platform"))
            timetableResultList.add(buildTimetableResult(infraObject))
        }
    }
}

