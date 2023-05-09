package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.database.ChatPreview
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.login.launchLoginActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ChatAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class ChatFragment(private val database: Database) : Fragment(R.layout.fragment_chat_list) {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatPreview>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRecyclerView = view.findViewById(R.id.chatListRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch the userId of the current user
        val currentUser =
            if (database is MockDatabase) {
                MockAuth(forceSigned = true).getUser()
            } else {
                FirebaseAuth.getUser()
            }

        if (currentUser == null) {
            launchLoginActivity(requireActivity())
            return
        }

        val userId = currentUser.getUid()

        //!!!!!!!!!!!!!testing adding a chats!!!!!!!!!!!!!!

        // You can replace these values with your actual userId and desired chat names and members
        /**
        val chatNames = listOf("Chat 1", "Chat 2", "Chat 3")
        val membersLists = listOf(
            listOf(userId, "user2", "user3"),
            listOf(userId, "user3", "user4"),
            listOf(userId, "user4", "user5")
        )
        val welcomeMessage = "Welcome to the chat!"

        // Create the chat conversations
        chatNames.forEachIndexed { index, chatName ->
            val membersList = membersLists[index]
            database.createChatConversation(chatName, membersList, userId, welcomeMessage)
                .thenAccept { conversationId ->
                    Log.d("ChatCreation", "Chat created with conversationId: $conversationId")
                }
        }

        **/
        //!!!!!!!!!!!!!!!!!!end of testing!!!!!!!!!!!!!!!!!!!!


        // Get user data and update chatList
        getUserChatPreviews(database, userId).thenAccept { chatPreviews ->
            chatList.clear()
            chatList.addAll(chatPreviews)
            chatAdapter.notifyDataSetChanged()
        }

        chatAdapter = ChatAdapter(chatList) { selectedChatPreview ->
            chatPreviewToChat(database, selectedChatPreview).thenAccept { chat ->
                // Create a new instance of ChatDetailFragment
                val chatDetailFragment = ChatOpenFragment.newInstance(chat)

                // Replace the current fragment with the ChatDetailFragment
                requireActivity().runOnUiThread {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, chatDetailFragment)
                        .addToBackStack(null) // Add the transaction to the back stack
                        .commit()
                }
            }
        }


        chatRecyclerView.adapter = chatAdapter
    }
    fun getUserChatPreviews(database: Database, userId: String): CompletableFuture<List<ChatPreview>> {
        return database.getUserData(userId)
            .thenCompose { userData ->
                val chatList = userData.chatList ?: emptyList()
                val chatPreviewFutures = chatList.map { conversationId ->
                    database.getChatPreview(conversationId)
                }

                CompletableFuture.allOf(*chatPreviewFutures.toTypedArray())
                    .thenApply {
                        chatPreviewFutures.map { it.join() }
                    }
            }
    }


    fun chatPreviewToChat(database: Database, chatPreview: ChatPreview): CompletableFuture<Chat> {
        val conversationId = chatPreview.conversationId
        if (conversationId == null) {
            throw IllegalArgumentException("Invalid conversationId")
        }

        return database.getChatMessages(conversationId).thenApply { messages ->
            val chat = Chat()
            messages.forEach { message ->
                chat.addMessage(message)
            }
            chat
        }
    }
}






