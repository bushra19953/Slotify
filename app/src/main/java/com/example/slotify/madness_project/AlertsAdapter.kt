package com.example.slotify.madness_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu // Added this
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

// CHANGE: Changed List to MutableList so items can be removed/edited
class AlertsAdapter(private val alerts: MutableList<AlertItem>) :
    RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTag: TextView = view.findViewById(R.id.tvTag)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvTitle: TextView = view.findViewById(R.id.tvAlertTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvAlertDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.tvTitle.text = alert.title
        holder.tvDesc.text = alert.description
        holder.tvTime.text = alert.time
        holder.tvTag.text = alert.type.uppercase()

        // --- LONG CLICK LOGIC ---
        holder.itemView.setOnLongClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add(0, 1, 0, "Edit")
            popup.menu.add(0, 2, 1, "Mark as Complete")
            popup.menu.add(0, 3, 2, "Delete")

            popup.setOnMenuItemClickListener { item ->
                val context = view.context as? AlertsActivity // Safe cast to Activity

                when (item.itemId) {
                    1 -> {
                        context?.showEditDialog(holder.adapterPosition)
                        true
                    }
                    2, 3 -> {
                        context?.removeItem(holder.adapterPosition)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            true
        }

        // --- STYLING LOGIC ---
        val context = holder.itemView.context
        holder.tvTag.setTextColor(ContextCompat.getColor(context, R.color.text_primary))

        // Ensure itemView is cast to CardView for background changes
        val cardView = holder.itemView as? com.google.android.material.card.MaterialCardView

        when (alert.type) {
            "Urgent" -> {
                cardView?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.orange_card))
                holder.tvTag.setBackgroundColor(ContextCompat.getColor(context, R.color.light_orange))
            }
            "Info" -> {
                cardView?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.medium_blue))
                holder.tvTag.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
            }
            "Reminder" -> {
                cardView?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_card))
                holder.tvTag.setBackgroundColor(ContextCompat.getColor(context, R.color.light_purple))
            }
            else -> {
                cardView?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white_card))
                holder.tvTag.setBackgroundColor(ContextCompat.getColor(context, R.color.button_bg))
            }
        }
    }

    override fun getItemCount() = alerts.size
}