package com.mobdeve.s20.gonzales.miguel.wazte

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(private val locations: List<Location>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        holder.locationName.text = location.name
        holder.locationDistance.text = location.distance
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName: TextView = itemView.findViewById(R.id.location_name)
        val locationDistance: TextView = itemView.findViewById(R.id.location_distance)
    }

    data class Location(val name: String, val distance: String)
}