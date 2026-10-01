# 📱 Android SQLite CRUD - Aplicación Móvil de Acceso a Datos (Semana 7 UPN)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android SDK](https://img.shields.io/badge/Android%20SDK-27%20to%2035-brightgreen.svg?style=flat&logo=android)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-DAO%20%7C%20MVC-blue.svg?style=flat)](#arquitectura-del-proyecto)
[![Material Design](https://img.shields.io/badge/UI-Material%203-blueviolet.svg?style=flat&logo=material-design)](https://m3.material.io)
[![SQLite](https://img.shields.io/badge/Database-SQLite%203-003B57.svg?style=flat&logo=sqlite)](https://www.sqlite.org)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg?style=flat)]()
[![Tests](https://img.shields.io/badge/Tests-100%25%20Passing-brightgreen.svg?style=flat)]()

Proyecto desarrollado para el curso **Programación de Aplicaciones Móviles** (Semana 7) de la **Universidad Privada del Norte (UPN)**, siguiendo las pautas académicas y de modelamiento UML del documento oficial de cátedra (`VideoAM-07.pdf`).

Diseñado con un estándar de ingeniería de software orientado a la industria, con arquitectura limpia, desacoplamiento en capas, patrón DAO, validaciones estrictas y pruebas unitarias deterministas.

---

## 🎯 Información Académica

- **Institución:** Universidad Privada del Norte (UPN)
- **Facultad:** Ingeniería
- **Curso:** Programación de Aplicaciones Móviles
- **Sesión / Unidad:** Semana 7 — Acceso a datos: SQLite, Tareas en Background y Content Providers
- **Docente:** Ing. Pablo Benites Gomez
- **Desarrollador:** Orlando Dorival — Estudiante del 9.° ciclo de Ingeniería

---

## 🏗️ Arquitectura del Proyecto

El proyecto implementa estrictamente el **modelamiento de clases UML** indicado en las diapositivas 19 y 20 del material oficial de la sesión:

![Diagrama de Arquitectura y Clases](mermaid%20diagramas/arquitectura.svg)

> 🔗 Enlace directo al archivo vectorial del diagrama: [arquitectura.svg](mermaid%20diagramas/arquitectura.svg)

### Capas del Sistema:
1. **Capa de Modelo (`model`):**
   - [`Persona`](app/src/main/java/com/example/act1sem7/model/Persona.kt): Entidad de dominio (*PersonaBean*) con atributos `codigo`, `nombre`, `apellido` y `dni`.
2. **Capa de Persistencia (`database`):**
   - [`MySQLiteHelper`](app/src/main/java/com/example/act1sem7/database/MySQLiteHelper.kt): Subclase de `SQLiteOpenHelper` responsable de la creación de la tabla `persona`, versionado y precarga de registros iniciales de demostración.
   - [`PersonaDAO`](app/src/main/java/com/example/act1sem7/database/PersonaDAO.kt): Objeto de Acceso a Datos (*Data Access Object*) que encapsula las transacciones SQL (`insert`, `update`, `delete`, `query`, `rawQuery`).
3. **Capa de Negocio y Validación (`util`):**
   - [`ValidationUtils`](app/src/main/java/com/example/act1sem7/util/ValidationUtils.kt): Lógica pura desacoplada para verificar DNI de 8 dígitos numéricos, longitud y caracteres de nombres y apellidos.
4. **Capa de Presentación y Adaptadores (`ui` / `adapter`):**
   - [`MainActivity`](app/src/main/java/com/example/act1sem7/MainActivity.kt) (**Vista 1**): Formulario de captura y modo edición con ViewBinding y Material Components.
   - [`ReporteGeneralActivity`](app/src/main/java/com/example/act1sem7/ReporteGeneralActivity.kt) (**Vista 2**): Reporte general en `RecyclerView` con tarjetas, búsqueda en tiempo real y botón de retorno.
   - [`PersonaAdapter`](app/src/main/java/com/example/act1sem7/adapter/PersonaAdapter.kt): Adaptador eficiente con soporte de callbacks para edición y eliminación reactiva.

---

## 🚀 Funcionalidades Principales (CRUD Completo)

| Operación | Componente | Descripción |
| :--- | :--- | :--- |
| **CREATE** | `MainActivity` | Registro de nuevas personas con validación de campos obligatorios, formato de DNI (8 dígitos) y prevención de duplicados en SQLite. |
| **READ** | `ReporteGeneralActivity` | Listado general ordenado por código autoincremental, con avatar numérico, nombre completo y DNI. Incluye buscador reactivo instantáneo y contador de registros. |
| **UPDATE** | `MainActivity` + `Reporte` | Selección de un registro desde el reporte que activa el **Modo Edición** en el formulario, precargando datos y permitiendo actualizar cambios con validación. |
| **DELETE** | `ReporteGeneralActivity` | Eliminación individual con diálogo de confirmación `MaterialAlertDialogBuilder`, borrado en SQLite vía DAO y actualización animada de la lista. |

---

## 💾 Esquema de Base de Datos SQLite

- **Base de Datos:** `db_personas.db` (Versión 1)
- **Tabla:** `persona`

```sql
CREATE TABLE persona (
    codigo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    apellido TEXT NOT NULL,
    dni TEXT NOT NULL
);
```

### Datos Precargados de Demostración (Diapositiva 21)
Para facilitar la evaluación inmediata sin requerir ingreso manual previo, la base de datos incluye los 5 registros del caso de estudio de la clase:
1. `Ivan Petrlik` — DNI: `10140461`
2. `Larissa Petrlik` — DNI: `10140462`
3. `Alberto Petrlik` — DNI: `1111132`
4. `Andres Petrlik` — DNI: `46464646`
5. `Jorge Jacinto` — DNI: `666666`

---

## 🧪 Pruebas Unitarias Deterministas (JUnit 4)

Se han implementado suites de pruebas unitarias locales en [`app/src/test/`](app/src/test/java/com/example/act1sem7/):
- **`ValidationUtilsTest`:** Cobertura de casos válidos, inválidos, límites (7 dígitos, 9 dígitos, alfanumérico, nulos, espacios) y mapa de errores.
- **`PersonaTest`:** Verificación de modelo, getters, inmutabilidad vía `copy()` y formato `toString()`.
- **`DatabaseContractTest`:** Validación de constantes contractuales del esquema de SQLite y cláusulas DDL.

Para ejecutar todas las pruebas unitarias:
```bash
./gradlew testDebugUnitTest
```

---

## 📦 Compilación y Generación del APK

Requisitos previos:
- JDK 17 o superior.
- Android SDK instalado (Build Tools 35 / API 35).

### Comandos de Compilación:
```bash
# Compilar código fuente Debug
./gradlew compileDebugSources

# Ejecutar suite de pruebas unitarias
./gradlew test

# Generar APK de instalación
./gradlew assembleDebug
```

El instalador compilado se ubica en:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🌟 Tecnologías y Buenas Prácticas Aplicadas

- **Lenguaje:** Kotlin 2.0 con sintaxis moderna, data classes y null-safety.
- **UI Toolkit:** Android ViewBinding + Material Design 3 (TextInputLayout, MaterialButton, MaterialCardView, MaterialToolbar).
- **Persistencia:** SQLite nativo mediante `SQLiteOpenHelper`, `SQLiteDatabase` y patrón DAO.
- **Gestión de Ciclo de Vida:** Activity Result API (`ActivityResultContracts.StartActivityForResult`), desregistro seguro de recursos en `onDestroy`.
- **Control de Versiones:** Git con Conventional Commits y registro automático en GitHub Desktop.

---

## 👤 Autor

**Orlando Dorival**
- Estudiante de Ingeniería de Sistemas / Software — Universidad Privada del Norte (UPN)
- GitHub: [@Orlandho](https://github.com/Orlandho)
