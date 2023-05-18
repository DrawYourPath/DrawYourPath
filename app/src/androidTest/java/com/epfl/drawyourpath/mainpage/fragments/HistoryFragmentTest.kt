package com.epfl.drawyourpath.mainpage.fragments

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.path.RunsAdapter
import com.epfl.drawyourpath.utils.Utils
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HistoryFragmentTest {

    private lateinit var scenario: FragmentScenario<HistoryFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp)
    }

    @Test
    fun testRunsDisplayed() {
        val runs = mutableListOf<Run>()

        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.001, 0.001)
        val points = listOf(listOf(point1, point2))
        val path = Path(points)
        val startTime = 1683226596L
        val endTime = startTime + 10
        val run1 = Run(path, startTime, 10, endTime, "square", 0.65123)
        runs.add(run1)

        val point3 = LatLng(0.0, 0.0)
        val point4 = LatLng(0.05, 0.0)
        val points2 = listOf(listOf(point3, point4))
        val path2 = Path(points2)
        val startTime2 = 1683226963L
        val endTime2 = startTime + 10000
        val run2 = Run(path2, startTime2, 10000, endTime2, "goalpost", 1.235)
        runs.add(run2)

        val context = ApplicationProvider.getApplicationContext<Context>()

        scenario.onFragment { fragment ->
            val recyclerView =
                fragment.requireView().findViewById<RecyclerView>(R.id.runsRecyclerView)

            // set up adapter and layoutManager for recyclerView
            recyclerView.adapter = RunsAdapter(runs)
            recyclerView.layoutManager = LinearLayoutManager(fragment.requireContext())

            // wait for recyclerView to finish the layout
        }

        // verify that the runs are displayed in the recyclerView
        for ((index, run) in runs.withIndex()) {
            onView(withId(R.id.runsRecyclerView)).perform(scrollToPosition<RunsAdapter.ViewHolder>(index))
            // check date
            onView(withText(run.getDate())).check(matches(isDisplayed()))
            // check distance
            val distance = context.getString(R.string.display_distance).format(Utils.getStringDistance(run.getDistance()))
            onView(withText(distance)).check(matches(isDisplayed()))
            // check duration
            val time = context.getString(R.string.display_duration).format(Utils.getStringDuration(run.getDuration()))
            onView(withText(time)).check(matches(isDisplayed()))
            // check predicted shape
            val shape = context.getString(R.string.display_shape).format(run.predictedShape)
            onView(withText(shape)).check(matches(isDisplayed()))
            // check score of predicted shape
            val score = context.getString(R.string.display_score).format(run.similarityScore)
            onView(withText(score)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testEmptyRecyclerView() {
        val runs = mutableListOf<Run>()

        scenario.onFragment { fragment ->
            val recyclerView =
                fragment.requireView().findViewById<RecyclerView>(R.id.runsRecyclerView)

            recyclerView.adapter = RunsAdapter(runs)
            recyclerView.layoutManager = LinearLayoutManager(fragment.requireContext())
        }

        onView(withId(R.id.runsRecyclerView)).check(matches(hasChildCount(0)))
    }
}
