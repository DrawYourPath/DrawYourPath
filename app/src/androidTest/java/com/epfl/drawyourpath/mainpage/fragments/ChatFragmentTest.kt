package com.epfl.drawyourpath.mainpage.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.helperClasses.ChatAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatFragmentTest {

    // This rule provides functional testing of a single Activity
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Test to verify if the chat fragment is displayed correctly
    @Test
    fun chatFragmentIsDisplayed() {
        // Create mock database instance
        val database = MockDatabase()

        // Launch the chat fragment in a container
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            ChatFragment(database)
        }

        // Verify if the chat fragment is displayed
        onView(withId(R.id.chat_list_fragment)).check(matches(isDisplayed()))
    }

    // Test to verify if the chat list is displayed in the chat fragment
    @Test
    fun chatListIsDisplayed() {
        // Create mock database instance
        val database = MockDatabase()

        // Launch the chat fragment in a container
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            ChatFragment(database)
        }

        // Verify if the chat list recycler view is displayed
        onView(withId(R.id.chatListRecyclerView))
            .check(matches(isDisplayed()))
    }

    // Test to verify if the chat list item is visible in the chat list recycler view
    @Test
    fun testChatListItemIsVisible() {
        // Create mock database instance
        val database = MockDatabase()

        // Launch the chat fragment in a container
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            ChatFragment(database)
        }

        // Scroll to the first item in the chat list recycler view and verify if it's visible
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ChatAdapter.ChatViewHolder>(0))
            .check(matches(hasDescendant(withId(R.id.chatTitleTextView))))
    }
}
