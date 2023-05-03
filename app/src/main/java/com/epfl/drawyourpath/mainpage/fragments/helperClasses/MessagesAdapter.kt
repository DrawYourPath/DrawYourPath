package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent

class MessagesAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TEXT = 1
        private const val VIEW_TYPE_PICTURE = 2
        private const val VIEW_TYPE_RUN_PATH = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_text, parent, false)
                TextMessageViewHolder(view)
            }
            VIEW_TYPE_PICTURE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_picture, parent, false)
                PictureMessageViewHolder(view)
            }
            VIEW_TYPE_RUN_PATH -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_run_path, parent, false)
                RunPathMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_TEXT -> {
                val textHolder = holder as TextMessageViewHolder
                val content = message.content as MessageContent.Text
                textHolder.textMessage.text = content.text
            }
            VIEW_TYPE_PICTURE -> {
                val pictureHolder = holder as PictureMessageViewHolder
                val content = message.content as MessageContent.Picture
                pictureHolder.image.setImageBitmap(content.image)
            }
            VIEW_TYPE_RUN_PATH -> {
                val runPathHolder = holder as RunPathMessageViewHolder
                // You can customize how to display RunPath message content here
            }
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].content) {
            is MessageContent.Text -> VIEW_TYPE_TEXT
            is MessageContent.Picture -> VIEW_TYPE_PICTURE
            is MessageContent.RunPath -> VIEW_TYPE_RUN_PATH
        }
    }

    class TextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.textMessage)
    }

    class PictureMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageMessage)
    }

    class RunPathMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // You can add views for displaying RunPath message content here
    }
}
