package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.MessagesAdapter

class ChatOpenFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: ImageButton
    private lateinit var sendImageButton: ImageButton
    private lateinit var sendRunButton: ImageButton

    // This is a sample list of messages, replace it with actual messages data
    private val messagesList = mutableListOf<Message>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendMessageButton = view.findViewById(R.id.sendMessageButton)
        sendImageButton = view.findViewById(R.id.sendImageButton)
        sendRunButton = view.findViewById(R.id.sendRunButton)

        messagesAdapter = MessagesAdapter(messagesList)
        messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        messagesRecyclerView.adapter = messagesAdapter

        // Load existing messages and display them
        loadMessages()

        sendMessageButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Replace "your_sender_id" with the actual sender ID
                val newMessage = Message.createTextMessage("your_sender_id", messageText, System.currentTimeMillis())
                messagesList.add(newMessage)
                messagesAdapter.notifyDataSetChanged()
                messageEditText.setText("")
            }
        }

        sendImageButton.setOnClickListener {
            // Handle sending image message here
        }

        sendRunButton.setOnClickListener {
            // Handle sending run path message here
        }
    }

    private fun loadMessages() {
        // Dummy messages
        val dummyMessages = listOf(
            Message.createTextMessage("user1", "Hey, how's it going?", System.currentTimeMillis() - 1000 * 60 * 60 * 2),
            Message.createTextMessage("user2", "It's going great! How about you?", System.currentTimeMillis() - 1000 * 60 * 60 * 1),
            Message.createTextMessage("user1", "I'm doing well, thanks!", System.currentTimeMillis() - 1000 * 60 * 30),
            Message.createTextMessage("user2", "Glad to hear that!", System.currentTimeMillis() - 1000 * 60 * 5),
        )

        messagesList.addAll(dummyMessages)

        // Update the adapter by calling notifyDataSetChanged()
        messagesAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val ARG_CHAT = "arg_chat"

        fun newInstance(chat: Chat): ChatOpenFragment {
            val fragment = ChatOpenFragment()
            val args = Bundle()
            args.putSerializable(ARG_CHAT, chat)
            fragment.arguments = args
            return fragment
        }
    }
}
