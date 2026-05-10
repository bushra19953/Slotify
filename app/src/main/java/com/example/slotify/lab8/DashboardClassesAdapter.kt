package com.example.slotify.lab8

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.example.slotify.R

class DashboardClassesAdapter(
    private val classes: List<DashboardClass>,
    private val onItemClick: (DashboardClass) -> Unit
) : RecyclerView.Adapter<DashboardClassesAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectName: TextView = view.findViewById(R.id.class_subject_name)
        val room: TextView = view.findViewById(R.id.class_room)
        val time: TextView = view.findViewById(R.id.class_time)
        val icon: ImageView = view.findViewById(R.id.class_icon)
        val statusIcon: ImageView = view.findViewById(R.id.class_status_icon)
        val iconContainer: FrameLayout = view.findViewById(R.id.class_icon_container)
        val classCard: MaterialCardView = view.findViewById(R.id.class_card)

        fun bind(classItem: DashboardClass) {
            subjectName.text = classItem.subjectName
            room.text = classItem.room
            time.text = classItem.time
            icon.setImageResource(classItem.iconResource)

            // Set background color for the card
            try {
                classCard.setCardBackgroundColor(Color.parseColor(classItem.backgroundColor))
            } catch (e: Exception) {
                // Fallback to default color if parsing fails
                classCard.setCardBackgroundColor(Color.parseColor("#001B48"))
            }

            // Set status icon
            statusIcon.setImageResource(R.drawable.ic_chevron_right)

            itemView.setOnClickListener {
                onItemClick(classItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun getItemCount(): Int = classes.size
}
