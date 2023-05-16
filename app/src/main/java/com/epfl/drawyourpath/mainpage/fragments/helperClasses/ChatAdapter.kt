package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.ChatPreview

class ChatAdapter(private val chatPreviews: List<ChatPreview>, private val clickListener: (ChatPreview) -> Unit, private val deleteListener: (ChatPreview) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chatTitleTextView: TextView = view.findViewById(R.id.chatTitleTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)


        fun bind(chatPreview: ChatPreview, clickListener: (ChatPreview) -> Unit, deleteListener: (ChatPreview) -> Unit) {
            itemView.setOnClickListener { clickListener(chatPreview) }
            deleteButton.setOnClickListener { deleteListener(chatPreview) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatPreview = chatPreviews[position]
        val chatTitle = chatPreview.title ?: "Chat ${position + 1}"
        holder.chatTitleTextView.text = chatTitle
        holder.bind(chatPreview, clickListener, deleteListener)
    }

    override fun getItemCount() = chatPreviews.size
}
