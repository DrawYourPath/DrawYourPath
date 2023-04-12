package com.epfl.drawyourpath.preferences

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.runner.lifecycle.ApplicationLifecycleCallback
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.login.ENABLE_ONETAP_SIGNIN
import com.epfl.drawyourpath.login.LoginActivity
import com.epfl.drawyourpath.userProfileCreation.UserProfileCreationActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PreferencesFragmentTest {

    //Note: testing the preference screen is not very convenient:
    //https://stackoverflow.com/questions/45172505/testing-android-preferencefragment-with-espresso
    private fun clickOnPreference(preferenceTitle: String) {
        onView(withId(androidx.preference.R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItem<ViewHolder>(
                hasDescendant(withText(preferenceTitle)), click()
            )
        )
    }
    //--------------- Modify Username ---------------

    /**
     * Test if clicking on modify password on the preference menu show the fragment to modify the password
     */
    @Test
    fun clickingOnModifyUsernameDisplaysModifyUsernameFragment(){
        val scenario = launchFragmentInContainer<PreferencesFragment>(themeResId = R.style.Theme_Bootcamp)

        clickOnPreference("Modify username")

        //Check that the fragment to modify the password is displayed
        onView(withId(R.id.fragment_modify_username)).check(matches(isDisplayed()))

        scenario.close()
    }

    /**
     * Test if the correct message is output below the TEST AVAILABILITY button,
     * when we click on the TEST AVAILABILITY button
     * when the username was already taken by another user.
     */
    @Test
    fun usernameUnAvailableIsPrintUnAvailableModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.input_username_modify_username)).perform(ViewActions.typeText("albert"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.test_availability_modify_username)).perform(ViewActions.click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText("*The username albert is NOT available or equal to the previous one")))

        scenario.close()
    }

    /**
     * Test if the correct message is output below the TEST AVAILABILITY button,
     * when we click on the TEST AVAILABILITY button
     * when the user name is empty in the edit view.
     */
    @Test
    fun usernameUnAvailableIsPrintIncorrectWhenWeChooseAnEmptyUsernameModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.test_availability_modify_username)).perform(ViewActions.click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText("The username can't be empty !")))

        scenario.close()
    }

    /**
     * Test if the correct message is output below the TEST AVAILABILITY button,
     * when we click on the TEST AVAILABILITY button
     * when the user name is available in the edit view.
     */
    @Test
    fun usernameCorrectIsPrintCorrectModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.input_username_modify_username)).perform(ViewActions.typeText("hugo"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.test_availability_modify_username)).perform(ViewActions.click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText("*The username hugo is available")))

        scenario.close()
    }

    /**
     * Test that click on VALIDATE button if the username is unavailable
     * will show the message that this username is unavailable
     */
    @Test
    fun validateAnUnAvailableUserNameShowMessageModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.input_username_modify_username)).perform(ViewActions.typeText("albert"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.validate_modify_username)).perform(ViewActions.click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText("*The username albert is NOT available or equal to the previous one")))

        scenario.close()
    }

    /**
     * Test that click on VALIDATE button if the username is unavailable
     * will show the message that this username is unavailable
     */
    @Test
    fun validateAnEmptyUserNameShowMessageModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.validate_modify_username)).perform(ViewActions.click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText("The username can't be empty !")))

        scenario.close()
    }

    /**
     * Test that we pass to the next correct fragment after we click on the cancel button
     * is correct
     */
    @Test
    fun correctTransitionCancelModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<PreferencesFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        clickOnPreference("Modify username")

        onView(withId(R.id.cancel_modify_username))
            .perform(ViewActions.click())

        //test if the correct fragment is show after clicking on VALIDATE button
        // Check fragment is settings
        // see https://stackoverflow.com/questions/45172505/testing-android-preferencefragment-with-espresso
        onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()))

        scenario.close()
    }

    //--------------- Modify Password ---------------

    @Test
    fun clickingOnModifyPasswordDisplaysModifyPasswordFragment() {
        val scenario =
            launchFragmentInContainer<PreferencesFragment>(themeResId = R.style.Theme_Bootcamp)

        clickOnPreference("Modify password")

        //Check that the fragment to modify the password is displayed
        onView(withId(R.id.fragment_modify_password)).check(matches(isDisplayed()))

        scenario.close()
    }

    //--------------- Disconnect ---------------

    @Test
    fun clickingOnDisconnectLaunchesLoginActivity() {
        val scenario =
            launchFragmentInContainer<PreferencesFragment>(themeResId = R.style.Theme_Bootcamp)
        Intents.init()

        clickOnPreference("Disconnect")

        //Check that the intent launches LoginActivity
        intended(hasComponent(LoginActivity::class.java.name))

        Intents.release()
        scenario.close()
    }

    @Test
    fun clickingDisconnectDisablesOneTap() {
        val scenario =
            launchFragmentInContainer<PreferencesFragment>(themeResId = R.style.Theme_Bootcamp)
        Intents.init()

        clickOnPreference("Disconnect")

        //Check that the intent disables OneTap
        intended(hasExtra(ENABLE_ONETAP_SIGNIN, false))

        Intents.release()
        scenario.close()
    }
}