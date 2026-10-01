package com.example.act1sem7

import com.example.act1sem7.util.ValidationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para las validaciones de negocio del formulario de personas.
 */
class ValidationUtilsTest {

    @Test
    fun isValidDni_conDniValidoDe8Digitos_retornaTrue() {
        assertTrue(ValidationUtils.isValidDni("10140461"))
        assertTrue(ValidationUtils.isValidDni("72486369"))
        assertTrue(ValidationUtils.isValidDni("00000000"))
        assertTrue(ValidationUtils.isValidDni("99999999"))
    }

    @Test
    fun isValidDni_conDniInvalido_retornaFalse() {
        assertFalse(ValidationUtils.isValidDni(null))
        assertFalse(ValidationUtils.isValidDni(""))
        assertFalse(ValidationUtils.isValidDni("   "))
        assertFalse(ValidationUtils.isValidDni("1234567"))      // 7 dígitos
        assertFalse(ValidationUtils.isValidDni("123456789"))    // 9 dígitos
        assertFalse(ValidationUtils.isValidDni("1014046A"))    // Con letra
        assertFalse(ValidationUtils.isValidDni("1014-046"))    // Con guion
        assertFalse(ValidationUtils.isValidDni("abcdefgh"))    // Solo letras
    }

    @Test
    fun isValidName_conNombresValidos_retornaTrue() {
        assertTrue(ValidationUtils.isValidName("Ivan"))
        assertTrue(ValidationUtils.isValidName("Larissa"))
        assertTrue(ValidationUtils.isValidName("José Carlos"))
        assertTrue(ValidationUtils.isValidName("María del Carmen"))
        assertTrue(ValidationUtils.isValidName("Ñaña"))
        assertTrue(ValidationUtils.isValidName("Ángel"))
    }

    @Test
    fun isValidName_conNombresInvalidos_retornaFalse() {
        assertFalse(ValidationUtils.isValidName(null))
        assertFalse(ValidationUtils.isValidName(""))
        assertFalse(ValidationUtils.isValidName("   "))
        assertFalse(ValidationUtils.isValidName("A"))          // 1 letra
        assertFalse(ValidationUtils.isValidName("Juan123"))    // Con números
        assertFalse(ValidationUtils.isValidName("Pedro@"))     // Con caracteres especiales
    }

    @Test
    fun validatePersonaFields_conDatosCorrectos_retornaMapaVacio() {
        val errores = ValidationUtils.validatePersonaFields(
            nombre = "Orlando",
            apellido = "Dorival",
            dni = "72486369"
        )
        assertTrue(errores.isEmpty())
    }

    @Test
    fun validatePersonaFields_conCamposVacios_retornaTresErrores() {
        val errores = ValidationUtils.validatePersonaFields("", "", "")
        assertEquals(3, errores.size)
        assertTrue(errores.containsKey("nombre"))
        assertTrue(errores.containsKey("apellido"))
        assertTrue(errores.containsKey("dni"))
    }

    @Test
    fun validatePersonaFields_conDniInvalido_retornaErrorEnDni() {
        val errores = ValidationUtils.validatePersonaFields(
            nombre = "Alberto",
            apellido = "Petrlik",
            dni = "123"
        )
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("dni"))
    }
}
