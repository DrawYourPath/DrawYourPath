package com.epfl.drawyourpath.mainpage.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ChatAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_chatFragment_isDisplayed() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check fragment is chats
        onView(withId(R.id.chat_list_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_chatListRecyclerView_isVisible() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check if chatListRecyclerView is visible
        onView(withId(R.id.chatListRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_chatListItem_isVisible() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check if the first chat item in the list is visible
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ChatAdapter.ChatViewHolder>(0))
            .check(matches(hasDescendant(withId(R.id.chatTitleTextView))))
    }

    @Test
    fun test_clickingChatItem_opensChatOpenFragment() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Click the first chat item in the list
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ChatAdapter.ChatViewHolder>(0, click()))

        // Check if the ChatOpenFragment is displayed
        onView(withId(R.id.chat_open_fragment)).check(matches(isDisplayed()))
    }


    @Test
    fun test_typingMessage_updatesMessageEditText() {
        navigateToChatOpenFragment()

        // Type a message
        val message = "Hello, world!"
        onView(withId(R.id.messageEditText)).perform(typeText(message))

        // Check if the messageEditText contains the typed message
        onView(withId(R.id.messageEditText)).check(matches(withText(message)))
    }

    @Test
    fun test_sendingMessage_addsMessageToRecyclerView() {
        navigateToChatOpenFragment()

        // Type a message
        val message = "Hello, world!"
        onView(withId(R.id.messageEditText)).perform(typeText(message), closeSoftKeyboard())

        // Click the send message button
        onView(withId(R.id.sendMessageButton)).perform(click())

        // Check if the message appears in the messagesRecyclerView as the last item
        onView(withId(R.id.messagesRecyclerView))
            .check(matches(hasDescendant(withText(message))))
    }

    private fun navigateToChatOpenFragment() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Click the first chat item in the list
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ChatAdapter.ChatViewHolder>(0, click()))
    }
}
