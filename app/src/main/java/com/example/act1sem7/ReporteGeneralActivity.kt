package com.example.act1sem7

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.act1sem7.adapter.PersonaAdapter
import com.example.act1sem7.database.PersonaDAO
import com.example.act1sem7.databinding.ActivityReporteGeneralBinding
import com.example.act1sem7.model.Persona
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Vista 2: Reporte General de Personas registradas en SQLite.
 * Corresponde a la pantalla secundaria especificada en la Diapositiva 21 (Tarea 1) de la Semana 7.
 * Muestra el listado de personas con su código, nombre, apellido y DNI, con opciones de búsqueda,
 * edición, eliminación directa y retorno a la vista principal.
 */
class ReporteGeneralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReporteGeneralBinding
    private lateinit var personaDAO: PersonaDAO
    private lateinit var adapter: PersonaAdapter
    private var listaPersonas = ArrayList<Persona>()

    companion object {
        const val EXTRA_EDITAR_PERSONA = "extra_editar_persona"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        personaDAO = PersonaDAO(this)

        configurarToolbar()
        configurarRecyclerView()
        configurarBuscador()
        configurarBotones()
        cargarDatos()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    override fun onDestroy() {
        super.onDestroy()
        personaDAO.close()
    }

    private fun configurarToolbar() {
        binding.toolbarReporte.setNavigationOnClickListener {
            finish()
        }
    }

    private fun configurarBotones() {
        // Botón REGRESAR explícito requerido en el flujo de la Diapositiva 21
        binding.btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun configurarRecyclerView() {
        adapter = PersonaAdapter(
            listaPersonas = listaPersonas,
            onEditarClick = { persona ->
                editarPersona(persona)
            },
            onEliminarClick = { persona, position ->
                confirmarEliminacion(persona, position)
            },
            onItemClick = { persona ->
                mostrarDetallePersona(persona)
            }
        )

        binding.rvPersonas.apply {
            layoutManager = LinearLayoutManager(this@ReporteGeneralActivity)
            adapter = this@ReporteGeneralActivity.adapter
        }
    }

    private fun configurarBuscador() {
        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s?.toString()?.trim() ?: ""
                if (filtro.isEmpty()) {
                    cargarDatos()
                } else {
                    val resultados = personaDAO.filtrarPersonas(filtro)
                    actualizarVistaConLista(resultados)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarDatos() {
        val personas = personaDAO.listarPersonas()
        actualizarVistaConLista(personas)
    }

    private fun actualizarVistaConLista(personas: List<Persona>) {
        listaPersonas.clear()
        listaPersonas.addAll(personas)
        adapter.actualizarLista(personas)

        // Contador de registros
        binding.tvContadorRegistros.text = getString(R.string.contador_registros, personas.size)

        // Estado vacío (Empty state)
        if (personas.isEmpty()) {
            binding.rvPersonas.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvPersonas.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        }
    }

    private fun editarPersona(persona: Persona) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_EDITAR_PERSONA, persona)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun confirmarEliminacion(persona: Persona, position: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_eliminar_titulo)
            .setMessage(
                getString(
                    R.string.dialog_eliminar_mensaje,
                    persona.nombre,
                    persona.apellido,
                    persona.dni
                )
            )
            .setPositiveButton(R.string.dialog_eliminar_positivo) { _, _ ->
                val filasEliminadas = personaDAO.eliminarPersona(persona.codigo)
                if (filasEliminadas > 0) {
                    adapter.eliminarItem(position)
                    val totalActual = personaDAO.contarRegistros()
                    binding.tvContadorRegistros.text =
                        getString(R.string.contador_registros, totalActual)

                    if (totalActual == 0) {
                        binding.rvPersonas.visibility = View.GONE
                        binding.layoutEmptyState.visibility = View.VISIBLE
                    }

                    Snackbar.make(
                        binding.reporteCoordinator,
                        R.string.msg_eliminacion_exitosa,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        binding.reporteCoordinator,
                        R.string.msg_error_operacion,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(R.string.dialog_eliminar_negativo, null)
            .show()
    }

    private fun mostrarDetallePersona(persona: Persona) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Información de Registro")
            .setMessage(
                """
                Código: #${persona.codigo}
                Nombre: ${persona.nombre}
                Apellido: ${persona.apellido}
                DNI: ${persona.dni}
                """.trimIndent()
            )
            .setPositiveButton("Editar") { _, _ ->
                editarPersona(persona)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }
}
