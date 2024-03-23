package dev.alphexo.movmentor.train.endpoints

class URLs {
    object CP {
        var SELECTED: String? = null
        const val PRODUCTION = "https://api.cp.pt/cp-api"
        const val STAGING = "http://api-qua.cp.pt/cp-api"
    }

    object Infra {
        var SELECTED: String? = null
        const val PRODUCTION = "https://servicos.infraestruturasdeportugal.pt"
    }
}
