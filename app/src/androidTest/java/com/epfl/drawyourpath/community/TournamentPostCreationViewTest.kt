package com.epfl.drawyourpath.community

import android.Manifest
import android.content.Intent
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.mainpage.MainActivity
import org.hamcrest.Matchers.anything
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TournamentPostCreationViewTest {
    private val runs = MockDatabase.mockUser.runs!!.sortedByDescending { it.getStartTime() }

    @get:Rule
    val permissionLocation: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val executorRule = CountingTaskExecutorRule()

    /**
     * wait for all thread to be done or throw a timeout error
     */
    private fun waitUntilAllThreadAreDone() {
        executorRule.drainTasks(2, TimeUnit.SECONDS)
        Thread.sleep(10)
    }

    @Test
    fun selectTournamentDisplayCorrectTournament() {
        // TODO change this when everything is linked
        val scenario = launchFragmentInContainer {
            TournamentPostCreationView()
        }
        // check default is first tournament in list
        onView(withId(R.id.post_creation_tournament_spinner)).check(matches(withSpinnerText(TournamentPostCreationView.TOURNAMENT_SAMPLE[0])))
        // click on spinner
        onView(withId(R.id.post_creation_tournament_spinner)).perform(click())
        // select third option
        onData(anything()).atPosition(2).perform(click())
        // check that third tournament is selected
        onView(withId(R.id.post_creation_tournament_spinner)).check(matches(withSpinnerText(TournamentPostCreationView.TOURNAMENT_SAMPLE[2])))

        scenario.close()
    }

    @Test
    fun selectRunDisplayCorrectRun() {
        val scenario = launchFragmentInContainer {
            TournamentPostCreationView()
        }

        // wait for the runs to load
        waitUntilAllThreadAreDone()
        // check default is first run in list
        onView(withId(R.id.post_creation_run_spinner)).check(matches(withSpinnerText(runs[0].toString())))
        // click on spinner
        onView(withId(R.id.post_creation_run_spinner)).perform(click())
        // select second option
        onData(anything()).atPosition(1).perform(click())
        // check that second run is selected
        onView(withId(R.id.post_creation_run_spinner)).check(matches(withSpinnerText(runs[1].toString())))

        scenario.close()
    }

    @Test
    fun backButtonGoesBackToCommunityView() {
        val scenario = ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
        // navigate to post creation
        goToPostCreation()
        // click on back button
        onView(withId(R.id.post_creation_back_button)).perform(click())
        // check that community fragment is displayed
        onView(withId(R.id.community_add_post_button)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun postButtonGoesBackToTournamentView() {
        val scenario = ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
        // navigate to post creation
        goToPostCreation()
        // click on post button
        onView(withId(R.id.post_creation_post_button)).perform(click())
        // check that community fragment is displayed
        onView(withId(R.id.community_add_post_button)).check(matches(isDisplayed()))

        scenario.close()
    }

    private fun goToPostCreation() {
        // go to community fragment
        onView(withId(R.id.community_menu_item)).perform(click())
        // go to post creation
        onView(withId(R.id.community_add_post_button)).perform(click())
    }

    @Before
    fun clearDatabase() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm clear com.epfl.drawyourpath.package").close()
    }
}
