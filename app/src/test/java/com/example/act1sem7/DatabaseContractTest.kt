package com.example.act1sem7

import com.example.act1sem7.database.MySQLiteHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de contrato para el esquema y constantes de la base de datos SQLite.
 */
class DatabaseContractTest {

    @Test
    fun databaseConstants_coincidenConEspecificacionesUML() {
        assertEquals("db_personas.db", MySQLiteHelper.NOMBREBASEDATOS)
        assertEquals(1, MySQLiteHelper.VERSION)
        assertEquals("persona", MySQLiteHelper.NOMBRETABLA)
        assertEquals("codigo", MySQLiteHelper.COL_CODIGO)
        assertEquals("nombre", MySQLiteHelper.COL_NOMBRE)
        assertEquals("apellido", MySQLiteHelper.COL_APELLIDO)
        assertEquals("dni", MySQLiteHelper.COL_DNI)
    }

    @Test
    fun sqlCreacion_contieneEstructuraEsperada() {
        val sql = MySQLiteHelper.SQL_CREACION.lowercase()

        assertTrue("Debe crear la tabla persona", sql.contains("create table persona"))
        assertTrue("Debe definir codigo como primary key autoincrement", sql.contains("codigo integer primary key autoincrement"))
        assertTrue("Debe definir campo nombre", sql.contains("nombre text not null"))
        assertTrue("Debe definir campo apellido", sql.contains("apellido text not null"))
        assertTrue("Debe definir campo dni", sql.contains("dni text not null"))
    }
}
