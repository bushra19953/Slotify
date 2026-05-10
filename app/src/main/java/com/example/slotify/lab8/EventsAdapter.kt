package com.example.slotify.lab8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

class EventsAdapter(private val events: List<Event>) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventIcon: ImageView = itemView.findViewById(R.id.event_icon)
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val eventDate: TextView = itemView.findViewById(R.id.event_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_tile, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = events[position]
        holder.eventIcon.setImageResource(currentEvent.iconResId)
        holder.eventName.text = currentEvent.name
        holder.eventDate.text = currentEvent.date
    }

    override fun getItemCount(): Int {
        return events.size
    }
}