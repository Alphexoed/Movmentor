package dev.alphexo.movmentor.train.models.data

import androidx.compose.ui.graphics.Color

sealed class ServiceType(val code: String, val text: String, val color: Color) {
    data object URBAN : ServiceType("U", "Urban", Color(0xFFFF8888))
    data object URBAN_SPECIAL : ServiceType("U-EC", "Urban Special", Color(0xFFFF8888))
    data object REGIONAL : ServiceType("R", "Regional", Color(0xFFFFFF88))
    data object INTER_REGIONAL : ServiceType("IR", "Inter-Regional", Color(0xFFFFAA88))
    data object INTER_REGIONAL_ARCO : ServiceType("IR-A", "Inter-Regional Arco", Color(0xFFFFAA88))
    data object INTER_CITIES : ServiceType("IC", "Inter-Cities", Color(0xFFAA88FF))
    data object INTER_CITIES_SPECIAL :
        ServiceType("IC-E", "Inter-Cities Special", Color(0xFFAA88FF))

    data object INTER_NACIONAL : ServiceType("IN", "Inter-National", Color(0xFF88AAFF))
    data object ALFA_PENDULAR : ServiceType("AP", "Alfa-Pendular", Color(0xFF88FF88))
    data object HISTORICAL : ServiceType("E", "Historical", Color(0xFF88E7FF))
    data object COMPLEMENTARY_TRANSPORTATION :
        ServiceType("TC", "Complementary Transportation", Color(0xFFD5FF88))

    data object SPECIAL : ServiceType("ESP", "Special", Color(0xFFFF88FF))
    data object SERVICE : ServiceType("S", "Service", Color(0xFFFF88FF))
    data object MATERIAL : ServiceType("MER", "Material", Color(0xFFFF88FF))
    data object UNKNOWN : ServiceType("X", "Unknown", Color(0xFF888888))
}

fun getServiceType(service: String): ServiceType {
    return when (service) {
        "INTERNACIONAL" -> ServiceType.INTER_NACIONAL
        "ALFA" -> ServiceType.ALFA_PENDULAR
        "IC" -> ServiceType.INTER_CITIES
        "IC-E" -> ServiceType.INTER_CITIES_SPECIAL
        "IR" -> ServiceType.INTER_REGIONAL
        "IR-A" -> ServiceType.INTER_REGIONAL_ARCO
        "REGIONAL" -> ServiceType.REGIONAL
        "URB|SUBUR" -> ServiceType.URBAN
        "U" -> ServiceType.URBAN
        "U-EC" -> ServiceType.URBAN_SPECIAL
        "TC" -> ServiceType.COMPLEMENTARY_TRANSPORTATION
        "E" -> ServiceType.HISTORICAL
        "ESPECIAL" -> ServiceType.SPECIAL
        "MERCADORIAS" -> ServiceType.MATERIAL
        "SERVIÇO" -> ServiceType.SERVICE
        else -> ServiceType.UNKNOWN
    }
}