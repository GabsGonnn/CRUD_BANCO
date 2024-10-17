package com.example.crud_banco.models

data class Sensor(
    var dispId: String? = null,
    var nome: String? = null,      // Nome do sensor
    var tipoSensor: String? = null,  // Tipo do sensor
    var valor: String? = null,
    var aux: String? = null, // Valor medido pelo sensor // Unidade de medida (ex: "Celsius", "kg", etc.)
    var timestamp: String? = null,
    var imagem: Int? = null
)
