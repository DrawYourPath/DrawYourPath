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
class ChatOpenFragmentTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Test to verify if the ChatOpenFragment is displayed correctly
     */
    @Test
    fun chatOpenFragmentIsDisplayed() {
        navigateToChatOpenFragment()

        // Verify if the ChatOpenFragment is displayed
        onView(withId(R.id.chat_open_fragment)).check(matches(isDisplayed()))
    }

    /**
     *Test to verify if the messages RecyclerView is displayed in the ChatOpenFragment
     */
    @Test
    fun messagesRecyclerViewIsDisplayed() {
        navigateToChatOpenFragment()

        // Verify if the messages RecyclerView is displayed
        onView(withId(R.id.messagesRecyclerView))
            .check(matches(isDisplayed()))
    }

    /**
     *Test to verify if typing a message updates the message EditText field
     */
    @Test
    fun testTypingMessageUpdatesMessageEditText() {
        navigateToChatOpenFragment()

        // Type a message
        val message = "Hello, world!"
        onView(withId(R.id.messageEditText)).perform(typeText(message))

        // Check if the messageEditText contains the typed message
        onView(withId(R.id.messageEditText)).check(matches(withText(message)))
    }

    /**
     * Test to verify if sending a message adds it to the RecyclerView
     */
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

    /**
     *Test to verify if the message EditText field is cleared after sending a message
     */
    @Test
    fun messageEditTextIsClearedAfterSendingMessage() {
        navigateToChatOpenFragment()

        // Type a message and send it
        onView(withId(R.id.messageEditText)).perform(typeText("Test message"), closeSoftKeyboard())
        onView(withId(R.id.sendMessageButton)).perform(click())

        // Check if the messageEditText is cleared after sending the message
        onView(withId(R.id.messageEditText)).check(matches(withText("")))
    }

    private fun navigateToChatOpenFragment() {
        // Go to chats
        onView(withId(R.id.chat_menu_item)).perform(click())

        // Click the first chat item in the list of chats
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ChatAdapter.ChatViewHolder>(0, click()))
    }
}
