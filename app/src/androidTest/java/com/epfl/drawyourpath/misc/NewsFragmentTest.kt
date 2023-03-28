package com.epfl.drawyourpath.misc

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsFragmentTest {

    private var leftButtonClicked = false;
    private var rightButtonClicked = false;

    private val news1 = News(
        "Michel completed a drawing",
        "Michel completed the drawing \"foobar\" in 2 hours.",
        null,
        NewsAction("SEND CONGRATS") { ->
            rightButtonClicked = !rightButtonClicked
        })

    private val news2 = News(
        "New trophy unlocked",
        "You completed the trophy \"Serial Runner\" by running everyday for a year.",
        NewsAction("DISMISS") { ->
            leftButtonClicked = !leftButtonClicked
        },
        NewsAction("TO TROPHIES") { ->
            rightButtonClicked = !rightButtonClicked
        })

    private val news3 = News(
        "TheRunner32 completed a drawing",
        "Michel completed the drawing \"cat\" in 1 hour.",
        NewsAction("SEND CONGRATS") { ->
            leftButtonClicked = !leftButtonClicked
        },
        null)

    private lateinit var scenario: FragmentScenario<NewsFragment>

    @Before
    fun setUp() {
        leftButtonClicked = false
        rightButtonClicked = false

        scenario = launchFragmentInContainer()
    }

    @Test
    fun addedNewsAppearsInFeedWithSpecifiedData() {

        scenario.onFragment {
            it.addNews(news2)
        }

        onView(withId(R.id.TV_Title)).check(matches(withSubstring(news2.title)))
        onView(withId(R.id.TV_Description)).check(matches(withSubstring(news2.description)))
        onView(withId(R.id.BT_Action1)).check(matches(withSubstring(news2.action1!!.title)))
        onView(withId(R.id.BT_Action2)).check(matches(withSubstring(news2.action2!!.title)))
    }

    @Test
    fun newsLeftButtonIsHiddentWhenNoActionIsSpecified() {
        scenario.onFragment {
            it.addNews(news1)
        }

        onView(withId(R.id.TV_Title)).check(matches(withSubstring(news1.title)))
        onView(withId(R.id.BT_Action1)).check(matches(not(isDisplayed())))
        onView(withId(R.id.BT_Action2)).check(matches(isDisplayed()))
    }

    @Test
    fun newsRighttButtonIsHiddentWhenNoActionIsSpecified() {
        scenario.onFragment {
            it.addNews(news3)
        }

        onView(withId(R.id.TV_Title)).check(matches(withSubstring(news3.title)))
        onView(withId(R.id.BT_Action1)).check(matches((isDisplayed())))
        onView(withId(R.id.BT_Action2)).check(matches(not(isDisplayed())))
    }

    @Test
    fun newsButtonsExecuteAssignedAction() {
        scenario.onFragment {
            it.addNews(news2)
        }

        onView(withId(R.id.TV_Title)).check(matches(withSubstring(news2.title)))

        onView(withId(R.id.BT_Action1)).perform(click())

        assertEquals(leftButtonClicked, true)
        assertEquals(rightButtonClicked, false)

        onView(withId(R.id.BT_Action2)).perform(click())


        assertEquals(leftButtonClicked, true)
        assertEquals(rightButtonClicked, true)
    }
}
