package com.epfl.drawyourpath.mainpage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.MockDataBase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class MainActivityTest {
    @get:Rule
    var testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun firstFragmentIsDrawFragmentWhenNoSavedState() {
        // First fragment is DrawFragment
        onView(withId(R.id.fragment_draw)).check(matches(isDisplayed()))
    }

    @Test
    fun fragmentsSwitchCorrectlyWhenBottomMenuItemsAreClicked() {
        // Go to community
        onView(withId(R.id.community_menu_item)).perform(click())

        // Check fragment is community
        onView(withId(R.id.fragment_community)).check(matches(isDisplayed()))

        // Go to friends
        onView(withId(R.id.friends_menu_item)).perform(click())

        // Check fragment is friends
        onView(withId(R.id.fragment_friends)).check(matches(isDisplayed()))

        // Go to draw
        onView(withId(R.id.draw_menu_item)).perform(click())

        // Check fragment is draw
        onView(withId(R.id.fragment_draw)).check(matches(isDisplayed()))

        // Go to history
        onView(withId(R.id.history_menu_item)).perform(click())

        // Check fragment is history
        onView(withId(R.id.fragment_history)).check(matches(isDisplayed()))

        // Go to settings

        onView(withId(R.id.preferences_menu_item)).perform(click())

        // Check fragment is settings
        // see https://stackoverflow.com/questions/45172505/testing-android-preferencefragment-with-espresso
        onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()))

    }

    @Test
    fun usernameAndEmailAreCorrectInDrawerMenu() {
        // go to drawer menu
        onView(withId(R.id.profile_button)).perform(click())

        // check username and email are correct
        onView(withId(R.id.header_username)).check(matches(withText(MockDataBase().usernameTest)))
        onView(withId(R.id.header_email)).check(matches(withText(MockDataBase().userAuthTest.getEmail())))
    }

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