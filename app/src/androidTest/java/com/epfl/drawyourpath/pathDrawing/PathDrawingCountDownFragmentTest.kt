package com.epfl.drawyourpath.pathDrawing

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PathDrawingCountDownFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Check that the countdown is displayed correctly (3,2,1, Go !)
     */
    @Test
    fun testThatTheCountdownIsDisplayedCorrectly() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingContainerFragment()
        }

        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_countdown_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("3")))
        // wait one seconds
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("2")))
        // wait one seconds
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("1")))
        // wait one seconds
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("GO !")))
        scenario.close()
    }

    /**
     * Test that at the end of the countdown the main drawing activity is displayed
     */
    @Test
    fun testThatTheTransitionIsMadeAfterCountDown() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingContainerFragment(1L)
        }

        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_countdown_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(1005)
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_main_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}
