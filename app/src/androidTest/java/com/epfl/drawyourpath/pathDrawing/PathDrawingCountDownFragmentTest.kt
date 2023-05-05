package com.epfl.drawyourpath.pathDrawing

import android.Manifest
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)

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
        t.close()
    }

    /**
     * Test that at the end of the countdown the main drawing activity is displayed
     */
    @Test
    fun testThatTheTransitionIsMadeAfterCountDown() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        intent.putExtra(PathDrawingActivity.EXTRA_COUNTDOWN_DURATION, 1L)
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)

        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_countdown_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(1002)
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_main_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        t.close()
    }
}