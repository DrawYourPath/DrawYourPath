package com.epfl.drawyourpath.mainpage.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.epfl.drawyourpath.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * ChatFragment class that represents the main chat list screen.
 * This is where all the user's chats are listed.
 */
class ChatFragment() : Fragment(R.layout.fragment_chat_list) {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatPreview>()

    /**
     * This method is called after the fragment's view has been created.
     * It initializes the chat list and binds the data to the RecyclerView.
     * TODO: refactor this method to use more helper functions to make it more readable
     */
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

        // If the user is not logged in, launch the LoginActivity
        if (currentUser == null) {
            launchLoginActivity(requireActivity())
            return
        }

        val userId = currentUser.getUid()

        // Get user data and update chatList
        getUserChatPreviews(database, userId).thenAccept { chatPreviews ->
            chatList.clear()
            chatList.addAll(chatPreviews)
            chatAdapter.notifyDataSetChanged()
        }

        // code related to the tranzition to a new fragment when a chat is selected and to delete a chat when the delete button is clicked
        chatAdapter = ChatAdapter(
            chatList,
            { selectedChatPreview ->
                // When a chat preview is selected, convert it to a chat and open the ChatDetailFragment
                chatPreviewToChat(database, selectedChatPreview).observe(viewLifecycleOwner){chat->
                    // Create a new instance of ChatDetailFragment
                    val chatDetailFragment = ChatOpenFragment.newInstance(chat, selectedChatPreview.conversationId!!)

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
                // When the delete button is clicked, delete the chat conversation
                database.removeChatMember(userId, selectedChatPreviewToDelete.conversationId!!)
                    .thenAccept() {
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

        // Code related to the new chat button and the popup functionality.

        // Initialize new chat button
        val newChatButton: FloatingActionButton = view.findViewById(R.id.addChatButton)
        newChatButton.setOnClickListener {
            // When the new chat button is clicked, open a popup menu with a list of friends
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
                        // Find the selected friend's ID
                        val selectedFriendId = userData.friendList?.firstOrNull { friendId ->
                            try {
                                database.getUsername(friendId).get() == selectedFriendUsername
                            } catch (e: Exception) {
                                Log.e(TAG, "Error while getting friend username: ", e)
                                false
                            }
                        }
                        if (selectedFriendId != null) {
                            // If a friend is selected, create a new chat conversation
                            val chatName = "Chat with $selectedFriendUsername"
                            val membersList = listOf(userId, selectedFriendId)
                            val welcomeMessage = getString(R.string.chat_welcome_message)
                            database.createChatConversation(
                                chatName,
                                membersList,
                                userId,
                                welcomeMessage,
                            )
                                .thenAccept { conversationId ->
                                    Log.d(
                                        "ChatCreation",
                                        "Chat created with conversationId: $conversationId",
                                    )

                                    // Create a new ChatPreview object for the newly created chat
                                    val welcomeMessageContent =
                                        MessageContent.Text(getString(R.string.chat_welcome_message)) // Replace with your MessageContent creation
                                    val welcomeMessage = Message(
                                        0L,
                                        userId,
                                        welcomeMessageContent,
                                        System.currentTimeMillis(),
                                    )
                                    val newChatPreview =
                                        ChatPreview(conversationId, chatName, welcomeMessage)

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

    /**
     * This function fetches the list of ChatPreview objects associated with a user from the database.
     *
     * @param database The Database object used to fetch user data and chat previews.
     * @param userId The ID of the user whose chat previews are to be fetched.
     * @return A CompletableFuture that contains a list of ChatPreview objects when completed.
     */
    fun getUserChatPreviews(
        database: Database,
        userId: String,
    ): CompletableFuture<List<ChatPreview>> {
        // Return a CompletableFuture that fetches the user data from the database
        return database.getUserData(userId)
            .thenCompose { userData ->
                // Get the list of conversation IDs associated with the user
                // If the user's chatList is null, use an empty list
                val chatList = userData.chatList ?: emptyList()

                // Create a list of CompletableFuture objects, each fetching a chat preview for a conversation ID
                val chatPreviewFutures = chatList.map { conversationId ->
                    database.getChatPreview(conversationId)
                }

                // Create a new CompletableFuture that completes when all chat preview futures complete
                CompletableFuture.allOf(*chatPreviewFutures.toTypedArray())
                    .thenCompose {
                        CompletableFuture.allOf(*chatPreviewFutures.toTypedArray())
                    }.thenApply { listMilestones ->
                        // Create a list of ChatPreview objects from the completed futures
                        chatPreviewFutures.mapNotNull { future ->
                            try {
                                future.get()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error while getting chat preview: ", e)
                                null
                            }
                        }
                    }
            }
    }

    /**
     * This function converts a ChatPreview object to a full Chat object.
     * It fetches the actual messages of the chat from the database and adds them to the Chat object.
     *
     * @param database The Database object used to fetch chat messages.
     * @param chatPreview The ChatPreview object to be converted into a Chat object.
     * @return Live data iobject that contains the chat
     * @throws IllegalArgumentException If the conversationId in the chatPreview is null.
     */
    fun chatPreviewToChat(database: Database, chatPreview: ChatPreview): LiveData<Chat> {
        // Get the conversationId from the chatPreview
        val conversationId = chatPreview.conversationId

        // If conversationId is null, throw an IllegalArgumentException
        if (conversationId == null) {
            throw IllegalArgumentException("Invalid conversationId")
        }

        // Return a CompletableFuture that fetches the chat messages from the database
        // and adds them to a new Chat object
        val messageListener = database.getChatMessages(conversationId)
        // Create a new Chat object
        val liveChat = MutableLiveData<Chat>()

        // For each message in the messages list, add it to the chat
        messageListener.observe(viewLifecycleOwner) {messagesList->
            val chat = Chat()
            messagesList.forEach{message ->
                chat.addMessage(message)
            }
            liveChat.postValue(chat)

        }
        return liveChat
    }
}
