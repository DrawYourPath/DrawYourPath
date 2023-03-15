package com.github.drawyourpath.bootcamp.mainpage.fragments

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
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.challenge.DailyGoal
import com.github.drawyourpath.bootcamp.challenge.TemporaryUser
import com.github.drawyourpath.bootcamp.challenge.Tournament
import com.github.drawyourpath.bootcamp.challenge.Trophy
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@RunWith(AndroidJUnit4::class)
class ChallengeFragmentTest {

    private var userOnlyGoal =
        TemporaryUser(LinkedList(mutableListOf(DailyGoal(5.0, 15.0, 1, 1.56, 12.0, 1))))


    @Test
    fun displayCorrectGoal() {
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
        //check that time is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("12.0/15.0"))))
            .check(matches(hasDescendant(withText("15"))))
            .check(matches(hasDescendant(withText("minutes"))))
        //check that nb of shapes is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("1.0/1.0"))))
            .check(matches(hasDescendant(withText("1"))))
            .check(matches(hasDescendant(withText("shapes"))))

        scenario.close()
    }

    @Test
    fun displayCorrectDefaultGoal() {
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
            .check(
                matches(
                    hasDescendant(
                        withText(
                            "0.0/${
                                TemporaryUser.DEFAULT_DAILY_GOAL.getGoalToDouble(
                                    0
                                )
                            }"
                        )
                    )
                )
            )
            .check(
                matches(
                    hasDescendant(
                        withText(
                            TemporaryUser.DEFAULT_DAILY_GOAL.getGoalToDouble(0).toInt().toString()
                        )
                    )
                )
            )
            .check(matches(hasDescendant(withText("kilometers"))))
        //check that time is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(
                matches(
                    hasDescendant(
                        withText(
                            "0.0/${
                                TemporaryUser.DEFAULT_DAILY_GOAL.getGoalToDouble(
                                    1
                                )
                            }"
                        )
                    )
                )
            )
            .check(
                matches(
                    hasDescendant(
                        withText(
                            TemporaryUser.DEFAULT_DAILY_GOAL.getGoalToDouble(1).toInt().toString()
                        )
                    )
                )
            )
            .check(matches(hasDescendant(withText("minutes"))))
        //check that nb of shapes is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(
                matches(
                    hasDescendant(
                        withText(
                            "0.0/${
                                TemporaryUser.DEFAULT_DAILY_GOAL.getGoalToDouble(
                                    2
                                )
                            }"
                        )
                    )
                )
            )
            .check(
                matches(
                    hasDescendant(
                        withText(
                            TemporaryUser.DEFAULT_DAILY_GOAL.getGoalToDouble(2).toInt().toString()
                        )
                    )
                )
            )
            .check(matches(hasDescendant(withText("shapes"))))

        scenario.close()
    }

    @Test
    fun modifyEachGoalCorrectly() {
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
                    0,
                    replaceTextOnViewChild("20", R.id.goal_display_edit_text)
                )
            )
        closeSoftKeyboard()
        //check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("1.56/20.0"))))
            .check(matches(hasDescendant(withText("20"))))
            .check(matches(hasDescendant(withText("kilometers"))))

        //change value of distance
        onView(withId(R.id.goals_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    replaceTextOnViewChild("60", R.id.goal_display_edit_text)
                )
            )
        closeSoftKeyboard()
        //check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("12.0/60.0"))))
            .check(matches(hasDescendant(withText("60"))))
            .check(matches(hasDescendant(withText("minutes"))))

        //change value of shape
        onView(withId(R.id.goals_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    2,
                    replaceTextOnViewChild("2", R.id.goal_display_edit_text)
                )
            )
        closeSoftKeyboard()
        //check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("1.0/2.0"))))
            .check(matches(hasDescendant(withText("2"))))
            .check(matches(hasDescendant(withText("shapes"))))

        scenario.close()
    }

    @Test
    fun createNewDailyGoalForTodayUsingYesterdayDailyGoal() {
        val userOnlyGoalYesterday = TemporaryUser(
            LinkedList(
                mutableListOf(
                    DailyGoal(5.0, 15.0, 10, 1.56, 12.0, 9, LocalDate.now().minusDays(1L))
                )
            )
        )
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
        //check that time is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("0.0/15.0"))))
            .check(matches(hasDescendant(withText("15"))))
            .check(matches(hasDescendant(withText("minutes"))))
        //check that nb of shapes is correct
        onView(withId(R.id.goals_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("0.0/10.0"))))
            .check(matches(hasDescendant(withText("10"))))
            .check(matches(hasDescendant(withText("shapes"))))

        scenario.close()
    }

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

    private fun replaceTextOnViewChild(value: String?, viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "replace text on a child view with specified id."

        override fun perform(uiController: UiController, view: View) =
            replaceText(value).perform(uiController, view.findViewById<View>(viewId))
    }

}