package com.example.SkyNetVpnService

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServerAdapter(
    private val servers: List<VpnServer>,
    private val onServerSelected: (VpnServer) -> Unit
) : RecyclerView.Adapter<ServerAdapter.ServerViewHolder>() {

    private var selectedPosition = -1
    var isEnabled: Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ServerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtLocation: TextView = view.findViewById(R.id.txtLocation)
        val txtIp: TextView = view.findViewById(R.id.txtIp)
        val root: View = view.findViewById(R.id.serverItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_server, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = servers[position]
        holder.txtLocation.text = server.location
        holder.txtIp.text = server.ip

        if (position == selectedPosition) {
            holder.root.setBackgroundColor(Color.parseColor("#40FFFFFF"))
        } else {
            holder.root.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.isEnabled = isEnabled
        holder.itemView.alpha = if (isEnabled) 1.0f else 0.5f

        holder.itemView.setOnClickListener {
            if (!isEnabled) return@setOnClickListener
            val previousSelected = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onServerSelected(server)
        }
    }

    override fun getItemCount() = servers.size
}
