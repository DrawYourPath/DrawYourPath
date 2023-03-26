package com.epfl.drawyourpath.preferences

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.login.ENABLE_ONETAP_SIGNIN
import com.epfl.drawyourpath.login.LoginActivity
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