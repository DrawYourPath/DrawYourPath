package com.epfl.drawyourpath.community

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions.setDate
import androidx.test.espresso.contrib.PickerActions.setTime
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class TournamentCreationFragmentTest {
    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun launchFragmentFromMainActivity(): ActivityScenario<MainActivity> {
        Intents.init()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

        val scenario: ActivityScenario<MainActivity> = launch(intent)
        onView(withId(R.id.community_menu_item)).perform(click())
        onView(withId(R.id.community_menu_button)).perform(click())
        onView(withText(R.string.create_new_tournament)).perform(click())

        return scenario
    }

    private fun launchFragment(): FragmentScenario<TournamentCreationFragment> {
        return FragmentScenario.launchInContainer(
            TournamentCreationFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )
    }

    @Test
    fun createEmptyTournamentShowError() {
        val scenario = launchFragment()

        pressCreate()

        onView(withId(R.id.tournament_creation_title_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_title_error)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tournament_creation_description_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_description_error)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tournament_creation_visibility_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_visibility_error)))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun createTournamentWithPastStartDateAndTimeShowError() {
        val scenario = launchFragment()

        selectStartDate(LocalDate.now().minusDays(1L))

        pressCreate()

        onView(withId(R.id.tournament_creation_start_date_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_start_date_error)))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun createTournamentWithStartTimeLessThanIntervalShowError() {
        val scenario = launchFragment()

        val start = LocalDateTime.now().plus(TournamentCreationFragment.MIN_START_TIME_INTERVAL)
            .minusMinutes(2L)

        selectStartDate(start.toLocalDate())
        selectStartTime(start.toLocalTime())

        pressCreate()

        onView(withId(R.id.tournament_creation_start_date_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_start_date_error)))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun createTournamentWithEndDateBeforeStartDateShowError() {
        val scenario = launchFragment()

        selectStartDate(LocalDate.now().plusDays(5L))

        selectEndDate(LocalDate.now().plusDays(4L))

        pressCreate()

        onView(withId(R.id.tournament_creation_end_date_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_end_date_error)))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun createTournamentWithEndDateLessThanIntervalFromStartDateShowError() {
        val scenario = launchFragment()

        val startDate = LocalDateTime.now().plusDays(5L)

        val endDate =
            startDate.plus(TournamentCreationFragment.MIN_END_TIME_INTERVAL).minusMinutes(2L)

        selectStartDate(startDate.toLocalDate())
        selectStartTime(startDate.toLocalTime())

        selectEndDate(endDate.toLocalDate())
        selectEndTime(endDate.toLocalTime())

        pressCreate()

        onView(withId(R.id.tournament_creation_end_date_error))
            .perform(scrollTo())
            .check(matches(withText(R.string.tournament_creation_end_date_error)))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun backButtonGoesToCommunity() {
        val scenario = launchFragmentFromMainActivity()

        onView(withId(R.id.tournament_creation_back_button)).perform(click())

        onView(withId(R.id.fragment_community)).check(matches(isDisplayed()))

        scenario.close()
        Intents.release()
    }

    private fun typeTitle(title: String) {
        onView(withId(R.id.tournament_creation_title)).perform(scrollTo(), replaceText(title))
        closeSoftKeyboard()
    }

    private fun typeDescription(description: String) {
        onView(withId(R.id.tournament_creation_description)).perform(
            scrollTo(),
            replaceText(description),
        )
        closeSoftKeyboard()
    }

    private fun selectStartDate(date: LocalDate) {
        onView(withId(R.id.tournament_creation_start_date)).perform(scrollTo(), click())
        onView(isAssignableFrom(DatePicker::class.java)).perform(
            setDate(
                date.year,
                date.monthValue,
                date.dayOfMonth,
            ),
        )
        onView(withId(android.R.id.button1)).perform(click())
    }

    private fun selectStartTime(time: LocalTime) {
        onView(withId(R.id.tournament_creation_start_time)).perform(scrollTo(), click())
        onView(isAssignableFrom(TimePicker::class.java)).perform(setTime(time.hour, time.minute))
        onView(withId(android.R.id.button1)).perform(click())
    }

    private fun selectEndDate(date: LocalDate) {
        onView(withId(R.id.tournament_creation_end_date)).perform(scrollTo(), click())
        onView(isAssignableFrom(DatePicker::class.java)).perform(
            setDate(
                date.year,
                date.monthValue,
                date.dayOfMonth,
            ),
        )
        onView(withId(android.R.id.button1)).perform(click())
    }

    private fun selectEndTime(time: LocalTime) {
        onView(withId(R.id.tournament_creation_end_time)).perform(scrollTo(), click())
        onView(isAssignableFrom(TimePicker::class.java)).perform(setTime(time.hour, time.minute))
        onView(withId(android.R.id.button1)).perform(click())
    }

    private fun selectVisibility(visibility: Tournament.Visibility) {
        when (visibility.ordinal) {
            0 -> onView(withId(R.id.tournament_creation_visibility_item_0)).perform(
                scrollTo(),
                click(),
            )

            1 -> onView(withId(R.id.tournament_creation_visibility_item_1)).perform(
                scrollTo(),
                click(),
            )
        }
    }

    private fun pressCreate() {
        onView(withId(R.id.tournament_creation_create_button)).perform(scrollTo(), click())
    }

    private fun selectCorrectOptionsAndCreate() {
        val title = "Discover the earth"
        val description = "draw the earth"
        val startDate = LocalDateTime.now().plusDays(5L)
        val endDate = startDate.plusWeeks(1L)
        val visibility = Tournament.Visibility.FRIENDS_ONLY

        typeTitle(title)

        typeDescription(description)

        selectStartDate(startDate.toLocalDate())
        selectStartTime(startDate.toLocalTime())

        selectEndDate(endDate.toLocalDate())
        selectEndTime(endDate.toLocalTime())

        selectVisibility(visibility)

        return pressCreate()
    }
}
