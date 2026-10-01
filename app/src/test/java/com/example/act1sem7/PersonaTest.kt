package com.example.act1sem7

import com.example.act1sem7.model.Persona
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Pruebas unitarias para el modelo de datos Persona.
 */
class PersonaTest {

    @Test
    fun persona_creacionCorrecta_asignaCampos() {
        val persona = Persona(
            codigo = 1,
            nombre = "Ivan",
            apellido = "Petrlik",
            dni = "10140461"
        )

        assertEquals(1, persona.codigo)
        assertEquals("Ivan", persona.nombre)
        assertEquals("Petrlik", persona.apellido)
        assertEquals("10140461", persona.dni)
    }

    @Test
    fun persona_nombreCompleto_concatenaNombreYApellido() {
        val persona = Persona(
            codigo = 2,
            nombre = "Larissa",
            apellido = "Petrlik",
            dni = "10140462"
        )
        assertEquals("Larissa Petrlik", persona.nombreCompleto)
    }

    @Test
    fun persona_toString_formateaCorrectamente() {
        val persona = Persona(
            codigo = 5,
            nombre = "Jorge",
            apellido = "Jacinto",
            dni = "666666"
        )
        assertEquals("5 - Jorge Jacinto (DNI: 666666)", persona.toString())
    }

    @Test
    fun persona_copy_permiteModificacionInmutable() {
        val original = Persona(codigo = 1, nombre = "Juan", apellido = "Perez", dni = "12345678")
        val modificado = original.copy(nombre = "Carlos")

        assertEquals("Carlos", modificado.nombre)
        assertEquals("Perez", modificado.apellido)
        assertEquals(1, modificado.codigo)
        assertNotEquals(original, modificado)
    }
}
