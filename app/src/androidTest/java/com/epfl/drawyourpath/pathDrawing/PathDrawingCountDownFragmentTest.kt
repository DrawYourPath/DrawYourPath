package com.epfl.drawyourpath.pathDrawing

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.fragments.DrawMenuFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PathDrawingCountDownFragmentTest {

    @Test
    fun testThatTheCountdownIsDisplayedCorrectly(){
        val scenario = launchFragmentInContainer<PathDrawingCountDownFragment>()

        Espresso.onView(ViewMatchers.withId(R.id.path_countdown_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("3")))
        //wait one seconds
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("2")))
        //wait one seconds
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("1")))
        //wait one seconds
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.countdown_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("GO")))
        scenario.close()
    }
}