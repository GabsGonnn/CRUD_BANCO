package com.example.crud_banco.models

data class Controle(
    var controleId: String? = null,
    var nomeDisp: String? = null,
    var acao: String? = null,
    var valor: String? = null,
    var tipoDisp: String? = null,
    var aux: String? = null,
    var imagem: Int? = null
)