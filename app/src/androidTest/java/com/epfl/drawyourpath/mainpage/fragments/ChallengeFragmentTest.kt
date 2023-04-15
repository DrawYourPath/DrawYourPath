package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.TemporaryUser
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.challenge.Trophy
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@RunWith(AndroidJUnit4::class)
class ChallengeFragmentTest {

    private var userOnlyGoal =
        TemporaryUser(LinkedList(mutableListOf(DailyGoal(5.0, 15.0, 1, 1.56, 12.0, 1))))


    /**
     * test if the distance goal is correctly displayed
     */
    @Test
    fun displayCorrectDistanceGoal() {
        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoal)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that distance is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("1.56/5.0"))))
            .check(matches(hasDescendant(withText("5"))))
            .check(matches(hasDescendant(withText("kilometers"))))

        scenario.close()
    }

    /**
     * test if the time goal is correctly displayed
     */
    @Test
    fun displayCorrectTimeGoal() {
        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoal)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that time is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("12.0/15.0"))))
            .check(matches(hasDescendant(withText("15"))))
            .check(matches(hasDescendant(withText("minutes"))))

        scenario.close()
    }

    /**
     * test if the nb of paths is correctly displayed
     */
    @Test
    fun displayCorrectNbPathsGoal() {
        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoal)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that nb of paths is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("1.0/1.0"))))
            .check(matches(hasDescendant(withText("1"))))
            .check(matches(hasDescendant(withText("paths"))))

        scenario.close()
    }

    /**
     * test if the default distance goal is correctly displayed when having no previous goal
     */
    @Test
    fun displayCorrectDefaultDistanceGoal() {
        val bundle = Bundle()
        bundle.putSerializable("user", TemporaryUser())

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that distance is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("0.0/${TemporaryUser.DEFAULT_DAILY_GOAL.distanceInKilometerGoal}"))))
            .check(
                matches(
                    hasDescendant(
                        withText(
                            TemporaryUser.DEFAULT_DAILY_GOAL.distanceInKilometerGoal.toInt()
                                .toString()
                        )
                    )
                )
            )
            .check(matches(hasDescendant(withText("kilometers"))))

        scenario.close()
    }

    /**
     * test if the default time goal is correctly displayed when having no previous goal
     */
    @Test
    fun displayCorrectDefaultTimeGoal() {
        val bundle = Bundle()
        bundle.putSerializable("user", TemporaryUser())

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that time is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("0.0/${TemporaryUser.DEFAULT_DAILY_GOAL.timeInMinutesGoal}"))))
            .check(
                matches(
                    hasDescendant(
                        withText(
                            TemporaryUser.DEFAULT_DAILY_GOAL.timeInMinutesGoal.toInt().toString()
                        )
                    )
                )
            )
            .check(matches(hasDescendant(withText("minutes"))))

        scenario.close()
    }

    /**
     * test if the default nb of paths goal is correctly displayed when having no previous goal
     */
    @Test
    fun displayCorrectDefaultPathsGoal() {
        val bundle = Bundle()
        bundle.putSerializable("user", TemporaryUser())

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that nb of paths is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("0.0/${TemporaryUser.DEFAULT_DAILY_GOAL.nbOfPathsGoal.toDouble()}"))))
            .check(matches(hasDescendant(withText(TemporaryUser.DEFAULT_DAILY_GOAL.nbOfPathsGoal.toString()))))
            .check(matches(hasDescendant(withText("paths"))))

        scenario.close()
    }

    /**
     * test if modifying the distance goal display the correct modified goal
     */
    @Test
    fun modifyDistanceGoalCorrectly() {
        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoal)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //change value of distance
        onView(withId(R.id.goals_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, replaceTextOnViewChild("20", R.id.goal_display_edit_text)
                )
            )

        closeSoftKeyboard()

        //check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("1.56/20.0"))))
            .check(matches(hasDescendant(withText("20"))))
            .check(matches(hasDescendant(withText("kilometers"))))

        scenario.close()
    }

    /**
     * test if modifying the time goal display the correct modified goal
     */
    @Test
    fun modifyTimeGoalCorrectly() {
        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoal)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //change value of time
        onView(withId(R.id.goals_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1, replaceTextOnViewChild("60", R.id.goal_display_edit_text)
                )
            )

        closeSoftKeyboard()

        //check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("12.0/60.0"))))
            .check(matches(hasDescendant(withText("60"))))
            .check(matches(hasDescendant(withText("minutes"))))

        scenario.close()
    }

    /**
     * test if modifying the nb of paths goal display the correct modified goal
     */
    @Test
    fun modifyPathGoalCorrectly() {
        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoal)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //change value of path
        onView(withId(R.id.goals_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    2, replaceTextOnViewChild("2", R.id.goal_display_edit_text)
                )
            )

        closeSoftKeyboard()

        //check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("1.0/2.0"))))
            .check(matches(hasDescendant(withText("2"))))
            .check(matches(hasDescendant(withText("paths"))))

        scenario.close()
    }

    private val userOnlyGoalYesterday = TemporaryUser(
        LinkedList(
            mutableListOf(
                DailyGoal(5.0, 15.0, 10, 1.56, 12.0, 9, LocalDate.now().minusDays(1L))
            )
        )
    )

    /**
     * test if the new distance goal for today is the same as yesterday but with a zero progress
     */
    @Test
    fun createNewDailyDistanceGoalForTodayUsingYesterdayDailyDistanceGoal() {

        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoalYesterday)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that distance is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("0.0/5.0"))))
            .check(matches(hasDescendant(withText("5"))))
            .check(matches(hasDescendant(withText("kilometers"))))

        scenario.close()
    }

    /**
     * test if the new time goal for today is the same as yesterday but with a zero progress
     */
    @Test
    fun createNewDailyTimeGoalForTodayUsingYesterdayDailyTimeGoal() {

        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoalYesterday)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that time is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("0.0/15.0"))))
            .check(matches(hasDescendant(withText("15"))))
            .check(matches(hasDescendant(withText("minutes"))))
        //check that nb of paths is correct

        scenario.close()
    }

    /**
     * test if the new nb of paths goal for today is the same as yesterday but with a zero progress
     */
    @Test
    fun createNewDailyPathsGoalForTodayUsingYesterdayDailyPathsGoal() {

        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyGoalYesterday)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that nb of paths is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("0.0/10.0"))))
            .check(matches(hasDescendant(withText("10"))))
            .check(matches(hasDescendant(withText("paths"))))

        scenario.close()
    }

    /**
     * test if the tournaments are correctly displayed
     */
    @Test
    fun displayCorrectTournaments() {
        val endDate3 = LocalDateTime.now().minusDays(2L)
        val t1 = Tournament(
            "test",
            "test",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L)
        )

        val t2 = Tournament(
            "2nd test",
            "test",
            LocalDateTime.now().minusDays(1L),
            LocalDateTime.now().plusDays(1L)
        )

        val t3 = Tournament(
            "test3",
            "test3",
            LocalDateTime.now().minusDays(3L),
            endDate3
        )

        val t4 = Tournament(
            "4th test",
            "test",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(3L)
        )

        val userOnlyTournament = TemporaryUser(tournaments = listOf(t1, t2, t3, t4))

        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyTournament)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that first tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("Start in 2 days 23 hours 59 minutes"))))
        //check that second tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("2nd test"))))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("End in 23 hours 59 minutes"))))
        //check that third tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("test3"))))
            .check(matches(hasDescendant(withText("test3"))))
            .check(matches(hasDescendant(withText("Ended the ${endDate3.dayOfMonth} of ${endDate3.month} ${endDate3.year}"))))
        //check that fourth tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(3))
            .check(matches(hasDescendant(withText("4th test"))))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("End in 2 days 23 hours 59 minutes"))))

        scenario.close()

    }

    /**
     * test if the trophies are correctly displayed
     */
    @Test
    fun displayCorrectTrophies() {
        val trophies: EnumMap<Trophy, LocalDate> = EnumMap(Trophy::class.java)
        trophies[Trophy.THEFIRSTKM] = LocalDate.of(2022, 10, 19)
        trophies[Trophy.TENKM] = LocalDate.of(2023, 1, 1)
        trophies[Trophy.MARATHON] = LocalDate.of(2023, 2, 10)
        trophies[Trophy.THEFIRSTHOUR] = LocalDate.of(2022, 8, 13)
        trophies[Trophy.THEFIRSTPATH] = LocalDate.of(2022, 5, 1)

        val userOnlyTrophies = TemporaryUser(trophies = trophies)

        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyTrophies)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp
        )

        //check that trophy THEFIRSTKM is correct
        onView(withId(R.id.trophies_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(Trophy.THEFIRSTKM.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2022, 10, 19)}"))))
        //check that trophy TENKM is correct
        onView(withId(R.id.trophies_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText(Trophy.TENKM.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2023, 1, 1)}"))))
        //check that trophy MARATHON is correct
        onView(withId(R.id.trophies_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText(Trophy.MARATHON.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2023, 2, 10)}"))))
        //check that trophy THEFIRSTHOUR is correct
        onView(withId(R.id.trophies_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(3))
            .check(matches(hasDescendant(withText(Trophy.THEFIRSTHOUR.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2022, 8, 13)}"))))
        //check that trophy THEFIRSTPATH is correct
        onView(withId(R.id.trophies_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
            .check(matches(hasDescendant(withText(Trophy.THEFIRSTPATH.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2022, 5, 1)}"))))

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

        override fun perform(uiController: UiController, view: View) =
            replaceText(value).perform(uiController, view.findViewById(viewId))
    }

}