package com.example.act1sem7.model

import java.io.Serializable

/**
 * Modelo de datos / Entidad Persona (PersonaBean) según la arquitectura UML
 * de la Semana 7 - Acceso a datos SQLite (UPN).
 *
 * @property codigo Identificador único autoincremental de la persona en SQLite.
 * @property nombre Nombre de la persona.
 * @property apellido Apellido de la persona.
 * @property dni Documento Nacional de Identidad (8 dígitos).
 */
data class Persona(
    var codigo: Int = 0,
    var nombre: String = "",
    var apellido: String = "",
    var dni: String = ""
) : Serializable {

    val nombreCompleto: String
        get() = "$nombre $apellido".trim()

    override fun toString(): String {
        return "$codigo - $nombre $apellido (DNI: $dni)"
    }
}
