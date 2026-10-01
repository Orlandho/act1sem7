package com.example.act1sem7

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.act1sem7.database.PersonaDAO
import com.example.act1sem7.databinding.ActivityMainBinding
import com.example.act1sem7.model.Persona
import com.example.act1sem7.util.ValidationUtils
import com.google.android.material.snackbar.Snackbar

/**
 * Vista 1: Actividad Principal (Formulario y Gestión de CRUD con SQLite).
 * Corresponde a la pantalla principal modelada en las Diapositivas 16, 17, 19, 20 y 21
 * de la Semana 7 - Acceso a Datos SQLite (UPN).
 *
 * Implementa las operaciones:
 * - CREATE: Registrar personas con validación de nombre, apellido y DNI.
 * - READ: Navegación al reporte general de registros.
 * - UPDATE: Edición y actualización de registros seleccionados desde el reporte.
 * - DELETE: Gestionado directamente desde la vista de reporte con confirmación.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var personaDAO: PersonaDAO
    private var personaEnEdicion: Persona? = null

    companion object {
        private const val KEY_PERSONA_EN_EDICION = "key_persona_en_edicion"
    }

    // Launcher para recibir la persona seleccionada a editar desde ReporteGeneralActivity
    private val launcherReporte = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val persona = result.data?.let { data ->
                IntentCompat.getSerializableExtra(
                    data,
                    ReporteGeneralActivity.EXTRA_EDITAR_PERSONA,
                    Persona::class.java
                )
            }
            persona?.let { activarModoEdicion(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        personaDAO = PersonaDAO(this)

        configurarListeners()
        actualizarContador()

        // Restaurar modo edición en caso de cambio de configuración (rotación de pantalla)
        if (savedInstanceState != null) {
            val personaGuardada = IntentCompat.getSerializableExtra(
                Intent().putExtras(savedInstanceState),
                KEY_PERSONA_EN_EDICION,
                Persona::class.java
            )
            personaGuardada?.let {
                activarModoEdicion(it, restaurarCamposTexto = false)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        personaEnEdicion?.let {
            outState.putSerializable(KEY_PERSONA_EN_EDICION, it)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarContador()
    }

    override fun onDestroy() {
        super.onDestroy()
        personaDAO.close()
    }

    private fun configurarListeners() {
        // Botón GRABAR / REGISTRAR / ACTUALIZAR
        binding.btnGrabar.setOnClickListener {
            procesarGuardado()
        }

        // Botón MOSTRAR REGISTROS (Navegación hacia Activity 2)
        binding.btnMostrarRegistros.setOnClickListener {
            val intent = Intent(this, ReporteGeneralActivity::class.java)
            launcherReporte.launch(intent)
        }

        // Botón LIMPIAR / CANCELAR EDICIÓN
        binding.btnLimpiar.setOnClickListener {
            if (personaEnEdicion != null) {
                cancelarModoEdicion()
            } else {
                limpiarFormulario()
            }
        }

        // Botón Cancelar del banner de edición
        binding.btnCancelarEdicionBanner.setOnClickListener {
            cancelarModoEdicion()
        }
    }

    /**
     * Procesa la inserción o actualización de la persona en SQLite con validaciones.
     */
    private fun procesarGuardado() {
        limpiarErrores()

        val nombre = binding.etNombre.text?.toString()?.trim() ?: ""
        val apellido = binding.etApellido.text?.toString()?.trim() ?: ""
        val dni = binding.etDni.text?.toString()?.trim() ?: ""

        val errores = ValidationUtils.validatePersonaFields(nombre, apellido, dni)
        if (errores.isNotEmpty()) {
            mostrarErroresValidacion(errores)
            return
        }

        val codigoActual = personaEnEdicion?.codigo ?: -1
        if (personaDAO.existeDni(dni, excluirCodigo = codigoActual)) {
            binding.tilDni.error = getString(R.string.error_dni_duplicado)
            binding.tilDni.requestFocus()
            return
        }

        if (personaEnEdicion == null) {
            // Operación CREATE: Insertar nueva persona
            val nuevaPersona = Persona(
                nombre = nombre,
                apellido = apellido,
                dni = dni
            )
            val resultadoId = personaDAO.insertarPersona(nuevaPersona)
            if (resultadoId != -1L) {
                Snackbar.make(
                    binding.coordinatorLayout,
                    getString(R.string.msg_registro_exitoso),
                    Snackbar.LENGTH_LONG
                ).show()
                limpiarFormulario()
                actualizarContador()
            } else {
                Snackbar.make(
                    binding.coordinatorLayout,
                    getString(R.string.msg_error_operacion),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            // Operación UPDATE: Actualizar persona existente
            val personaActualizada = personaEnEdicion!!.copy(
                nombre = nombre,
                apellido = apellido,
                dni = dni
            )
            val filasAfectadas = personaDAO.modificarPersona(personaActualizada)
            if (filasAfectadas > 0) {
                Snackbar.make(
                    binding.coordinatorLayout,
                    getString(R.string.msg_actualizacion_exitosa),
                    Snackbar.LENGTH_LONG
                ).show()
                cancelarModoEdicion()
                actualizarContador()
            } else {
                Snackbar.make(
                    binding.coordinatorLayout,
                    getString(R.string.msg_error_operacion),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarErroresValidacion(errores: Map<String, String>) {
        errores["nombre"]?.let {
            binding.tilNombre.error = it
            binding.tilNombre.requestFocus()
        }
        errores["apellido"]?.let {
            binding.tilApellido.error = it
            if (!binding.tilNombre.isErrorEnabled) binding.tilApellido.requestFocus()
        }
        errores["dni"]?.let {
            binding.tilDni.error = it
            if (!binding.tilNombre.isErrorEnabled && !binding.tilApellido.isErrorEnabled) {
                binding.tilDni.requestFocus()
            }
        }
    }

    private fun limpiarErrores() {
        binding.tilNombre.error = null
        binding.tilApellido.error = null
        binding.tilDni.error = null
    }

    private fun limpiarFormulario() {
        binding.etNombre.text?.clear()
        binding.etApellido.text?.clear()
        binding.etDni.text?.clear()
        limpiarErrores()
        binding.etNombre.requestFocus()
    }

    /**
     * Activa el modo de edición cargando los datos de la persona en los campos del formulario.
     */
    private fun activarModoEdicion(persona: Persona, restaurarCamposTexto: Boolean = true) {
        personaEnEdicion = persona
        binding.cardModoEdicion.visibility = View.VISIBLE
        binding.tvModoEdicionDesc.text = "Modificando a: ${persona.nombreCompleto} (#${persona.codigo})"
        binding.tvTituloFormulario.text = getString(R.string.titulo_edicion)
        binding.btnGrabar.text = getString(R.string.btn_actualizar)
        binding.btnGrabar.setIconResource(R.drawable.ic_check)

        if (restaurarCamposTexto) {
            binding.etNombre.setText(persona.nombre)
            binding.etApellido.setText(persona.apellido)
            binding.etDni.setText(persona.dni)
            limpiarErrores()
            binding.etNombre.requestFocus()
        }
    }

    /**
     * Cancela el modo de edición y regresa al modo de registro normal.
     */
    private fun cancelarModoEdicion() {
        personaEnEdicion = null
        binding.cardModoEdicion.visibility = View.GONE
        binding.tvTituloFormulario.text = getString(R.string.titulo_formulario)
        binding.btnGrabar.text = getString(R.string.btn_grabar)
        binding.btnGrabar.setIconResource(R.drawable.ic_add)
        limpiarFormulario()
    }

    private fun actualizarContador() {
        val count = personaDAO.contarRegistros()
        binding.tvTotalRegistros.text = "Total de registros en SQLite: $count"
    }
}