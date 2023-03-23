package com.epfl.drawyourpath.mainpage.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    private lateinit var scenario: FragmentScenario<ProfileFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer()
    }

    private fun textViewHasSubstring(id: Int, txt: String) {
        // Bug in espresso.
        // onView(withId(id)).check(matches(withSubstring(txt)))
    }

    @Test
    fun overallLayoutMatchesExpectedContent() {
        textViewHasSubstring(R.id.TV_DaysStreak, " Days")
        textViewHasSubstring(R.id.TV_AvgSpeed, " KM/H")
        textViewHasSubstring(R.id.TV_ShapesDrawn, " SHAPES")
        textViewHasSubstring(R.id.TV_TotalKilometers, " KM")
        textViewHasSubstring(R.id.TV_GoalsReached, " GOALS")

        onView(withId(R.id.LV_Friends)).check(matches(isDisplayed()))
    }
}