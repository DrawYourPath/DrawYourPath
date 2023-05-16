package com.epfl.drawyourpath.mainpage.fragments

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ChatAdapter
import junit.framework.TestCase.assertEquals
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testChatFragmentisDisplayed() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())
        // wait for the fragment to load!
        Thread.sleep(1000)
        // Check fragment is chats
        onView(withId(R.id.chat_list_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testChatListRecyclerViewisVisible() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check if chatListRecyclerView is visible
        onView(withId(R.id.chatListRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testChatListItemIsVisible() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check if the first chat item in the list is visible
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ChatAdapter.ChatViewHolder>(0))
            .check(matches(hasDescendant(withId(R.id.chatTitleTextView))))
    }

    @Test
    fun testClickingChatItemOpensChatOpenFragment() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Click the first chat item in the list
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ChatAdapter.ChatViewHolder>(0, click()))

        // Check if the ChatOpenFragment is displayed
        onView(withId(R.id.chat_open_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testTypingMessageUpdatesMessageEditText() {
        navigateToChatOpenFragment()

        // Type a message
        val message = "Hello, world!"
        onView(withId(R.id.messageEditText)).perform(typeText(message))

        // Check if the messageEditText contains the typed message
        onView(withId(R.id.messageEditText)).check(matches(withText(message)))
    }

    @Test
    fun testSendingMessageAddsMessageToRecyclerView() {
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

        // Click the first chat item in the list of chats
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ChatAdapter.ChatViewHolder>(0, click()))
    }

    @Test
    fun testChatListItemDeleteButtonIsVisibleAndClickable() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check if the delete button of the first chat item in the list is visible and clickable
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ChatAdapter.ChatViewHolder>(0))
            .check(matches(hasDescendant(allOf(withId(R.id.deleteButton), isDisplayed(), isClickable()))))
    }

    @Test
    fun testClickingDeleteButtonRemovesChatItemFromRecyclerView() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Get the initial number of chat items
        var initialItemCount = 0
        onView(withId(R.id.chatListRecyclerView)).check { view, _ ->
            initialItemCount = (view as RecyclerView).adapter?.itemCount ?: 0
        }

        // Click the delete button of the first chat item in the list
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ChatAdapter.ChatViewHolder>(0, clickChildViewWithId(R.id.deleteButton)))

        // Check if the number of chat items is reduced
        onView(withId(R.id.chatListRecyclerView)).check { view, _ ->
            val newItemCount = (view as RecyclerView).adapter?.itemCount ?: 0
            assertEquals(initialItemCount - 1, newItemCount)
        }
    }

    private fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(ViewGroup::class.java))
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }
}
