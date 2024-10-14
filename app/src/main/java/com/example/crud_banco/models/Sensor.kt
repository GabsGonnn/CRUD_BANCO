package com.example.crud_banco.models

data class Sensor(
    var dispId: String? = null,
    var nome: String? = null,      // Nome do sensor
    var tipoSensor: String? = null,  // Tipo do sensor
    var valor: String? = null,        // Valor medido pelo sensor
    var unidade: String? = null,      // Unidade de medida (ex: "Celsius", "kg", etc.)
    var timestamp: String? = null,      // Timestamp para registrar o momento da leitura
    var imagem: Int? = null
)
