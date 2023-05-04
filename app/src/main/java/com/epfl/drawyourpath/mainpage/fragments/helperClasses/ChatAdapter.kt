package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Chat

class ChatAdapter(private val chatList: List<Chat>, private val clickListener: (Chat) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val chatTitleTextView: TextView = view.findViewById(R.id.chatTitleTextView)

        fun bind(chat: Chat, clickListener: (Chat) -> Unit) {
            itemView.setOnClickListener { clickListener(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        val chatTitle = "Chat ${position + 1}" // Customize this to display the desired chat title
        holder.chatTitleTextView.text = chatTitle
        holder.bind(chat, clickListener)
    }

    override fun getItemCount() = chatList.size
}
