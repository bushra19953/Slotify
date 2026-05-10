package com.example.slotify.madness_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

class TimelineAdapter(private val items: List<TimelineItem>) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subject: TextView = view.findViewById(R.id.tvSubjectName)
        val time: TextView = view.findViewById(R.id.tvTimeSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.subject.text = item.subject
        holder.time.text = item.time
    }

    override fun getItemCount(): Int = items.size
}