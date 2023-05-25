package com.epfl.drawyourpath.community

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.mainpage.MainActivity
import org.junit.Test

class TournamentPostDetailTest {

    @Test
    fun goToPostDetailAndBack() {
        val scenario = ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
        // go to community fragment
        onView(withId(R.id.community_menu_item)).perform(click())
        // go to post details
        onView(withId(R.id.display_community_tournaments_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.tournament_path_display_image)),
        )
        // check that post detail fragment is displayed
        onView(withId(R.id.run_info_stats_fragment)).check(matches(isDisplayed()))
        // press on back button
        onView(withId(R.id.post_detail_back_button)).perform(click())
        // check that community fragment is displayed
        onView(withId(R.id.community_add_post_button)).check(matches(isDisplayed()))

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
