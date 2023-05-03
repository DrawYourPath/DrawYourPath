package com.epfl.drawyourpath.mainpage.fragments

import androidx.test.espresso.matcher.ViewMatchers.*
import java.util.*

// @RunWith(AndroidJUnit4::class)
class ChallengeFragmentTest {

    // TODO: Rewrite these tests
    /*
    @get:Rule
    val executorRule = CountingTaskExecutorRule()

    private var mockUser = MockDatabase().mockUser
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val dailyGoal = DailyGoal.TEST_SAMPLE

    /**
     * wait for all thread to be done or throw a timeout error
     */
    private fun waitUntilAllThreadAreDone() {
        executorRule.drainTasks(2, TimeUnit.SECONDS)
        Thread.sleep(250)
    }

    /**
     * test if the new distance goal is correctly displayed when there is no today daily goal
     */
    @Test
    fun displayNewDistanceGoalWhenNoTodayDailyGoal() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val distanceProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, mockUser.goals!!.distance!!)

        waitUntilAllThreadAreDone()

        // check that distance is correct
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(distanceProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.distance!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.kilometers))))

        scenario.close()
    }

    /**
     * test if the new nb of paths is correctly displayed when there is no today daily goal
     */
    @Test
    fun displayNewNbPathsGoalWhenNoTodayDailyGoal() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val pathProgressGoal =
            context.resources.getString(R.string.progress_over_goal_path).format(0.0, mockUser.goals!!.paths!!.toDouble())

        waitUntilAllThreadAreDone()

        // check that nb of paths is correct
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.paths!!.toString()))))
            .check(matches(hasDescendant(withText(R.string.paths))))

        scenario.close()
    }

    /**
     * test if modifying the distance goal with blank display the correct goal
     */
    @Test
    fun modifyDistanceGoalWithBlankDoesNothing() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // change value of distance
        onView(withId(R.id.goals_view))
            .perform(
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
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(distanceProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.distance!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.kilometers))))

        scenario.close()
    }

    /**
     * test if modifying the nb of paths goal with blank display the correct goal
     */
    @Test
    fun modifyPathGoalWithBlankDoesNothing() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // change value of path
        onView(withId(R.id.goals_view))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    2,
                    replaceTextOnViewChild("", R.id.goal_display_edit_text),
                ),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    2,
                    pressImeActionButtonOnViewChild(R.id.goal_display_edit_text),
                ),
            )

        val pathProgressGoal =
            context.resources.getString(R.string.progress_over_goal_path).format(0.0, mockUser.goals!!.paths!!.toDouble())

        waitUntilAllThreadAreDone()

        // check that the value is correctly changed
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.paths!!.toString()))))
            .check(matches(hasDescendant(withText(R.string.paths))))

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
            LocalDateTime.now().plusDays(4L),
        )

        val t2 = Tournament(
            "2nd test",
            "test",
            LocalDateTime.now().minusDays(1L),
            LocalDateTime.now().plusDays(1L),
        )

        val t3 = Tournament(
            "test3",
            "test3",
            LocalDateTime.now().minusDays(3L),
            endDate3,
        )

        val t4 = Tournament(
            "4th test",
            "test",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(3L),
        )

        val userOnlyTournament = TemporaryUser(tournaments = listOf(t1, t2, t3, t4))

        val bundle = Bundle()
        bundle.putSerializable("user", userOnlyTournament)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp,
        )

        // check that first tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("Start in 2 days 23 hours 59 minutes"))))
        // check that second tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("2nd test"))))
            .check(matches(hasDescendant(withText("test"))))
            .check(matches(hasDescendant(withText("End in 23 hours 59 minutes"))))
        // check that third tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText("test3"))))
            .check(matches(hasDescendant(withText("test3"))))
            .check(matches(hasDescendant(withText("Ended the ${endDate3.dayOfMonth} of ${endDate3.month} ${endDate3.year}"))))
        // check that fourth tournament is correct
        onView(withId(R.id.tournaments_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(3))
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
            R.style.Theme_Bootcamp,
        )

        // check that trophy THEFIRSTKM is correct
        onView(withId(R.id.trophies_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(Trophy.THEFIRSTKM.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2022, 10, 19)}"))))
        // check that trophy TENKM is correct
        onView(withId(R.id.trophies_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText(Trophy.TENKM.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2023, 1, 1)}"))))
        // check that trophy MARATHON is correct
        onView(withId(R.id.trophies_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText(Trophy.MARATHON.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2023, 2, 10)}"))))
        // check that trophy THEFIRSTHOUR is correct
        onView(withId(R.id.trophies_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(3))
            .check(matches(hasDescendant(withText(Trophy.THEFIRSTHOUR.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2022, 8, 13)}"))))
        // check that trophy THEFIRSTPATH is correct
        onView(withId(R.id.trophies_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(4))
            .check(matches(hasDescendant(withText(Trophy.THEFIRSTPATH.trophyName))))
            .check(matches(hasDescendant(withText("Acquired ${LocalDate.of(2022, 5, 1)}"))))

        scenario.close()
    }

    // TODO: These tests are failing.
    /*

    /**
     * test if the new time goal is correctly displayed when there is no today daily goal
     */
    @Test
    fun displayNewTimeGoalWhenNoTodayDailyGoal() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        val timeProgressGoal = context.resources.getString(R.string.progress_over_goal).format(0.0, mockUser.goals!!.activityTime!!)

        waitUntilAllThreadAreDone()

        // check that time is correct
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText(timeProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.activityTime!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.minutes))))

        scenario.close()
    }


    /**
     * test if the nb of paths is correctly displayed when there is today daily goal
     */
    @Test
    fun displayNbPathsGoalOfTodayDailyGoal() {
        val bundle = Bundle()
        bundle.putBoolean(ChallengeFragment.TEST_EXTRA, true)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp,
        )

        val pathProgressGoal =
            context.resources.getString(R.string.progress_over_goal_path)
                .format(dailyGoal.paths.toDouble(), dailyGoal.expectedPaths.toDouble())

        waitUntilAllThreadAreDone()

        // check that nb of paths is correct
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(2))
            .check(matches(hasDescendant(withText(pathProgressGoal))))
            .check(matches(hasDescendant(withText(dailyGoal.expectedPaths.toString()))))
            .check(matches(hasDescendant(withText(R.string.paths))))

        scenario.close()
    }

    /**
     * test if modifying the time goal with blank display the correct goal
     */
    @Test
    fun modifyTimeGoalWithBlankDoesNothing() {
        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // change value of time
        onView(withId(R.id.goals_view))
            .perform(
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
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText(timeProgressGoal))))
            .check(matches(hasDescendant(withText(mockUser.goals!!.activityTime!!.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.minutes))))

        scenario.close()
    }


    /**
     * test if the time goal is correctly displayed when there is today daily goal
     */
    @Test
    fun displayTimeGoalOfTodayDailyGoal() {
        val bundle = Bundle()
        bundle.putBoolean(ChallengeFragment.TEST_EXTRA, true)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp,
        )

        val timeProgressGoal =
            context.resources.getString(R.string.progress_over_goal).format(dailyGoal.time, dailyGoal.expectedTime)

        waitUntilAllThreadAreDone()

        // check that time is correct
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText(timeProgressGoal))))
            .check(matches(hasDescendant(withText(dailyGoal.expectedTime.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.minutes))))

        scenario.close()
    }

    /**
     * test if the distance goal is correctly displayed when there is today daily goal
     */
    @Test
    fun displayDistanceGoalOfTodayDailyGoal() {
        val bundle = Bundle()
        bundle.putBoolean(ChallengeFragment.TEST_EXTRA, true)

        val scenario = FragmentScenario.launchInContainer(
            ChallengeFragment::class.java,
            bundle,
            R.style.Theme_Bootcamp,
        )

        val distanceProgressGoal =
            context.resources.getString(R.string.progress_over_goal).format(dailyGoal.distance, dailyGoal.expectedDistance)

        waitUntilAllThreadAreDone()

        // check that distance is correct
        onView(withId(R.id.goals_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(distanceProgressGoal))))
            .check(matches(hasDescendant(withText(dailyGoal.expectedDistance.toInt().toString()))))
            .check(matches(hasDescendant(withText(R.string.kilometers))))

        scenario.close()
    }
     */

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

    /**
     * helper function to perform a replaceText inside a RecyclerView
     *
     * @param viewId the id of the editText inside the RecyclerView
     */
    private fun pressImeActionButtonOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "press Ime action button on a child view with specified id."

        override fun perform(uiController: UiController, view: View) =
            pressImeActionButton().perform(uiController, view.findViewById(viewId))
    }
     */
}
