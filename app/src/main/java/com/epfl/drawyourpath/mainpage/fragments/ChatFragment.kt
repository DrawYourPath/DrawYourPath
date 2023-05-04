package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ChatAdapter

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRecyclerView = view.findViewById(R.id.chatListRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Add mock chats to the chatList
        for (i in 1..10) {
            val chat = Chat()
            chatList.add(chat)
        }

        chatAdapter = ChatAdapter(chatList) { selectedChat ->
            // Handle chat item click here
            // For example, navigate to the ChatDetailFragment and pass the selectedChat
        }

        chatRecyclerView.adapter = chatAdapter
    }
}

