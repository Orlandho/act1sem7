package com.example.act1sem7.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.act1sem7.databinding.ItemPersonaBinding
import com.example.act1sem7.model.Persona

/**
 * Adaptador para el RecyclerView de personas en el Reporte General.
 * Provee enlaces para eventos de edición, eliminación e inspección de cada registro.
 */
class PersonaAdapter(
    private var listaPersonas: ArrayList<Persona>,
    private val onEditarClick: (Persona) -> Unit,
    private val onEliminarClick: (Persona, Int) -> Unit,
    private val onItemClick: ((Persona) -> Unit)? = null
) : RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    inner class PersonaViewHolder(val binding: ItemPersonaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val binding = ItemPersonaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        val persona = listaPersonas[position]
        with(holder.binding) {
            tvItemCodigo.text = persona.codigo.toString()
            tvItemNombreCompleto.text = persona.nombreCompleto
            tvItemDni.text = "DNI: ${persona.dni}"

            btnItemEditar.setOnClickListener {
                onEditarClick(persona)
            }

            btnItemEliminar.setOnClickListener {
                onEliminarClick(persona, holder.bindingAdapterPosition)
            }

            root.setOnClickListener {
                onItemClick?.invoke(persona)
            }
        }
    }

    override fun getItemCount(): Int = listaPersonas.size

    /**
     * Actualiza completamente el listado de personas en el adaptador.
     */
    fun actualizarLista(nuevaLista: List<Persona>) {
        listaPersonas.clear()
        listaPersonas.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    /**
     * Elimina un elemento del adaptador en una posición específica con animación.
     */
    fun eliminarItem(posicion: Int) {
        if (posicion in 0 until listaPersonas.size) {
            listaPersonas.removeAt(posicion)
            notifyItemRemoved(posicion)
            notifyItemRangeChanged(posicion, listaPersonas.size - posicion)
        }
    }
}
