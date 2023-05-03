package com.epfl.drawyourpath.pathDrawing

import android.Manifest
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
class PathDrawingResumeStopFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Test that clicking on the resume button show the PathDrawingFragment in drawing state
     */
    @Test
    fun checkThatClickingOnResumeButtonShowMainFragmentInStateDrawing() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)
        //wait that the countdown passed
        Thread.sleep(4100)
        //click on stop button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_pause_button))
            .perform(ViewActions.click())
        //click on the resume button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_resume_button))
            .perform(ViewActions.click())
        //check that the main fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_main_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //check the content of the main fragment
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_current_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_pause_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        t.close()
    }

    /**
     * Test that clicking on the stop button show the PathDrawingEndFragment
     */
    @Test
    fun checkThatClickingOnStopButtonShowEndDrawingFragment() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)
        //wait that the countdown passed
        Thread.sleep(4100)
        //click on pause button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_pause_button))
            .perform(ViewActions.click())
        //click on the resume button
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_stop_button))
            .perform(ViewActions.click())
        //check that the main fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_end_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //check the content of the main fragment
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_detail_performance_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.path_drawing_end_back_menu_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        t.close()
    }
}