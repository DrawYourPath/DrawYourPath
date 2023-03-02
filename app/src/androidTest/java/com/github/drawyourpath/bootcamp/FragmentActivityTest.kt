package com.github.drawyourpath.bootcamp

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FragmentActivityTest {
    @get:Rule
    var testRule = ActivityScenarioRule(FragmentActivity::class.java)

    @Test
    fun fragmentsSwitchCorrectlyWhenMenuItemsAreClicked() {
        //First fragment is main
        onView(withId(R.id.mainTextView)).check(matches(isDisplayed()))

        //Go to profile
        onView(withContentDescription("Navigate up")).perform(click()) //Press the hamburger button
        onView(withId(R.id.activity_main_drawer_profile)).perform(click())

        //Second fragment is profile
        onView(withId(R.id.profileTextView)).check(matches(isDisplayed()))

        //Go to settings
        onView(withContentDescription("Navigate up")).perform(click()) //Press the hamburger button
        onView(withId(R.id.activity_main_drawer_settings)).perform(click())

        //Third fragment is settings
        onView(withId(R.id.settingsTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun fragmentsDisplayCorrectText() {
        //Main fragment
        launchFragmentInContainer<MainFragment>()
        onView(withId(R.id.mainTextView)).check(matches(withText("Main Fragment")))

        //Profile fragment
        launchFragmentInContainer<ProfileFragment>()
        onView(withId(R.id.profileTextView)).check(matches(withText("Profile Fragment")))

        //Settings fragment
        launchFragmentInContainer<SettingsFragment>()
        onView(withId(R.id.settingsTextView)).check(matches(withText("Settings Fragment")))
    }


}