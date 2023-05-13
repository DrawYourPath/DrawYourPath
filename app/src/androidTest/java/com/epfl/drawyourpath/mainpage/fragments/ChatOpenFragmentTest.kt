package com.epfl.drawyourpath.mainpage.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatOpenFragmentTest {

    @Test
    fun chatOpenFragmentIsDisplayed() {
        val scenario = launchFragmentInContainer<ChatOpenFragment>(
            themeResId = R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.chat_open_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun messagesRecyclerViewIsDisplayed() {
        val scenario = launchFragmentInContainer<ChatOpenFragment>(
            themeResId = R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.messagesRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTypingMessageUpdatesMessageEditText() {
        val scenario = launchFragmentInContainer<ChatOpenFragment>(
            themeResId = R.style.Theme_Bootcamp,
        )

        // Type a message
        val message = "Hello, world!"
        onView(withId(R.id.messageEditText)).perform(typeText(message))

        // Check if the messageEditText contains the typed message
        onView(withId(R.id.messageEditText)).check(matches(withText(message)))
    }

    @Test
    fun testSendingMessageAddsMessageToRecyclerView() {
        val scenario = launchFragmentInContainer<ChatOpenFragment>(
            themeResId = R.style.Theme_Bootcamp,
        )

        // Type a message
        val message = "Hello, world!"
        onView(withId(R.id.messageEditText)).perform(typeText(message), closeSoftKeyboard())

        // Click the send message button
        onView(withId(R.id.sendMessageButton)).perform(click())

        // Check if the message appears in the messagesRecyclerView as the last item
        onView(withId(R.id.messagesRecyclerView))
            .check(matches(hasDescendant(withText(message))))
    }

    @Test
    fun messageEditTextIsClearedAfterSendingMessage() {
        val scenario = launchFragmentInContainer<ChatOpenFragment>(
            themeResId = R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.messageEditText)).perform(typeText("Test message"), closeSoftKeyboard())
        onView(withId(R.id.sendMessageButton)).perform(click())

        onView(withId(R.id.messageEditText)).check(matches(withText("")))
    }
}
