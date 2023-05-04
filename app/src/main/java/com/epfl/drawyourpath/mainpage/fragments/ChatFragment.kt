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

class ChatFragment: Fragment(R.layout.fragment_chat_list){

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<Chat>()



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
            // Create a new instance of ChatDetailFragment
            val chatDetailFragment = ChatOpenFragment.newInstance(selectedChat)

            // Replace the current fragment with the ChatDetailFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, chatDetailFragment)
                .addToBackStack(null) // Add the transaction to the back stack
                .commit()
        }

        chatRecyclerView.adapter = chatAdapter
    }
}
