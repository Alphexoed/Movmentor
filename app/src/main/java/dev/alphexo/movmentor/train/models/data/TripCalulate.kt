package dev.alphexo.movmentor.train.models.data

import dev.alphexo.movmentor.train.endpoints.Trip
import dev.alphexo.movmentor.utils.customMap
import org.json.JSONArray
import org.json.JSONObject


data class TripCalcStop(
    val platform: Int?,
    val departureHour: String,
    val arrivalHour: String,
    val latitude: String,
    val longitude: String,
    val stationName: String,
    val stationId: String
)

data class TripCalcSection(
    val messages: JSONArray?,
    val duration: String,
    val trainNumber: String,
    val trainStops: List<TripCalcStop>?,
    val departureStationName: String,
    val departureStationId: String,
    val arrivalStationName: String,
    val arrivalStationId: String,
    val serviceCode: ServiceType,
    val arrivalTime: String,
    val departureTime: String,
    val delay: Any?,
    val departurePlatform: Int?,
    val allocation: Any?,
    val occupancy: Any?,
    val arrivalPlatform: Int?,
    val bike: Any?,
    val mobility: Any?,
    val sequenceNumber: Int?
)

data class TripBasePrices(
    val priceType: Int, val centsValue: Int, val constraints: JSONArray?, val travelClass: Int
)

data class TripCalcTrip(
    val duration: String,
    val services: List<String>,
    val basePrices: List<TripBasePrices>,
    val saleableOnline: Boolean,
    val travelSections: List<TripCalcSection>,
    val arrivalTime: String,
    val departureTime: String,
    val delay: Any?,
    val allocation: Any?,
    val occupancy: Any?,
    val saleLink: String?,
    val transferCount: Int,
    val bike: Any?,
    val mobility: Any?
)

data class TripCalculate(
    val warnings: List<String>?,
    val trips: List<TripCalcTrip>,
    val departureStationName: String,
    val departureStationId: String,
    val arrivalStationName: String,
    val arrivalStationId: String
)

fun buildStop(data: JSONObject): TripCalcStop {
    return TripCalcStop(
        platform = data.optInt("platform"),
        departureHour = data.getString("departure"),
        arrivalHour = data.getString("arrival"),
        latitude = data.getString("latitude"),
        longitude = data.getString("longitude"),
        stationName = data.getJSONObject("station").getString("designation"),
        stationId = data.getJSONObject("station").getString("code")
    )
}

fun buildSections(data: JSONObject): TripCalcSection {
    return TripCalcSection(
        messages = data.getJSONArray("messages"),
        duration = data.getString("duration"),
        trainNumber = data.getString("trainNumber"),
        trainStops = data.getJSONArray("trainStops").customMap { buildStop(it) },
        departureStationName = data.getJSONObject("departureStation").getString("designation"),
        departureStationId = data.getJSONObject("departureStation").getString("code"),
        arrivalStationName = data.getJSONObject("arrivalStation").getString("designation"),
        arrivalStationId = data.getJSONObject("arrivalStation").getString("code"),
        serviceCode = getServiceType(data.getJSONObject("serviceCode").getString("code")),
        arrivalTime = data.getString("arrivalTime"),
        departureTime = data.getString("departureTime"),
        delay = data.opt("delay"),
        departurePlatform = data.optInt("departurePlatform"),
        allocation = data.opt("allocation"),
        occupancy = data.opt("occupancy"),
        arrivalPlatform = data.optInt("arrivalPlatform"),
        bike = data.opt("bike"),
        mobility = data.opt("mobility"),
        sequenceNumber = data.optInt("mobility"),
    )
}

fun buildPrices(data: JSONObject): TripBasePrices {
    return TripBasePrices(
        priceType = data.getInt("priceType"),
        centsValue = data.getInt("centsValue"),
        constraints = data.optJSONArray("constraints"),
        travelClass = data.getInt("travelClass")
    )
}

fun buildTrip(data: JSONObject): TripCalcTrip {
    return TripCalcTrip(
        duration = data.getString("duration"),
        services = data.getString("services").split("|"),
        basePrices = data.getJSONArray("basePrices").customMap { buildPrices(it) },
        saleableOnline = data.getBoolean("saleableOnline"),
        travelSections = data.getJSONArray("travelSections").customMap { buildSections(it) },
        arrivalTime = data.getString("arrivalTime"),
        departureTime = data.getString("departureTime"),
        delay = data.opt("delay"),
        allocation = data.opt("allocation"),
        occupancy = data.opt("occupancy"),
        saleLink = data.optString("saleLink"),
        transferCount = data.getInt("transferCount"),
        bike = data.opt("bike"),
        mobility = data.opt("mobility"),
    )
}

fun buildTripCalculate(data: JSONObject): TripCalculate {
    val tempWarnings = mutableListOf<String>()

    data.getJSONArray("messages").customMap {
        if (it.getString("type") == "WARNING") {
            tempWarnings.add(it.getString("text"))
        }
    }

    return TripCalculate(
        warnings = tempWarnings,
        trips = data.getJSONArray("outwardTrip").customMap { buildTrip(it) },
        departureStationName = data.getJSONObject("departureStation").getString("designation"),
        departureStationId = data.getJSONObject("departureStation").getString("code"),
        arrivalStationName = data.getJSONObject("arrivalStation").getString("designation"),
        arrivalStationId = data.getJSONObject("arrivalStation").getString("code"),
    )
}