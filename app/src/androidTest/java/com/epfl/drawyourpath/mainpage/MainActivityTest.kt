package com.epfl.drawyourpath.mainpage

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainActivityTest {
    @get:Rule
    var testRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun firstFragmentIsDrawFragmentWhenNoSavedState() {
        // First fragment is DrawFragment
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun fragmentsSwitchCorrectlyWhenBottomMenuItemsAreClicked() {
        // Go to community
        onView(withId(R.id.community_menu_item)).perform(click())

        // Check fragment is community
        onView(withId(R.id.fragment_community)).check(matches(isDisplayed()))

        // TODO: Friends fragment redirect to login screen when not logged.
        //       Find a way to test it.
        // Go to friends
        // onView(withId(R.id.friends_menu_item)).perform(click())

        // Check fragment is friends
        // onView(withId(R.id.fragment_friends)).check(matches(isDisplayed()))

        // Go to draw
        onView(withId(R.id.draw_menu_item)).perform(click())

        // Check fragment is draw
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        // Go to history
        onView(withId(R.id.history_menu_item)).perform(click())

        // Check fragment is history
        onView(withId(R.id.fragment_history)).check(matches(isDisplayed()))

        // Go to chats

        onView(withId(R.id.chat_menu_item)).perform(click())

        // Check fragment is chats
        onView(withId(R.id.chat_list_fragment)).check(matches(isDisplayed()))
    }

    // TODO: uncomment this method when refactor the cache
    /*
    @Test
    fun usernameAndEmailAreCorrectInDrawerMenu() {
        val database = MockDatabase()

        // go to drawer menu
        onView(withId(R.id.profile_button)).perform(click())

        // check if username and email are correct
        onView(withId(R.id.header_username)).check(matches(withText(MockDatabase.mockUser.username)))
        onView(withId(R.id.header_email)).check(matches(withText(MockDatabase.mockUser.email)))
    }*/

    @Test
    fun fragmentsSwitchCorrectlyWhenDrawerMenuItemsAreClicked() {
        // Go to profile
        onView(withId(R.id.profile_button)).perform(click())
        onView(withId(R.id.profile_menu_item)).perform(click())

        // Check fragment is profile
        onView(withId(R.id.fragment_profile)).check(matches(isDisplayed()))

        // Go to stats
        onView(withId(R.id.profile_button)).perform(click())
        onView(withId(R.id.stats_menu_item)).perform(click())

        // Check fragment is stats
        onView(withId(R.id.fragment_stats)).check(matches(isDisplayed()))

        // Go to challenge
        onView(withId(R.id.profile_button)).perform(click())
        onView(withId(R.id.challenge_menu_item)).perform(click())

        // Check fragment is challenge
        onView(withId(R.id.fragment_challenge)).check(matches(isDisplayed()))
    }
}
