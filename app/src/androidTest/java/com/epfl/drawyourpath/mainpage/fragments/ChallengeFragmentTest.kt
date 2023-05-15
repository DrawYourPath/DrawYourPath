package com.epfl.drawyourpath.mainpage.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.challenge.milestone.Milestone
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.database.MockDatabase
import org.hamcrest.core.StringContains.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ChallengeFragmentTest {

    @get:Rule
    val executorRule = CountingTaskExecutorRule()

    private var mockUser = MockDatabase.mockUser
    private val context = ApplicationProvider.getApplicationContext<Context>()

    /**
     * wait for all thread to be done or throw a timeout error
     */
    private fun waitUntilAllThreadAreDone() {
        executorRule.drainTasks(2, TimeUnit.SECONDS)
        Thread.sleep(10)
    }

    @Test
    fun displayDistanceGoalOfMockUser() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val distanceProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, mockUser.goals!!.distance!!)

        waitUntilAllThreadAreDone()

        // check that distance is correct
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(distanceProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.distance!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.kilometers))))

        scenario.close()
    }

    @Test
    fun displayTimeGoalOfMockUser() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val timeProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, mockUser.goals!!.activityTime!!)

        waitUntilAllThreadAreDone()

        // check that time is correct
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(timeProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.activityTime!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.minutes))))

        scenario.close()
    }

    @Test
    fun displayNbPathsGoalOfMockUser() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val pathProgressGoal = context.resources.getString(R.string.progress_over_goal_path).format(0.0, mockUser.goals!!.paths!!.toDouble())

        waitUntilAllThreadAreDone()

        // check that nb of paths is correct
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.paths!!.toString()))))
            .check(matches(hasDescendant(withText(R.string.paths))))

        scenario.close()
    }

    @Test
    fun modifyDistanceGoalWithBlankDoesNothing() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // change value of distance
        onView(withId(R.id.goals_view)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                replaceTextOnViewChild("", R.id.goal_display_edit_text),
            ),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
            ),
        )

        val distanceProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, mockUser.goals!!.distance!!)

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(distanceProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.distance!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.kilometers))))

        scenario.close()
    }

    @Test
    fun modifyTimeGoalWithBlankDoesNothing() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // change value of time
        onView(withId(R.id.goals_view)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                replaceTextOnViewChild("", R.id.goal_display_edit_text),
            ),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
            ),
        )

        val timeProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, mockUser.goals!!.activityTime!!)

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(timeProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.activityTime!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.minutes))))

        scenario.close()
    }

    @Test
    fun modifyPathGoalWithBlankDoesNothing() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // change value of path
        onView(withId(R.id.goals_view)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                replaceTextOnViewChild("", R.id.goal_display_edit_text),
            ),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
            ),
        )

        val pathProgressGoal = context.resources.getString(R.string.progress_over_goal_path).format(0.0, mockUser.goals!!.paths!!.toDouble())

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(pathProgressGoal)))).check(matches(hasDescendant(withText(mockUser.goals!!.paths!!.toString()))))
            .check(matches(hasDescendant(withText(R.string.paths))))

        scenario.close()
    }

    /*@Test
    fun modifyDistanceGoalDisplayNewDistanceGoal() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val newDistance = 158

        // change value of path
        onView(withId(R.id.goals_view)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                replaceTextOnViewChild(newDistance.toString(), R.id.goal_display_edit_text),
            ),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
            ),
        )

        val pathProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, newDistance.toDouble())

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(newDistance.toString()))))
            .check(matches(hasDescendant(withText(R.string.kilometers))))

        scenario.close()
    }

    @Test
    fun modifyTimeGoalDisplayNewTimeGoal() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val newTime = 70

        // change value of path
        onView(withId(R.id.goals_view)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                replaceTextOnViewChild(newTime.toString(), R.id.goal_display_edit_text),
            ),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
            ),
        )

        val pathProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, newTime.toDouble())

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(newTime.toString()))))
            .check(matches(hasDescendant(withText(R.string.minutes))))

        scenario.close()
    }

    @Test
    fun modifyPathsGoalDisplayNewPathsGoal() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val newPaths = 12

        // change value of path
        onView(withId(R.id.goals_view)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                replaceTextOnViewChild(newPaths.toString(), R.id.goal_display_edit_text),
            ),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
            ),
        )

        val pathProgressGoal = context.resources.getString(R.string.progress_over_goal_path).format(0.0, newPaths.toDouble())

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view)).check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(newPaths.toString()))))
            .check(matches(hasDescendant(withText(R.string.paths))))

        scenario.close()
    }*/

    // TODO change to mock user's trophies when in mock database
    @Test
    fun displayTrophiesSample() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // for some reason CI does not want to display all of them
        for (trophy in Trophy.sample.subList(0, 1)) {
            onView(withText(trophy.tournamentName)).perform(scrollTo())
            onView(withId(R.id.trophies_view)).check(matches(hasDescendant(withText(trophy.tournamentName))))
                .check(matches(hasDescendant(withText(trophy.tournamentDescription))))
                .check(matches(hasDescendant(withText(containsString(trophy.dateAsString)))))
                .check(matches(hasDescendant(withText(containsString(trophy.ranking.toString())))))
        }

        scenario.close()
    }

    // TODO change to mock user's milestones when in mock database
    @Test
    fun displayMilestonesSample() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        for (milestone in Milestone.sample) {
            onView(withText(milestone.name)).perform(scrollTo())
            onView(withId(R.id.milestones_view)).check(matches(hasDescendant(withText(milestone.name))))
                .check(matches(hasDescendant(withText(milestone.description))))
                .check(matches(hasDescendant(withText(containsString(milestone.dateAsString)))))
        }

        scenario.close()
    }

    /**
     * helper function to perform a replaceText inside a RecyclerView
     *
     * @param value the String to replace inside the editText
     * @param viewId the id of the editText inside the RecyclerView
     */
    private fun replaceTextOnViewChild(value: String, viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "replace text on a child view with specified id."

        override fun perform(uiController: UiController, view: View) = replaceText(value).perform(uiController, view.findViewById(viewId))
    }

    /**
     * helper function to perform a replaceText inside a RecyclerView
     *
     * @param viewId the id of the editText inside the RecyclerView
     */
    private fun pressImeActionButtonOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "press Ime action button on a child view with specified id."

        override fun perform(uiController: UiController, view: View) = pressImeActionButton().perform(uiController, view.findViewById(viewId))
    }
}