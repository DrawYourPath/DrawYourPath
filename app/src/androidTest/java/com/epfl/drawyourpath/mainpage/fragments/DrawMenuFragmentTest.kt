package com.epfl.drawyourpath.mainpage.fragments

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DrawMenuFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun checkThatTheMapIsDisplayed() {
        val scenario = launchFragmentInContainer<DrawMenuFragment>()

        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }

    @Test
    fun checkThatClickingOnTheStartDrawButtonShowCountDown() {
        val scenario = launchFragmentInContainer<DrawMenuFragment>()

        Espresso.onView(ViewMatchers.withId(R.id.button_start_drawing))
            .perform(ViewActions.click())

        // check that the draw path activity was lunched
        Espresso.onView(ViewMatchers.withId(R.id.contentFragmentPathDrawing))
            .check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
        // check that the countdown is currently displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_countdown_fragment))
            .check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
        scenario.close()
    }
}
