package com.example.act1sem7.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.act1sem7.model.Persona

/**
 * Data Access Object (DAO) para la entidad Persona.
 * Encapsula todas las operaciones CRUD sobre la tabla 'persona' en SQLite,
 * siguiendo el patrón arquitectónico requerido en el modelamiento UML de la Semana 7.
 */
class PersonaDAO(context: Context) {

    private val dbHelper: MySQLiteHelper = MySQLiteHelper(context)
    private var database: SQLiteDatabase? = null

    /**
     * Abre la conexión a la base de datos en modo lectura/escritura.
     */
    fun open(): PersonaDAO {
        if (database == null || !database!!.isOpen) {
            database = dbHelper.writableDatabase
        }
        return this
    }

    /**
     * Cierra la conexión a la base de datos para liberar recursos.
     */
    fun close() {
        if (database != null && database!!.isOpen) {
            database!!.close()
        }
        dbHelper.close()
    }

    /**
     * Inserta una nueva persona en la base de datos SQLite (CREATE).
     * @param persona Objeto Persona a persistir.
     * @return El ID autoincremental generado, o -1 en caso de error.
     */
    fun insertarPersona(persona: Persona): Long {
        open()
        val values = ContentValues().apply {
            put(MySQLiteHelper.COL_NOMBRE, persona.nombre.trim())
            put(MySQLiteHelper.COL_APELLIDO, persona.apellido.trim())
            put(MySQLiteHelper.COL_DNI, persona.dni.trim())
        }
        val id = database?.insert(MySQLiteHelper.NOMBRETABLA, null, values) ?: -1L
        if (id != -1L) {
            persona.codigo = id.toInt()
        }
        return id
    }

    /**
     * Actualiza los datos de una persona existente en SQLite (UPDATE).
     * @param persona Objeto Persona con los datos actualizados y su código correspondiente.
     * @return Cantidad de filas afectadas (1 si fue exitoso, 0 si no se encontró).
     */
    fun modificarPersona(persona: Persona): Int {
        open()
        val values = ContentValues().apply {
            put(MySQLiteHelper.COL_NOMBRE, persona.nombre.trim())
            put(MySQLiteHelper.COL_APELLIDO, persona.apellido.trim())
            put(MySQLiteHelper.COL_DNI, persona.dni.trim())
        }
        return database?.update(
            MySQLiteHelper.NOMBRETABLA,
            values,
            "${MySQLiteHelper.COL_CODIGO} = ?",
            arrayOf(persona.codigo.toString())
        ) ?: 0
    }

    /**
     * Elimina un registro de persona por su código (DELETE).
     * @param codigo Identificador de la persona a eliminar.
     * @return Cantidad de filas eliminadas.
     */
    fun eliminarPersona(codigo: Int): Int {
        open()
        return database?.delete(
            MySQLiteHelper.NOMBRETABLA,
            "${MySQLiteHelper.COL_CODIGO} = ?",
            arrayOf(codigo.toString())
        ) ?: 0
    }

    /**
     * Obtiene el listado completo de personas registradas en SQLite (READ/LIST).
     * @return Lista de objetos Persona ordenados por código ascendente.
     */
    fun listarPersonas(): ArrayList<Persona> {
        open()
        val lista = ArrayList<Persona>()
        val cursor: Cursor? = database?.query(
            MySQLiteHelper.NOMBRETABLA,
            arrayOf(
                MySQLiteHelper.COL_CODIGO,
                MySQLiteHelper.COL_NOMBRE,
                MySQLiteHelper.COL_APELLIDO,
                MySQLiteHelper.COL_DNI
            ),
            null,
            null,
            null,
            null,
            "${MySQLiteHelper.COL_CODIGO} ASC"
        )

        cursor?.use {
            val idxCodigo = it.getColumnIndexOrThrow(MySQLiteHelper.COL_CODIGO)
            val idxNombre = it.getColumnIndexOrThrow(MySQLiteHelper.COL_NOMBRE)
            val idxApellido = it.getColumnIndexOrThrow(MySQLiteHelper.COL_APELLIDO)
            val idxDni = it.getColumnIndexOrThrow(MySQLiteHelper.COL_DNI)

            while (it.moveToNext()) {
                lista.add(
                    Persona(
                        codigo = it.getInt(idxCodigo),
                        nombre = it.getString(idxNombre),
                        apellido = it.getString(idxApellido),
                        dni = it.getString(idxDni)
                    )
                )
            }
        }
        return lista
    }

