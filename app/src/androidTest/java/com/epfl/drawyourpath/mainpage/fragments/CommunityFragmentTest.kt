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
import com.epfl.drawyourpath.community.TournamentPost
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommunityFragmentTest {

    private val post = MockDatabase().mockPost

    private val tournament = MockDatabase().mockTournament.value!!

    /**
     * test that the function getUserVotes return the correct result
     */
    @Test
    fun getUserVotesReturnsCorrectMap() {
        val run = Run(
            Path(listOf(listOf(LatLng(0.0, 0.1)))),
            10,
            10,
            20,
            "Cat",
            0.9,
        )
        val userVotes = mutableMapOf("test" to 1)
        val post = TournamentPost("postId", "tournament id", "tournament name", "userid", run, usersVotes = userVotes)
        assertThat(post.getUsersVotes(), `is`(userVotes))
    }

    /**
     * test that pressing the upvote button adds a vote
     */
    @Test
    fun upvoteOnceAddsVote() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(post.getVotes().toString()))))
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() + 1).toString()))))

        scenario.close()
    }

    /**
     * test that pressing twice the upvote button adds then removes the vote
     */
    @Test
    fun upvoteTwiceAddsThenRemovesVote() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(post.getVotes().toString()))))
        // first upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() + 1).toString()))))
        // second upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes()).toString()))))
        scenario.close()
    }

    /**
     * test that pressing the downvote button subtracts a vote
     */
    @Test
    fun downvoteOnceSubtractsVote() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(post.getVotes().toString()))))
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() - 1).toString()))))

        scenario.close()
    }

    /**
     * test that pressing twice the downvote button subtracts then removes the vote
     */
    @Test
    fun downvoteTwiceSubtractsThenRemovesVote() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(post.getVotes().toString()))))
        // first downvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() - 1).toString()))))
        // second downvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes()).toString()))))

        scenario.close()
    }

    /**
     * test that upvote then downvote make the final vote as oldVote-1 and vice versa
     */
    @Test
    fun multipleUpvoteDownvoteAddsAndSubtractsCorrectly() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText(post.getVotes().toString()))))

        // upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() + 1).toString()))))
        // downvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_downvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() - 1).toString()))))
        // upvote
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_upvote_button)),
        )
        onView(withId(R.id.display_community_tournaments_view)).check(matches(hasDescendant(withText((post.getVotes() + 1).toString()))))

        scenario.close()
    }

    /**
     * check that the navigation to the details of the weekly tournament shows the details then go back
     */
    @Test
    fun showDetailTournamentShowsTheDetailsThenGoBack() {
        val scenario = FragmentScenario.launchInContainer(
            CommunityFragment::class.java,
            Bundle(),
            R.style.Theme_Bootcamp,
        )

        // go to weekly detail
        onView(withId(R.id.community_menu_button)).perform(click())
        onView(withContentDescription("${tournament.name} details")).perform(click())
        // check details are shown
        onView(withId(R.id.community_detail_name)).check(matches(withText(tournament.name)))
        onView(withId(R.id.community_detail_description)).check(matches(withText(tournament.description)))
        onView(withId(R.id.community_detail_date)).check(matches(withText(containsString("End in"))))
        // go back
        onView(withId(R.id.community_back_button)).perform(click())
        // check details are not shown
        onView(withId(R.id.community_detail_name)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_detail_description)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_detail_date)).check(matches(not(isDisplayed())))
        onView(withId(R.id.community_back_button)).check(matches(not(isDisplayed())))

        scenario.close()
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
