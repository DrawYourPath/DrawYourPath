package com.epfl.drawyourpath.userProfileCreation

import android.content.Intent
import android.os.SystemClock
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserNameTestAndSetFragmentTest {
    /**
     * Test if the correct message is output below the TEST AVAILABILITY button,
     * when we click on the TEST AVAILABILITY button
     * when the user name was already taken by another user.
     */
    fun usernameUnAvailableIsPrintUnAvailable() {
        // pass in test mode to used the Mockdatabase instead of the Firebase
        launchSetUsernameFragment()
        onView(withId(R.id.input_userName_text_UserProfileCreation))
            .perform(ViewActions.replaceText("hugo"))
        closeSoftKeyboard()
        onView(withId(R.id.testUserName_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        onView(withId(R.id.testUserName_text_userProfileCreation))
            .check(matches(withSubstring("taken")))


    }

    /**
     * Test if the correct message is output below the TEST AVAILABILITY button,
     * when we click on the TEST AVAILABILITY button
     * when the user name is empty in the edit view.
     */
    @Test
    fun usernameUnAvailableIsPrintIncorrectWhenWeChooseAnEmptyUsername() {
        // pass in test mode to used the Mockdatabase instead of the Firebase
        launchSetUsernameFragment()
        onView(withId(R.id.testUserName_button_userProfileCreation)).perform(ViewActions.click())

        // test if the text printed is correct
        onView(withId(R.id.testUserName_text_userProfileCreation)).check(matches(withText("The username can't be empty !")))

    }

    /**
     * Test if the correct message is output below the TEST AVAILABILITY button,
     * when we click on the TEST AVAILABILITY button
     * when the user name is available in the edit view.
     */
    @Test
    fun usernameCorrectIsPrintCorrect() {
        // pass in test mode to used the Mockdatabase instead of the Firebase
        launchSetUsernameFragment()

        onView(withId(R.id.input_userName_text_UserProfileCreation)).perform(ViewActions.replaceText("albert"))
        closeSoftKeyboard()
        onView(withId(R.id.testUserName_button_userProfileCreation)).perform(ViewActions.click())

        // test if the text printed is correct
        onView(withId(R.id.testUserName_text_userProfileCreation)).check(matches(withSubstring("is available")))

    }

    /**
     * Test that click on VALIDATE button if the username is unavailable
     * will show the message that this username is unavailable
     */
    @Test
    fun validateAnUnavailableUserNameShowMessage() {
        // pass in test mode to used the Mockdatabase instead of the Firebase
        launchSetUsernameFragment()
        onView(withId(R.id.input_userName_text_UserProfileCreation)).perform(ViewActions.replaceText("hugo"))
        closeSoftKeyboard()
        onView(withId(R.id.setUserName_button_userProfileCreation)).perform(ViewActions.click())

        // test if the text printed is correct
        onView(withId(R.id.testUserName_text_userProfileCreation)).check(matches(withSubstring("taken")))
    }

    /**
     * Test that click on VALIDATE button if the username is unavailable
     * will show the message that this username is unavailable
     */
    @Test
    fun validateAnEmptyUserNameShowMessage() {
        // pass in test mode to used the Mockdatabase instead of the Firebase
        launchSetUsernameFragment()

        onView(withId(R.id.setUserName_button_userProfileCreation)).perform(ViewActions.click())

        // test if the text printed is correct
        onView(withId(R.id.testUserName_text_userProfileCreation)).check(matches(withText("The username can't be empty !")))
    }

    /**
     * Test that we pass to the next correct fragment after we click on the validate button if the username
     * is correct
     */
    @Test
    fun correctTransition() {
        // pass in test mode to used the Mockdatabase instead of the Firebase
        var intent = Intent(
            getApplicationContext(),
            UserProfileCreationActivity::class.java,
        )
        intent.putExtra(PROFILE_TEST_KEY, true)
        var t: ActivityScenario<UserProfileCreationActivity> = launch(intent)

        onView(withId(R.id.start_profile_creation_button_userProfileCreation))
            .perform(ViewActions.click())
        onView(withId(R.id.input_userName_text_UserProfileCreation))
            .perform(ViewActions.replaceText("albert"))
        closeSoftKeyboard()
        onView(withId(R.id.setUserName_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the correct fragment is shown after clicking on VALIDATE button
        onView(withId(R.id.personalInfoFragment))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}

fun launchSetUsernameFragment(): FragmentScenario<SelectUsernameFragment> {
    return launchFragmentInContainer(
        bundleOf(
            PROFILE_TEST_KEY to true
        )
    )
}
