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

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun chatFragmentIsDisplayed() {
        val database = MockDatabase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            ChatFragment(database)
        }

        onView(withId(R.id.chat_list_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun chatListIsDisplayed() {
        val database = MockDatabase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            ChatFragment(database)
        }

        onView(withId(R.id.chatListRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testChatListItemIsVisible() {
        val database = MockDatabase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            ChatFragment(database)
        }

        // Check if the first chat item in the list is visible
        onView(withId(R.id.chatListRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ChatAdapter.ChatViewHolder>(0))
            .check(matches(hasDescendant(withId(R.id.chatTitleTextView))))
    }





}

