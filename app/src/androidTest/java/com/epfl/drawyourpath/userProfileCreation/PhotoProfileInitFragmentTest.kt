package com.epfl.drawyourpath.userProfileCreation

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import org.hamcrest.Description
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PhotoProfileInitFragmentTest : Fragment() {

    /**
     * Test that we pass to the next correct fragment after we click on the skip button
     */
    @Test
    fun correctTransitionOnClickSkip() {
        var t = goToProfilePhotoInitFragment()

        Espresso.onView(ViewMatchers.withId(R.id.skipPhotoProfile_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the correct fragment is show after clicking on VALIDATE button
        Espresso.onView(ViewMatchers.withId(R.id.endProfileCreationFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Select a photo" buttom,
     * when we click on the "VALIDATE" button
     * when the user forgot to select a photo
     */
    @Test
    fun errorPrintedWhenValidateNotSelectedPhoto() {
        var t = goToProfilePhotoInitFragment()
        Espresso.onView(ViewMatchers.withId(R.id.setPhotoProfile_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(ViewMatchers.withId(R.id.photoProfile_error_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("* You have forget to select a photo !")))


        t.close()
    }

}


/**
 * Helper function to go from the UserProfileCreation activity to the PersonalInfoFragment in the UI
 * and select the Mock Database for the tests.
 */
private fun goToProfilePhotoInitFragment(): ActivityScenario<UserProfileCreationActivity> {
    //pass in test mode to used the Mockdatabase instead of the Firebase
    var intent =
        Intent(ApplicationProvider.getApplicationContext(), UserProfileCreationActivity::class.java)
    intent.putExtra("isRunningTestForDataBase", true)
    var t: ActivityScenario<UserProfileCreationActivity> = ActivityScenario.launch(intent)
    Espresso.onView(ViewMatchers.withId(R.id.start_profile_creation_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(ViewMatchers.withId(R.id.input_userName_text_UserProfileCreation))
        .perform(ViewActions.typeText("hugo"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(ViewMatchers.withId(R.id.setUserName_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(ViewMatchers.withId(R.id.input_firstname_text_UserProfileCreation))
        .perform(ViewActions.typeText("Hugo"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(ViewMatchers.withId(R.id.input_surname_text_UserProfileCreation))
        .perform(ViewActions.typeText("Hugo"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(ViewMatchers.withId(R.id.selectDate_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(ViewMatchers.isAssignableFrom(DatePicker::class.java))
        .perform(PickerActions.setDate(2000, 2, 20))
    Espresso.onView(ViewMatchers.withId(android.R.id.button1))
        .perform(ViewActions.click())
    Espresso.onView(ViewMatchers.withId(R.id.setPersonalInfo_button_userProfileCreation))
        .perform(ViewActions.click())
    Espresso.onView(ViewMatchers.withId(R.id.input_distanceGoal_text_UserProfileCreation))
        .perform(ViewActions.typeText("10"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(ViewMatchers.withId(R.id.input_timeGoal_text_UserProfileCreation))
        .perform(ViewActions.typeText("60"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(ViewMatchers.withId(R.id.input_nbOfPathsGoal_text_UserProfileCreation))
        .perform(ViewActions.typeText("5"))
    Espresso.closeSoftKeyboard()
    Espresso.onView(ViewMatchers.withId(R.id.setUserGoals_button_userProfileCreation))
        .perform(ViewActions.click())
    return t
}