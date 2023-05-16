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
class TableFromListFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    /**
     * Check the score recognized and the score gives to this form are correctly displayed on this fragment.
     */
    @Test
    fun correctFormScoreDisplayed() {
        val column1Name = "column1"
        val column2Name = "column"
        val mapDisplay = mapOf("1" to "2.0m/s", "2" to "3.0m/s")
        val scenario =
            launchFragmentInContainer<TableFromListFragment>(themeResId = R.style.Theme_Bootcamp) {
                TableFromListFragment(map = mapDisplay, column1Name = column1Name, column2Name = column2Name)
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.table_from_list_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // check that the table is displayed
        Espresso.onView(ViewMatchers.withId(R.id.tableFromList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the column1 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_1))
            .check(ViewAssertions.matches(ViewMatchers.withText(column1Name)))
        // check the column2 title is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_COLUMN_TITLE_2))
            .check(ViewAssertions.matches(ViewMatchers.withText(column2Name)))
        // check the first key is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_KEY_INIT + 0))
            .check(ViewAssertions.matches(ViewMatchers.withText(mapDisplay.keys.toMutableList()[0])))
        // check the second key is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_KEY_INIT + 1))
            .check(ViewAssertions.matches(ViewMatchers.withText(mapDisplay.keys.toMutableList()[1])))
        // check the fist value is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_VALUE_INIT))
            .check(ViewAssertions.matches(ViewMatchers.withText(mapDisplay.values.toMutableList()[0])))
        // check the second value is display
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_VALUE_INIT + 1))
            .check(ViewAssertions.matches(ViewMatchers.withText(mapDisplay.values.toMutableList()[1])))
        // Check that the rows are present
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_ROW_TITLE))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_ROW_VALUE + 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(TableFromListFragment.ID_ROW_VALUE + 1))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }
}
