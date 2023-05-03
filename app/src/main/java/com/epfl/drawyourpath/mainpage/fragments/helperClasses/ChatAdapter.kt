package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.chat.MessageContent

class ChatAdapter(private val chat: Chat) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_PICTURE = 2
        const val VIEW_TYPE_RUN_PATH = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (chat.getMessages()[position].content) {
            is MessageContent.Text -> VIEW_TYPE_TEXT
            is MessageContent.Picture -> VIEW_TYPE_PICTURE
            is MessageContent.RunPath -> VIEW_TYPE_RUN_PATH
        }
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
        val messageContent = chat.getMessages()[position].content
        when (holder) {
            is TextMessageViewHolder -> holder.bind(messageContent as MessageContent.Text)
            is PictureMessageViewHolder -> holder.bind(messageContent as MessageContent.Picture)
            is RunPathMessageViewHolder -> holder.bind(messageContent as MessageContent.RunPath)
        }
    }

    override fun getItemCount(): Int = chat.getMessages().size

    inner class TextMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageTextView: TextView = view.findViewById(R.id.messageTextView)

        fun bind(messageContent: MessageContent.Text) {
            messageTextView.text = messageContent.text
        }
    }

    inner class PictureMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageImageView: ImageView = view.findViewById(R.id.messageImageView)

        fun bind(messageContent: MessageContent.Picture) {
            messageImageView.setImageBitmap(messageContent.image)
        }
    }

    inner class RunPathMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val runPathInfoTextView: TextView = view.findViewById(R.id.runPathInfoTextView)

        fun bind(messageContent: MessageContent.RunPath) {
            val run = messageContent.run
            val runInfo = "Distance: ${run.distance} m, Duration: ${run.time} s, Calories: ${run.calories}"
            runPathInfoTextView.text = runInfo
        }
    }
}
