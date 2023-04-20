package com.epfl.drawyourpath.mainpage.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
        onView(withId(id)).check(matches(withSubstring(txt)))
    }

    @Test
    fun overallLayoutMatchesExpectedContent() {
        textViewHasSubstring(R.id.TV_DaysStreak, " Days")
        textViewHasSubstring(R.id.TV_AvgSpeed, " KM/H")
        textViewHasSubstring(R.id.TV_ShapesDrawn, " Shapes")
        textViewHasSubstring(R.id.TV_TotalKilometers, " KM")
        textViewHasSubstring(R.id.TV_GoalsReached, " Goals")

        onView(withId(R.id.IV_QRCode)).check(matches(isDisplayed()))

        onView(withId(R.id.LV_Friends)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnTrophyOpensTrophyModal() {
        onView(withId(R.id.TV_Trophy)).perform(click())

        // Accessing the close button ensures the modal is currently showing.
        onView(withId(R.id.BT_Close)).perform(click())
    }

    // TODO: Add tests when the user profile can be fetched to ensure it
    //       matches incomming data.
}