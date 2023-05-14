package com.epfl.drawyourpath.mainpage.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.path.RunsAdapter
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
        val run1 = Run(path, startTime, endTime)
        runs.add(run1)

        val point3 = LatLng(0.0, 0.0)
        val point4 = LatLng(0.05, 0.0)
        val points2 = listOf( listOf(point3, point4))
        val path2 = Path(points2)
        val startTime2 = 1683226963L
        val endTime2 = startTime + 10000
        val run2 = Run(path2, startTime2, endTime2)
        runs.add(run2)

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
            Espresso.onView(ViewMatchers.withId(R.id.runsRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RunsAdapter.ViewHolder>(index))

            Espresso.onView(ViewMatchers.withText(run.getDate()))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(
                ViewMatchers.withText(
                    "Distance: ${
                        String.format(
                            "%.2f",
                            run.getDistance() / 1000,
                        )
                    } Km",
                ),
            ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withText("Time taken: ${run.getDuration() / 60} minutes"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withText("Calories burned: ${run.getCalories()} kcal"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(
                ViewMatchers.withText(
                    "Speed: ${
                        String.format(
                            "%.2f",
                            run.getAverageSpeed(),
                        )
                    } m/s",
                ),
            ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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

        Espresso.onView(ViewMatchers.withId(R.id.runsRecyclerView))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))
    }
}
