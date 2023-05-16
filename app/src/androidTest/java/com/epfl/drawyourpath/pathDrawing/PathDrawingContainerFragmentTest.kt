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
class PathDrawingContainerFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Test that the countdown fragment is lunch on the activity lunch
     */
    @Test
    fun checkThatTheCountdownFragmentIsDisplayedAtTheCreation() {
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingContainerFragment()
        }

        // test that the countdown fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_countdown_fragment)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed(),
            ),
        )
        scenario.close()
    }
}
