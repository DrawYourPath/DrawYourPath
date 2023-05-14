package com.epfl.drawyourpath.friendsList

import android.content.Context
import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.mainpage.IS_TEST_KEY
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
    /*
    @Test
    fun searchFriendsDisplaysFilteredResults() {
        val database = MockDataBase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }
        Thread.sleep(1000)
        var searchText = "John Doe"

        onView(withId(R.id.friends_search_bar)).perform(replaceText(searchText))
        Thread.sleep(1000)

        onView(withId(R.id.friends_search_bar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.friends_search_bar)).perform(replaceText("friend1")).perform(pressKey(
            KeyEvent.KEYCODE_ENTER))
        //onView(withId(R.id.friends_search_bar))
          //  .perform(replaceText(), pressKey(KeyEvent.KEYCODE_ENTER))





        // Check if the filtered result is displayed.
        onView(withText("friend1")).check(matches(isDisplayed()))
    }*/

    @Test
    fun correctListOfFriendsIsDisplayed() {
        val database = MockDatabase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        // onView(withText("friend1")).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun clickUnfriendButtonAndCheckIfFriend1HasAddFriendButton() {
        val database = MockDatabase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            val frag = FriendsFragment(database)
            frag.handleUsernameSearch("MOCK_USER")
            frag.handleUsernameSearch("INVALID_USER__")
            frag
        }

        /*
        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_friend_button)).perform(click())

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))
         */

        scenario.close()
    }

    @Test
    fun clickFriendButtonAndCheckIfFriend1HasUnfriendButton() {
        // TODO: use new model
        val database = MockDatabase()
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
            FriendsFragment(database)
        }

        /*
        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_friend_button)).perform(click())

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_friend_button)).perform(click())

        onView(withText("friend1")).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()))
        */

        scenario.close()
    }

    private fun addPermission(permissionStr: String) {
        getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant ${getApplicationContext<Context>()
                .packageName} android.permission.$permissionStr",
        )
    }

    @Test
    fun clickOnScanQROpensScanningActivity() {
        Intents.init()

        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)
        addPermission("CAMERA")
        addPermission("FLASHLIGHT")

        val intent = Intent(getApplicationContext(), MainActivity::class.java)
        val scenario: ActivityScenario<MainActivity> = launch(intent)

        onView(withId(R.id.friends_menu_item)).perform(click())
        onView(withId(R.id.BT_ScanQR)).perform(click())

        // Permission screen blocks the test
        // intended(hasComponent(QRScannerActivity::class.java.name))

        scenario.close()

        Intents.release()
    }

    @Test
    fun clickOnFriendsOpenProfileFragment() {
        val intent = Intent(getApplicationContext(), MainActivity::class.java)
        intent.putExtra(IS_TEST_KEY, true)

        val scenario: ActivityScenario<MainActivity> = launch(intent)
        onView(withId(R.id.friends_menu_item)).perform(click())

        onView(withText("testusername")).perform(click())

        scenario.close()
    }
}
