package com.epfl.drawyourpath.pathDrawing

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PathDrawingCountDownFragmentTest {

    @Test
    fun testThatTheCountdownIsDisplayedCorrectly() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PathDrawingActivity::class.java,
        )
        val t: ActivityScenario<PathDrawingActivity> = ActivityScenario.launch(intent)

        Espresso.onView(ViewMatchers.withId(R.id.path_countdown_fragment))
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
            .check(ViewAssertions.matches(ViewMatchers.withText("GO")))
        t.close()
    }
}
