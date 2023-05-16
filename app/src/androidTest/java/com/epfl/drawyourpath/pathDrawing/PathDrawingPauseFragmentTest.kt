package com.epfl.drawyourpath.pathDrawing

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
class PathDrawingPauseFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Test that clicking on the pause button show the PathDrawingFragment in non drawing state
     */
    @Test
    fun checkThatClickingOnPauseButtonShowMainFragmentInNonStateDrawing() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingContainerFragment(1L)
        }

        // wait that the countdown passed
        Thread.sleep(1005)
        // click on stop button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_pause_button))
            .perform(ViewActions.click())
        // check that the main fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_main_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the content of the main fragment
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_detail_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_resume_stop_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}
