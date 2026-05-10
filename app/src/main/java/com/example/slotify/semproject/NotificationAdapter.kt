package com.example.slotify.semproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.databinding.ItemNotificationBinding
import com.example.slotify.semproject.model.NotificationItem

class NotificationAdapter(
    private val onItemLongClicked: (NotificationItem) -> Unit
) : ListAdapter<NotificationItem, NotificationAdapter.NotificationViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position), onItemLongClicked)
    }

    class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationItem, onLongClick: (NotificationItem) -> Unit) {
            binding.root.setCardBackgroundColor(android.graphics.Color.WHITE)
            binding.tvNotificationTitle.text = item.title
            binding.tvNotificationDetails.text = "${item.dateTime} • ${item.venue}"

            itemView.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<NotificationItem>() {
            override fun areItemsTheSame(old: NotificationItem, new: NotificationItem) =
                old.title == new.title && old.dateTime == new.dateTime
            override fun areContentsTheSame(old: NotificationItem, new: NotificationItem) = old == new
        }
    }
}