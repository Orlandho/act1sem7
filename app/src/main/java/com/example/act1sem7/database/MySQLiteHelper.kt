package com.example.act1sem7.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Helper de SQLite que gestiona el ciclo de vida de la base de datos local.
 * Implementa la estructura definida en el modelamiento UML de la Semana 7 (UPN).
 */
class MySQLiteHelper(context: Context) : SQLiteOpenHelper(
    context,
    NOMBREBASEDATOS,
    null,
    VERSION
) {

    companion object {
        const val NOMBREBASEDATOS: String = "db_personas.db"
        const val VERSION: Int = 1
        const val NOMBRETABLA: String = "persona"

        const val COL_CODIGO: String = "codigo"
        const val COL_NOMBRE: String = "nombre"
        const val COL_APELLIDO: String = "apellido"
        const val COL_DNI: String = "dni"

        const val SQL_CREACION: String = """
            CREATE TABLE $NOMBRETABLA (
                $COL_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_APELLIDO TEXT NOT NULL,
                $COL_DNI TEXT NOT NULL
            );
        """
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(SQL_CREACION)

        // Precarga de registros iniciales de demostración según Slide 21 del curso
        database.execSQL("INSERT INTO $NOMBRETABLA ($COL_NOMBRE, $COL_APELLIDO, $COL_DNI) VALUES ('Ivan', 'Petrlik', '10140461');")
        database.execSQL("INSERT INTO $NOMBRETABLA ($COL_NOMBRE, $COL_APELLIDO, $COL_DNI) VALUES ('Larissa', 'Petrlik', '10140462');")
        database.execSQL("INSERT INTO $NOMBRETABLA ($COL_NOMBRE, $COL_APELLIDO, $COL_DNI) VALUES ('Alberto', 'Petrlik', '1111132');")
        database.execSQL("INSERT INTO $NOMBRETABLA ($COL_NOMBRE, $COL_APELLIDO, $COL_DNI) VALUES ('Andres', 'Petrlik', '46464646');")
        database.execSQL("INSERT INTO $NOMBRETABLA ($COL_NOMBRE, $COL_APELLIDO, $COL_DNI) VALUES ('Jorge', 'Jacinto', '666666');")
    }

    override fun onUpgrade(db: SQLiteDatabase, antiguaversion: Int, nuevaversion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $NOMBRETABLA")
        onCreate(db)
    }
}
