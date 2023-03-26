package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.community.TournamentModel
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.path.Path
import com.github.drawyourpath.bootcamp.path.Run
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime


@RunWith(AndroidJUnit4::class)
class CommunityFragmentTest {

    /**
     * test that pressing the upvote button adds a vote
     */
    @Test
    fun upvoteOnceIsCorrect() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(sampleWeekly),
            R.style.Theme_Bootcamp
        )
        val beginVotes = sampleWeekly.posts[0].getVotes()

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(beginVotes.toString()))))
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes + 1).toString()))))

        scenario.close()
    }

    /**
     * test that pressing twice the upvote button removes the vote
     */
    @Test
    fun upvoteTwiceIsCorrect() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(sampleWeekly),
            R.style.Theme_Bootcamp
        )
        val beginVotes = sampleWeekly.posts[0].getVotes()

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(beginVotes.toString()))))
        //first upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes + 1).toString()))))
        //second upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes).toString()))))
        scenario.close()
    }

    /**
     * test that pressing the downvote button subtracts a vote
     */
    @Test
    fun downvoteOnceIsCorrect() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(sampleWeekly),
            R.style.Theme_Bootcamp
        )
        val beginVotes = sampleWeekly.posts[0].getVotes()

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(beginVotes.toString()))))
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes - 1).toString()))))

        scenario.close()
    }

    /**
     * test that pressing twice the downvote button removes the vote
     */
    @Test
    fun downvoteTwiceIsCorrect() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(sampleWeekly),
            R.style.Theme_Bootcamp
        )
        val beginVotes = sampleWeekly.posts[0].getVotes()

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(beginVotes.toString()))))
        //first downvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes - 1).toString()))))
        //second downvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes).toString()))))

        scenario.close()
    }

    /**
     * test that upvote then downvote make the final vote as oldVote-1 and vice versa
     */
    @Test
    fun multipleUpvoteDownvoteIsCorrect() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(sampleWeekly),
            R.style.Theme_Bootcamp
        )
        val beginVotes = sampleWeekly.posts[0].getVotes()

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(beginVotes.toString()))))
        //upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes + 1).toString()))))
        //downvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes - 1).toString()))))
        //upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button))
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((beginVotes + 1).toString()))))

        scenario.close()
    }

    /**
     * check that the navigation to the details of the weekly tournament is correct
     */
    @Test
    fun showDetailWeeklyAndGoBackIsCorrect() {
        val weekly = sampleWeekly

        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(weekly),
            R.style.Theme_Bootcamp
        )

        //go to weekly detail
        onView(withId(R.id.community_menu_button)).perform(click())
        onView(withContentDescription("${weekly.name} details")).perform(click())
        //check details are shown
        onView(withId(R.id.community_detail_name)).check(matches(withText(weekly.name)))
        onView(withId(R.id.community_detail_description)).check(matches(withText(weekly.description)))
        onView(withId(R.id.community_detail_date)).check(matches(withText(containsString("Start in"))))
        // go back
        onView(withId(R.id.community_back_button)).perform(click())
        //check details are not shown
        onView(withId(R.id.community_detail_name)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_detail_description)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_detail_date)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_back_button)).check(matches(not(isDisplayed())))

        scenario.close()
    }

    /**
     * check that going to a detail tournament show only posts from this tournament
     */
    @Test
    fun showDetailSomeTournamentShowOnlyPostsFromSomeTournament() {
        val weekly = sampleWeekly
        val your = sampleYourTournaments
        val discover = sampleDiscoveryTournaments

        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            getBundle(weekly, your, discover),
            R.style.Theme_Bootcamp
        )

        val postFromEarth = postsDiscoverEarth.map { p -> withText(p.user) }
        val postFromAllNotEarth = buildList {
            this.addAll(sampleWeekly.posts)
            this.addAll(postsYour)
            this.addAll(postsDiscoverMoon)
        }.map { p -> withText(p.user) }
        val postFromAll = listOf(postFromEarth, postFromAllNotEarth).flatten()

        //go to earth detail
        onView(withId(R.id.community_menu_button)).perform(click())
        onView(withContentDescription("${discover[0].name} details")).perform(click())
        //check details are shown
        onView(withId(R.id.community_detail_name)).check(matches(withText(discover[0].name)))
        onView(withId(R.id.community_detail_description)).check(matches(withText(discover[0].description)))
        onView(withId(R.id.community_detail_date)).check(matches(withText(discover[0].getStartOrEndDate())))
        //check posts for discover the earth are displayed but not for any other tournament
        for (i in 0 until discover[0].posts.count()) {
            onView(withId(R.id.display_community_tournaments_view))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(i))
                .check(matches(hasDescendant(anyOf(postFromEarth))))
                .check(matches(not(hasDescendant(anyOf(postFromAllNotEarth)))))
        }
        // go back
        onView(withId(R.id.community_back_button)).perform(click())
        //check details are not shown
        onView(withId(R.id.community_detail_name)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_detail_description)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_detail_date)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_back_button)).check(matches(not(isDisplayed())))

        //check posts for every tournament are displayed
        for (i in 0 until postsYour.count() + postsDiscoverEarth.count() + postsDiscoverMoon.count() + 1) {
            onView(withId(R.id.display_community_tournaments_view))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(i))
                .check(matches(hasDescendant(anyOf(postFromAll))))
        }

        scenario.close()
    }

    private fun getBundle(weekly: Tournament, your: List<Tournament> = listOf(), discover: List<Tournament> = listOf()): Bundle {
        val bundle = Bundle()
        bundle.putSerializable("tournaments", TournamentModel(weekly, your, discover))
        return bundle
    }


    private val postsYour = mutableListOf(
        TournamentPost("Michel", sampleRun(), 158),
        TournamentPost("MrPrefect", sampleRun(), 666),
        TournamentPost("Me Myself and I", sampleRun(), 123456),
        TournamentPost("Invalid Username", sampleRun(), 0)
    )

    private val postsDiscoverEarth = mutableListOf(
        TournamentPost("SpaceMan", sampleRun(), 35),
        TournamentPost("NASA", sampleRun(), 124),
        TournamentPost("Alien", sampleRun(), -3)
    )

    private val postsDiscoverMoon = mutableListOf(
        TournamentPost("Diabolos", sampleRun(), 666),
        TournamentPost("Jaqueline", sampleRun(), 356)
    )

    /**
     * sample tournaments
     */
    private val sampleWeekly = Tournament(
        "Weekly tournament: Star Path",
        "draw a star path",
        LocalDateTime.now().plusDays(3L),
        LocalDateTime.now().plusDays(4L),
        listOf(TournamentPost("xxDarkxx", sampleRun(), -13))
    )


    /**
     * sample tournaments
     */
    private val sampleYourTournaments = mutableListOf(
        Tournament(
            "time square",
            "draw a square",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L),
            postsYour
        )
    )

    /**
     * sample tournaments
     */
    private val sampleDiscoveryTournaments = mutableListOf(
        Tournament(
            "Discover the earth",
            "draw the earth",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L),
            postsDiscoverEarth
        ),
        Tournament(
            "to the moon",
            "draw the moon",
            LocalDateTime.now().plusDays(3L),
            LocalDateTime.now().plusDays(4L),
            postsDiscoverMoon
        )
    )

    /**
     * sample run
     */
    private fun sampleRun(): Run {
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.001, 0.001)
        val points = listOf(point1, point2)
        val path = Path(points)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 10
        return Run(path, startTime, endTime)
    }

    /**
     * helper function to perform a click inside a RecyclerView
     *
     * @param viewId the id of the clickable item inside the RecyclerView
     */
    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) =
            click().perform(uiController, view.findViewById(viewId))
    }

}