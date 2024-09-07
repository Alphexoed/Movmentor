package dev.alphexo.movmentor.train.models.data

import org.json.JSONArray
import org.json.JSONObject

data class Station(
    val region: String?,
    val railways: JSONArray,
    val latitude: Double,
    val longitude: Double,
    val designation: String,
    val code: String,
)

class Stations {
    fun build(data: JSONObject) = Station(
        data.optString("region"),
        data.getJSONArray("railways"),
        data.getDouble("latitude"),
        data.getDouble("longitude"),
        data.getString("designation"),
        data.getString("code")
    )
}