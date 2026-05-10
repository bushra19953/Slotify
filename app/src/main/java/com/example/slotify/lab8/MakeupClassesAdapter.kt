package com.example.slotify.lab8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

class MakeupClassesAdapter(private val classes: List<MakeupClass>) :
    RecyclerView.Adapter<MakeupClassesAdapter.ClassViewHolder>() {

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.makeup_subject)
        val classTime: TextView = itemView.findViewById(R.id.makeup_time)
        val classVenue: TextView = itemView.findViewById(R.id.makeup_venue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_makeup_tile, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentClass = classes[position]
        holder.subjectName.text = currentClass.subject
        holder.classTime.text = currentClass.time
        holder.classVenue.text = currentClass.venue
    }

    override fun getItemCount(): Int {
        return classes.size
    }
}