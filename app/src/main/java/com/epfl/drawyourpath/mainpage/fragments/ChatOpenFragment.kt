package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.chat.Chat
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.login.launchLoginActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.MessagesAdapter
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.RunPopupAdapter
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class ChatOpenFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: ImageButton
    private lateinit var sendImageButton: ImageButton
    private lateinit var sendRunButton: ImageButton
    private lateinit var database: Database
    private lateinit var chatId: String
    private lateinit var runPopupAdapter: RunPopupAdapter
    private lateinit var userModelCachedVar: UserModelCached

    // Initialize the list of messages
    private val messagesList = mutableListOf<Message>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendMessageButton = view.findViewById(R.id.sendMessageButton)
        sendImageButton = view.findViewById(R.id.sendImageButton)
        sendRunButton = view.findViewById(R.id.sendRunButton)

        // Get the database instance
        val userModelCached: UserModelCached by activityViewModels()
        userModelCachedVar = userModelCached

        database = userModelCached.getDatabase()

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

        messagesAdapter = MessagesAdapter(messagesList, userId)
        messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        messagesRecyclerView.adapter = messagesAdapter

        // Get the chat object from arguments
        val chat: Chat? = arguments?.getSerializable(ARG_CHAT) as Chat?
        chatId = arguments?.getString(ARG_CHAT_ID) ?: ""

        // Populate the messagesList with real messages from the Chat object

        chat?.getMessages()?.let {
            messagesList.addAll(it)
            messagesAdapter.notifyDataSetChanged()
        }

        sendMessageButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Replace "your_sender_id" with the actual sender ID
                val newMessage = Message.createTextMessage(userId, messageText, System.currentTimeMillis())
                messagesList.add(newMessage)
                messagesAdapter.notifyDataSetChanged()
                messageEditText.setText("")
                database.addChatMessage(chatId, newMessage)
            }
        }

        sendImageButton.setOnClickListener {
            // Handle sending image message here
        }

        sendRunButton.setOnClickListener {
            // Observe the LiveData object
            userModelCached.getRunHistory().observe(viewLifecycleOwner) { runHistory ->
                val runPopupMenu = PopupMenu(requireContext(), sendRunButton)
                runHistory.forEach { run ->
                    runPopupMenu.menu.add(run.getDate())
                }
                runPopupMenu.setOnMenuItemClickListener { menuItem ->
                    val selectedRunTitle = menuItem.title.toString()
                    // Find the selected run in the user's run history
                    val selectedRun =
                        runHistory.firstOrNull { it.getDate() == selectedRunTitle }
                    if (selectedRun != null) {
                        // If a run is selected, create a new Message
                        val newMessage = Message.createRunPathMessage(
                            userId,
                            selectedRun,
                            System.currentTimeMillis(),
                        ) // replace with appropriate method to create run path message
                        messagesList.add(newMessage)
                        messagesAdapter.notifyDataSetChanged()
                        database.addChatMessage(chatId, newMessage)
                    }
                    true
                }
                runPopupMenu.show()
            }
        }
    }
    companion object {
        private const val ARG_CHAT = "arg_chat"
        private const val ARG_CHAT_ID = "arg_chat_id"

        fun newInstance(chat: Chat, chatId: String): ChatOpenFragment {
            val fragment = ChatOpenFragment()
            val args = Bundle()
            args.putSerializable(ARG_CHAT, chat)
            args.putString(ARG_CHAT_ID, chatId)
            fragment.arguments = args
            return fragment
        }
    }
}
