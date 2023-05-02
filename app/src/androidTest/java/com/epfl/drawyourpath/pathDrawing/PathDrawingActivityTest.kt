package com.epfl.drawyourpath.pathDrawing

import android.Manifest
import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.DrawMenuFragment
import com.epfl.drawyourpath.userProfileCreation.UserProfileCreationActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PathDrawingActivityTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun checkThatTheCountdownFragmentIsDisplayedAtTheCreation(){
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)

        //test that the countdown fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.path_countdown_fragment)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed(),
            ),
        )
        t.close()
    }

    @Test
    fun testThatTheTransitionIsMadeAfterCountDown(){
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)

        Espresso.onView(ViewMatchers.withId(R.id.path_countdown_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(4000)
        Espresso.onView(ViewMatchers.withId(R.id.path_draw_main_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        t.close()
    }
}