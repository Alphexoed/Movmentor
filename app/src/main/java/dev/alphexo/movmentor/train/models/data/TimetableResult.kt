package dev.alphexo.movmentor.train.models.data

import org.json.JSONObject

data class TimetableResult(
    val trainPassed: Boolean,
    val departureDate: String,
    val departureDateFull: String,
    val tripDate: String,
    val destinationNodeId: Int,
    val departureNodeId: Int,
    val trainNumber1: Int,
    val trainNumber2: Int,
    val destinationStationName: String,
    val departureStationName: String,
    val warnings: String,
    val operator: String,
    val service: String,
    val platform: String?
)

fun buildTimetableResult(data: JSONObject) = TimetableResult(
    data.getBoolean("ComboioPassou"),
    data.getString("DataHoraPartidaChegada"),
    data.getString("DataHoraPartidaChegada_ToOrderBy"),
    data.getString("DataRealizacao"),
    data.getInt("EstacaoDestino"),
    data.getInt("EstacaoOrigem"),
    data.getInt("NComboio1"),
    data.getInt("NComboio2"),
    data.getString("NomeEstacaoDestino"),
    data.getString("NomeEstacaoOrigem"),
    data.getString("Observacoes"),
    data.getString("Operador"),
    data.getString("TipoServico"),
    data.optString("CP:Plataforma")
)