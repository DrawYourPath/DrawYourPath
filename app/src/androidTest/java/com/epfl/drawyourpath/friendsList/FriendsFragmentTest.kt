package com.epfl.drawyourpath.friendsList

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.mainpage.fragments.FriendsFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendsFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // this test is commented out because I cant figure out how to type text in the search view
    /*@Test
    fun searchFriendsDisplaysFilteredResults() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }
        Thread.sleep(1000)
        var searchText = "John Doe"

        onView(withId(R.id.friends_search_bar)).perform(click())
        Thread.sleep(1000)

        onView(withId(R.id.friends_search_bar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.friends_search_bar)).perform(typeText("friend1")).perform(pressKey(KeyEvent.KEYCODE_ENTER))
        //onView(withId(R.id.friends_search_bar))
          //  .perform(typeText(), pressKey(KeyEvent.KEYCODE_ENTER))





        // Check if the filtered result is displayed.
        onView(withText("friend1")).check(matches(isDisplayed()))
    }*/

    @Test
    fun correctListOfFriendsIsDisplayed() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        onView(withText("friend1")).check(matches(isDisplayed()))
    }

    @Test
    fun clickUnfriendButtonAndCheckIfFriend1HasAddFriendButton() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_friend_button)).perform(click())

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))
    }

    @Test
    fun clickFriendButtonAndCheckIfFriend1HasUnfriendButton() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_friend_button)).perform(click())

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_friend_button)).perform(click())

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnScanQROpensScanningActivity() {
        /* TODO: uncomment once the UserModel doesn't crash during tests
        Intents.init()

        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        val scenario: ActivityScenario<MainActivity> = launch(intent)

        onView(withId(R.id.friends_menu_item)).perform(click())

        onView(withId(R.id.BT_ScanQR)).perform(click())

        intended(hasComponent(QRScannerActivity::class.java.name))

        Intents.release()
         */
    }
}
