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
    fun isValidDni_conDniValidoDe6a8Digitos_retornaTrue() {
        // Casos reales de la Diapositiva 21 del curso
        assertTrue(ValidationUtils.isValidDni("666666"))       // 6 dígitos (Jorge Jacinto)
        assertTrue(ValidationUtils.isValidDni("1111132"))      // 7 dígitos (Alberto Petrlik)
        assertTrue(ValidationUtils.isValidDni("10140461"))     // 8 dígitos (Ivan Petrlik)
        assertTrue(ValidationUtils.isValidDni("10140462"))     // 8 dígitos (Larissa Petrlik)
        assertTrue(ValidationUtils.isValidDni("46464646"))     // 8 dígitos (Andres Petrlik)
        assertTrue(ValidationUtils.isValidDni("72486369"))     // 8 dígitos
    }

    @Test
    fun isValidDni_conDniInvalido_retornaFalse() {
        assertFalse(ValidationUtils.isValidDni(null))
        assertFalse(ValidationUtils.isValidDni(""))
        assertFalse(ValidationUtils.isValidDni("   "))
        assertFalse(ValidationUtils.isValidDni("12345"))        // 5 dígitos (menor al límite inferior de 6)
        assertFalse(ValidationUtils.isValidDni("123456789"))    // 9 dígitos (mayor al límite superior de 8)
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
        assertEquals("El DNI debe contener entre 6 y 8 dígitos numéricos", errores["dni"])
    }

    @Test
    fun validatePersonaFields_conCasosSlide21_retornaValido() {
        val casoJorge = ValidationUtils.validatePersonaFields("Jorge", "Jacinto", "666666")
        assertTrue("Jorge Jacinto con DNI 666666 debe ser válido", casoJorge.isEmpty())

        val casoAlberto = ValidationUtils.validatePersonaFields("Alberto", "Petrlik", "1111132")
        assertTrue("Alberto Petrlik con DNI 1111132 debe ser válido", casoAlberto.isEmpty())
    }
}
