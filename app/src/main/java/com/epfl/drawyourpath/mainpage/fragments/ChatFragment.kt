package com.epfl.drawyourpath.mainpage.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.database.ChatPreview
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.login.launchLoginActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ChatAdapter
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class ChatFragment() : Fragment(R.layout.fragment_chat_list) {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatPreview>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the database instance
        val userModelCached: UserModelCached by activityViewModels()
        val database = userModelCached.getDatabase()

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

        // !!!!!!!!!!!!testing adding a chats!!!!!!!!!!!!!!

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
        // !!!!!!!!!!!!!!!!!!end of testing!!!!!!!!!!!!!!!!!!!!

        // Get user data and update chatList
        getUserChatPreviews(database, userId).thenAccept { chatPreviews ->
            chatList.clear()
            chatList.addAll(chatPreviews)
            chatAdapter.notifyDataSetChanged()
        }

        chatAdapter = ChatAdapter(
            chatList,
            { selectedChatPreview ->
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
            },
            { selectedChatPreviewToDelete ->
                // Delete the chat conversation
                database.removeChatMember(userId, selectedChatPreviewToDelete.conversationId!!).thenAccept() {
                    // Remove the deleted chat from chatList and notify the adapter
                    chatList.remove(selectedChatPreviewToDelete)
                    requireActivity().runOnUiThread {
                        chatAdapter.notifyDataSetChanged()
                    }
                }
            },
        )

        chatRecyclerView.adapter = chatAdapter




        // Get user data
        val userAccountFuture = database.getUserData(userId)

        // Initialize new chat button
        val newChatButton: FloatingActionButton = view.findViewById(R.id.addChatButton)
        newChatButton.setOnClickListener {
            userAccountFuture.thenCompose { userData ->
                val friends = userData.friendList ?: emptyList()
                val futures = friends.map { friendId -> database.getUsername(friendId) }
                CompletableFuture.allOf(*futures.toTypedArray()).thenApply {
                    val friendsUsernames = futures.mapNotNull { future -> future.get() }
                    friendsUsernames to userData
                }
            }.thenAccept { (friendsUsernames, userData) ->
                val popupMenu = PopupMenu(requireContext(), newChatButton)
                friendsUsernames.forEach { friendUsername ->
                    popupMenu.menu.add(friendUsername)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val selectedFriendUsername = menuItem.title.toString()
                    // Run this in another thread
                    Executors.newSingleThreadExecutor().execute {
                        val selectedFriendId = userData.friendList?.firstOrNull { friendId ->
                            try {
                                database.getUsername(friendId).get() == selectedFriendUsername
                            } catch (e: Exception) {
                                Log.e(TAG, "Error while getting friend username: ", e)
                                false
                            }
                        }
                        if (selectedFriendId != null) {
                            val chatName = "Chat with $selectedFriendUsername"
                            val membersList = listOf(userId, selectedFriendId)
                            val welcomeMessage = "Welcome to the chat!"
                            database.createChatConversation(chatName, membersList, userId, welcomeMessage)
                                .thenAccept { conversationId ->
                                    Log.d("ChatCreation", "Chat created with conversationId: $conversationId")

                                    // Create a new ChatPreview object for the newly created chat
                                    val welcomeMessageContent = MessageContent.Text("Welcome to the chat!") // Replace with your MessageContent creation
                                    val welcomeMessage = Message(0L, userId, welcomeMessageContent, System.currentTimeMillis())
                                    val newChatPreview = ChatPreview(conversationId, chatName, welcomeMessage)

                                    // Add the new ChatPreview to the chatList and notify the adapter
                                    requireActivity().runOnUiThread {
                                        chatList.add(newChatPreview)
                                        chatAdapter.notifyDataSetChanged()
                                    }
                                }
                        }
                    }
                    true
                }
                popupMenu.show()
            }.exceptionally { exception ->
                Log.e(TAG, "Error while getting UserAccount: ", exception)
                null
            }
        }

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


