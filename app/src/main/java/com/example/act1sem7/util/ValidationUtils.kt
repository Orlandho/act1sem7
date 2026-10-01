package com.example.act1sem7.util

/**
 * Utilidades puras para validación de datos del formulario de personas.
 * Desacoplado del framework de Android para garantizar testeabilidad unitaria al 100%.
 */
object ValidationUtils {

    // En el Perú y en los ejemplos oficiales de la clase (Diapositiva 21: Jorge Jacinto 666666, Alberto Petrlik 1111132),
    // los documentos de identidad abarcan de 6 a 8 dígitos numéricos.
    private val DNI_REGEX = Regex("^[0-9]{6,8}$")
    private val NAME_REGEX = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]{2,50}$")

    /**
     * Valida que el DNI contenga entre 6 y 8 dígitos numéricos.
     */
    fun isValidDni(dni: String?): Boolean {
        if (dni.isNullOrBlank()) return false
        return DNI_REGEX.matches(dni.trim())
    }

    /**
     * Valida que el nombre o apellido contenga solo letras y espacios, con longitud mínima de 2.
     */
    fun isValidName(name: String?): Boolean {
        if (name.isNullOrBlank()) return false
        val trimmed = name.trim()
        return trimmed.length in 2..50 && NAME_REGEX.matches(trimmed)
    }

    /**
     * Valida un formulario completo de persona.
     * Retorna un mapa con el nombre del campo y el mensaje de error correspondiente (o vacío si es válido).
     */
    fun validatePersonaFields(
        nombre: String?,
        apellido: String?,
        dni: String?
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        when {
            nombre.isNullOrBlank() -> errors["nombre"] = "El nombre es obligatorio"
            !isValidName(nombre) -> errors["nombre"] = "Ingrese un nombre válido (al menos 2 letras)"
        }

        when {
            apellido.isNullOrBlank() -> errors["apellido"] = "El apellido es obligatorio"
            !isValidName(apellido) -> errors["apellido"] = "Ingrese un apellido válido (al menos 2 letras)"
        }

        when {
            dni.isNullOrBlank() -> errors["dni"] = "El DNI es obligatorio"
            !isValidDni(dni) -> errors["dni"] = "El DNI debe contener entre 6 y 8 dígitos numéricos"
        }

        return errors
    }
}
