package com.epfl.drawyourpath.friendsList


import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.FriendsFragment
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)


class FriendsFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)





    /*@Test
    fun searchFriendsDisplaysFilteredResults() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        var searchText = "John Doe"
        //Thread.sleep(1000)
        //onView(withId(R.id.friends_search_bar)).perform(click())


        onView(withId(R.id.friends_search_bar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.friends_search_bar)).perform(typeText("friend1")).perform(pressKey(KeyEvent.KEYCODE_ENTER))
        //onView(withId(R.id.friends_search_bar))
          //  .perform(typeText(), pressKey(KeyEvent.KEYCODE_ENTER))





        // Check if the filtered result is displayed.
        onView(withText("friend1")).check(matches(isDisplayed()))
    }
    */







    @Test
    fun correctListOfFriendsIsDisplayed() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        onView(withText("friend1")).check(matches(isDisplayed()))

    }
}