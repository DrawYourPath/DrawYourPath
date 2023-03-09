package com.github.drawyourpath.bootcamp.mainpage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.drawyourpath.bootcamp.R
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
        onView(withId(R.id.settings_menu_item)).perform(click())

        // Check fragment is settings
        onView(withId(R.id.fragment_settings)).check(matches(isDisplayed()))
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
    }


}