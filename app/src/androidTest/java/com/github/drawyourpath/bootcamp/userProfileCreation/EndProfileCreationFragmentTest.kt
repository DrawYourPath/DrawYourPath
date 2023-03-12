package com.github.drawyourpath.bootcamp.userProfileCreation

import android.content.Intent
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.drawyourpath.bootcamp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndProfileCreationFragmentTest : Fragment() {

    /**
     * Test that the correct text is printed on the screen with the correct username inside the text
     */
    @Test
    fun correctEndMeassageOutput() {
        var t = goToEndUserProfileCreationFragment()
        val username = "hugo"
        val correctMessage =
            "We are happy to welcome you, " + username + " in the DrawYourPath app." +
                    " Please click on the BEGIN NOW button to discover the app !"

        //test if the correct fragment is show after clicking on VALIDATE button
        Espresso.onView(withId(R.id.endProfileCreation_text_userProfileCreation))
            .check(ViewAssertions.matches(withText(correctMessage)))

        t.close()
    }
}


/**
 * Helper function to go from the UserProfileCreation activity to the PersonalInfoFragment in the UI
 * and select the Mock Database for the tests.
 */
private fun goToEndUserProfileCreationFragment(): ActivityScenario<UserProfileCreationActivity> {
    //pass in test mode to used the Mockdatabase instead of the Firebase
    var intent =
        Intent(ApplicationProvider.getApplicationContext(), UserProfileCreationActivity::class.java)
    intent.putExtra("isRunningTestForDataBase", true)
    var t: ActivityScenario<UserProfileCreationActivity> = ActivityScenario.launch(intent)
    Espresso.onView(withId(R.id.start_profile_creation_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(withId(R.id.input_userName_text_UserProfileCreation))
        .perform(ViewActions.typeText("hugo"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(withId(R.id.setUserName_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(withId(R.id.input_firstname_text_UserProfileCreation))
        .perform(ViewActions.typeText("Hugo"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(withId(R.id.input_surname_text_UserProfileCreation))
        .perform(ViewActions.typeText("Hugo"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(withId(R.id.selectDate_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(isAssignableFrom(DatePicker::class.java))
        .perform(PickerActions.setDate(2000, 2, 20))
    Espresso.onView(withId(android.R.id.button1))
        .perform(ViewActions.click())
    Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(withId(R.id.input_distanceGoal_text_UserProfileCreation))
        .perform(ViewActions.typeText("10"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(withId(R.id.input_timeGoal_text_UserProfileCreation))
        .perform(ViewActions.typeText("60"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(withId(R.id.input_nbOfPathsGoal_text_UserProfileCreation))
        .perform(ViewActions.typeText("5"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
        .perform(ViewActions.click())
    return t
}