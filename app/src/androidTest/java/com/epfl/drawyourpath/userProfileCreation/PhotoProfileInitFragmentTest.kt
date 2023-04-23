package com.epfl.drawyourpath.userProfileCreation

import android.content.Intent
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoProfileInitFragmentTest : Fragment() {

    /**
     * Test that we pass to the next correct fragment after we click on the skip button
     */
    @Test
    fun correctTransitionOnClickSkip() {
        var t = goToProfilePhotoInitFragment()

        onView(withId(R.id.skipPhotoProfile_button_userProfileCreation))
            .perform(click())

        // test if the correct fragment is show after clicking on VALIDATE button
        onView(withId(R.id.endProfileCreationFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        t.close()
    }

    /**
     * Test that we pass to the next correct fragment after we click on the validate button after selecting an image
     */
    @Test
    fun correctTransitionOnClickValidate() {
        var t = goToProfilePhotoInitFragment()

        onView(withId(R.id.selectPhotoInitPhotoFrag))
            .perform(click())
        onView(withId(R.id.setPhotoProfile_button_userProfileCreation))
            .perform(click())

        // test if the correct fragment is show after clicking on VALIDATE button
        onView(withId(R.id.endProfileCreationFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        t.close()
    }

    /**
     * Test if no message is printed after selecting an image
     */
    @Test
    fun correctDisplayAfterSelectImage() {
        var t = goToProfilePhotoInitFragment()

        onView(withId(R.id.selectPhotoInitPhotoFrag))
            .perform(click())

        // test if the correct fragment is show after clicking on VALIDATE button
        onView(withId(R.id.photoProfile_error_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))

        // test that the image is displayed
        onView(withId(R.id.imagePhotoProfileInitFrag))
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
        onView(withId(R.id.setPhotoProfile_button_userProfileCreation))
            .perform(click())

        // test if the text printed is correct
        onView(withId(R.id.photoProfile_error_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("* You have forgotten to select a photo !")))

        // test that the image is displayed
        onView(withId(R.id.imagePhotoProfileInitFrag))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        t.close()
    }
}

/**
 * Helper function to go from the UserProfileCreation activity to the PersonalInfoFragment in the UI
 * and select the Mock Database for the tests.
 */
private fun goToProfilePhotoInitFragment(): ActivityScenario<UserProfileCreationActivity> {
    // pass in test mode to used the Mockdatabase instead of the Firebase
    var intent =
        Intent(ApplicationProvider.getApplicationContext(), UserProfileCreationActivity::class.java)
    intent.putExtra("isRunningTestForDataBase", true)
    var t: ActivityScenario<UserProfileCreationActivity> = ActivityScenario.launch(intent)
    onView(withId(R.id.start_profile_creation_button_userProfileCreation))
        .perform(click())
    onView(withId(R.id.input_userName_text_UserProfileCreation))
        .perform(typeText("hugo"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.setUserName_button_userProfileCreation))
        .perform(click())
    onView(withId(R.id.input_firstname_text_UserProfileCreation))
        .perform(typeText("Hugo"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.input_surname_text_UserProfileCreation))
        .perform(typeText("Hugo"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.selectDate_button_userProfileCreation))
        .perform(click())
    onView(ViewMatchers.isAssignableFrom(DatePicker::class.java))
        .perform(PickerActions.setDate(2000, 2, 20))
    onView(withId(android.R.id.button1))
        .perform(click())
    onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
        .perform(click())
    onView(withId(R.id.input_distanceGoal_text_UserProfileCreation))
        .perform(typeText("10"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.input_timeGoal_text_UserProfileCreation))
        .perform(typeText("60"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.input_nbOfPathsGoal_text_UserProfileCreation))
        .perform(typeText("5"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.setUserGoals_button_userProfileCreation))
        .perform(click())
    return t
}
