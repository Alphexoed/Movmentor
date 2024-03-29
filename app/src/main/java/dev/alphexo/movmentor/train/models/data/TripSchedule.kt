package dev.alphexo.movmentor.train.models.data

import dev.alphexo.movmentor.utils.customMap
import org.json.JSONObject

data class TripStop(
    val trainPassed: Boolean,
    val scheduledHour: String,
    val nodeId: Int,
    val stationName: String,
    val warnings: String,
)

fun buildTripStop(data: JSONObject) = TripStop(
    data.getBoolean("ComboioPassou"),
    data.getString("HoraProgramada"),
    data.getInt("NodeID"),
    data.getString("NomeEstacao"),
    data.getString("Observacoes"),
)

data class TripSchedule(
    val destinationDate: String,
    val departureDate: String,
    val destinationStationName: String,
    val tripDuration: String,
    val schedule: List<TripStop>,
    val operator: String,
    val departureStationName: String,
    val trainSituation: String,
    val service: ServiceType,
)

fun buildTripSchedule(data: JSONObject): TripSchedule {
    return TripSchedule(
        data.getString("DataHoraDestino"),
        data.getString("DataHoraOrigem"),
        data.getString("Destino"),
        data.getString("DuracaoViagem"),
        data.getJSONArray("NodesPassagemComboio").customMap { buildTripStop(it) },
        data.getString("Operador"),
        data.getString("Origem"),
        data.getString("SituacaoComboio"),
        getServiceType(data.getString("TipoServico")),
    )
}