package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.ChatPreview

// Class responsible for providing views that represent items in a data set.
class ChatAdapter(
    // A list of chat previews
    private val chatPreviews: List<ChatPreview>,
    // Listener that is called when a chat preview is clicked
    private val clickListener: (ChatPreview) -> Unit,
    // Listener that is called when the delete button is clicked for a chat preview
    private val deleteListener: (ChatPreview) -> Unit,
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // ViewHolder provides a reference to the views for each chat preview in the item list
    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chatTitleTextView: TextView = view.findViewById(R.id.chatTitleTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)

        // Binds the chat preview to the ViewHolder and sets the click and delete listeners
        fun bind(chatPreview: ChatPreview, clickListener: (ChatPreview) -> Unit, deleteListener: (ChatPreview) -> Unit) {
            itemView.setOnClickListener { clickListener(chatPreview) } // Sets the click listener for the chat preview
            deleteButton.setOnClickListener { deleteListener(chatPreview) } // Sets the delete listener for the chat preview
        }
    }

    // Creates a new ViewHolder when there are no existing ViewHolders that can be reused
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    // Binds the data to the ViewHolder
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatPreview = chatPreviews[position]
        val chatTitle = chatPreview.title ?: "Chat ${position + 1}" // If the chat preview has no title, default to "Chat #"
        holder.chatTitleTextView.text = chatTitle // Sets the chat title
        holder.bind(chatPreview, clickListener, deleteListener) // Binds the chat preview to the ViewHolder
    }

    // Returns the total number of chat previews in the list
    override fun getItemCount() = chatPreviews.size
}
