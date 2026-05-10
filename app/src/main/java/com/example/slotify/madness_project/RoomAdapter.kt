package com.example.slotify.madness_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

class RoomAdapter(
    // Change 1: Use MutableList so we can swap data during search
    private var rooms: MutableList<RoomData>,
    private val onActionClicked: (RoomData, Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvRoomNumber)
        val tvDetails: TextView = view.findViewById(R.id.tvRoomDetails)
        val tvTags: TextView = view.findViewById(R.id.tvStatusTag)
        val btnAction: Button = view.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return ViewHolder(v)
    }
    // ... inside your RoomAdapter class

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        holder.tvNumber.text = room.number
        holder.tvDetails.text = room.desc
        holder.tvTags.text = room.tags

        if (room.isConflict) {
            holder.btnAction.text = "Resolve"
            holder.btnAction.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_light))
        } else {
            holder.btnAction.text = "Book"
            holder.btnAction.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.medium_blue))
        }

        holder.btnAction.setOnClickListener {
            // FIX: Change holder.bindingAdapterPosition to holder.bindingAdapterPosition
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onActionClicked(rooms[currentPos], currentPos)
            }
        }
    }

// ... rest of your class

    override fun getItemCount() = rooms.size

    // Change 2: ADD THIS FUNCTION.
    // This is what makes the Search Bar actually update the list on screen.
    fun updateData(newList: MutableList<RoomData>) {
        this.rooms = newList
        notifyDataSetChanged()
    }
}