    /**
     * Filtra personas por nombre, apellido o DNI que coincidan con la búsqueda.
     */
    fun filtrarPersonas(criterio: String): ArrayList<Persona> {
        open()
        val lista = ArrayList<Persona>()
        val patron = "%${criterio.trim()}%"
        val cursor: Cursor? = database?.query(
            MySQLiteHelper.NOMBRETABLA,
            arrayOf(
                MySQLiteHelper.COL_CODIGO,
                MySQLiteHelper.COL_NOMBRE,
                MySQLiteHelper.COL_APELLIDO,
                MySQLiteHelper.COL_DNI
            ),
            "${MySQLiteHelper.COL_NOMBRE} LIKE ? OR ${MySQLiteHelper.COL_APELLIDO} LIKE ? OR ${MySQLiteHelper.COL_DNI} LIKE ?",
            arrayOf(patron, patron, patron),
            null,
            null,
            "${MySQLiteHelper.COL_CODIGO} ASC"
        )

        cursor?.use {
            val idxCodigo = it.getColumnIndexOrThrow(MySQLiteHelper.COL_CODIGO)
            val idxNombre = it.getColumnIndexOrThrow(MySQLiteHelper.COL_NOMBRE)
            val idxApellido = it.getColumnIndexOrThrow(MySQLiteHelper.COL_APELLIDO)
            val idxDni = it.getColumnIndexOrThrow(MySQLiteHelper.COL_DNI)

            while (it.moveToNext()) {
                lista.add(
                    Persona(
                        codigo = it.getInt(idxCodigo),
                        nombre = it.getString(idxNombre),
                        apellido = it.getString(idxApellido),
                        dni = it.getString(idxDni)
                    )
                )
            }
        }
        return lista
    }

    /**
     * Busca una persona por su código único.
     */
    fun buscarPorId(codigo: Int): Persona? {
        open()
        val cursor: Cursor? = database?.query(
            MySQLiteHelper.NOMBRETABLA,
            arrayOf(
                MySQLiteHelper.COL_CODIGO,
                MySQLiteHelper.COL_NOMBRE,
                MySQLiteHelper.COL_APELLIDO,
                MySQLiteHelper.COL_DNI
            ),
            "${MySQLiteHelper.COL_CODIGO} = ?",
            arrayOf(codigo.toString()),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return Persona(
                    codigo = it.getInt(it.getColumnIndexOrThrow(MySQLiteHelper.COL_CODIGO)),
                    nombre = it.getString(it.getColumnIndexOrThrow(MySQLiteHelper.COL_NOMBRE)),
                    apellido = it.getString(it.getColumnIndexOrThrow(MySQLiteHelper.COL_APELLIDO)),
                    dni = it.getString(it.getColumnIndexOrThrow(MySQLiteHelper.COL_DNI))
                )
            }
        }
        return null
    }

    /**
     * Verifica si un DNI ya se encuentra registrado (excluyendo opcionalmente un ID en caso de edición).
     */
    fun existeDni(dni: String, excluirCodigo: Int = -1): Boolean {
        open()
        val selection = if (excluirCodigo != -1) {
            "${MySQLiteHelper.COL_DNI} = ? AND ${MySQLiteHelper.COL_CODIGO} != ?"
        } else {
            "${MySQLiteHelper.COL_DNI} = ?"
        }
        val selectionArgs = if (excluirCodigo != -1) {
            arrayOf(dni.trim(), excluirCodigo.toString())
        } else {
            arrayOf(dni.trim())
        }

        val cursor = database?.query(
            MySQLiteHelper.NOMBRETABLA,
            arrayOf(MySQLiteHelper.COL_CODIGO),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val existe = cursor?.use { it.count > 0 } ?: false
        return existe
    }

    /**
     * Retorna la cantidad total de registros en la base de datos.
     */
    fun contarRegistros(): Int {
        open()
        val cursor = database?.rawQuery("SELECT COUNT(*) FROM ${MySQLiteHelper.NOMBRETABLA}", null)
        var count = 0
        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        return count
    }
}
