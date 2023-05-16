package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GraphFromListFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()


    /**
     * Check that the graph displayed is correctyl displayed
     */
    @Test
    fun checkGraphCorrectlyDisplayed() {
        val column1Name = "column1"
        val column2Name = "column2"
        val mapDisplay = mapOf(1.0 to 2.0, 2.0 to 3.0)
        val scenario =
            launchFragmentInContainer<GraphFromListFragment>(themeResId = R.style.Theme_Bootcamp) {
                GraphFromListFragment(map = mapDisplay, titleAxe1 = column1Name, titleAxe2 = column2Name)
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graph_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the graph is displayed
        Espresso.onView(ViewMatchers.withId(R.id.graphFromList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}