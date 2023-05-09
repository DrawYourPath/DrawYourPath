package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.database.ChatPreview

class ChatAdapter(private val chatPreviews: List<ChatPreview>, private val clickListener: (ChatPreview) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chatTitleTextView: TextView = view.findViewById(R.id.chatTitleTextView)

        fun bind(chatPreview: ChatPreview, clickListener: (ChatPreview) -> Unit) {
            itemView.setOnClickListener { clickListener(chatPreview) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatPreview = chatPreviews[position]
        val chatTitle = chatPreview.title ?: "Chat ${position + 1}" // Use the title from ChatPreview if available
        holder.chatTitleTextView.text = chatTitle
        holder.bind(chatPreview, clickListener)
    }

    override fun getItemCount() = chatPreviews.size
}

