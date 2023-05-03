package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.MessagesAdapter

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the views
        val sendImageButton = view.findViewById<ImageButton>(R.id.sendImageButton)
        val sendRunButton = view.findViewById<ImageButton>(R.id.sendRunButton)
        val sendMessageButton = view.findViewById<ImageButton>(R.id.sendMessageButton)

        val messagesRecyclerView = view.findViewById<RecyclerView>(R.id.messagesRecyclerView)

        // Set up the RecyclerView with an empty list to start with
        val messages = mutableListOf<Message>()
        messagesRecyclerView.adapter = MessagesAdapter(messages)
        messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up click listeners for the buttons
        sendImageButton.setOnClickListener {
            // Handle sending an image message
        }

        sendRunButton.setOnClickListener {
            // Handle sending a run message
        }

        sendMessageButton.setOnClickListener {
            // Handle sending a text message
        }
    }
}
