package com.epfl.drawyourpath.userProfileCreation

import android.content.Intent
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserGoalsInitFragmentTest {

    /**
     * Test if the correct error message is output below the "Enter your distance Goal" edit text,
     * when we click on the "VALIDATE" button
     * when the distance goal is empty (equivalent that if the user forgot to enter it).
     */
    @Test
    fun errorPrintedWhithEmptyDistanceGoal() {
        var t = goToProfilePhotoInitFragment()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        Espresso.onView(withId(R.id.distanceGoalError_text_userProfileCreation))
            .check(matches(withText("* This field can't be empty !")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your time Goal" edit text,
     * when we click on the "VALIDATE" button
     * when the time goal is empty (equivalent that if the user forgot to enter it).
     */
    @Test
    fun errorPrintedWhithEmptyTimeGoal() {
        var t = goToProfilePhotoInitFragment()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        Espresso.onView(withId(R.id.timeGoalError_text_userProfileCreation))
            .check(matches(withText("* This field can't be empty !")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your number of paths goal" edit text,
     * when we click on the "VALIDATE" button
     * when the number of paths goal is empty (equivalent that if the user forgot to enter it).
     */
    @Test
    fun errorPrintedWhithEmptyNbOfPathsGoal() {
        var t = goToProfilePhotoInitFragment()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        Espresso.onView(withId(R.id.nbOfPathsGoalError_text_userProfileCreation))
            .check(matches(withText("* This field can't be empty !")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your distance Goal" edit text,
     * when we click on the "VALIDATE" button
     * when the distance goal is in an incorrect format (must be an integer).
     */
    @Test
    fun errorPrintedWhithIncorrectDistanceGoal() {
        var t = goToProfilePhotoInitFragment()
        // incorrect double
        Espresso.onView(withId(R.id.input_distanceGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10.0"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())
        // test if the text printed is correct
        Espresso.onView(withId(R.id.distanceGoalError_text_userProfileCreation))
            .check(matches(withText("* This field can be only composed of integer number.")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your time Goal" edit text,
     * when we click on the "VALIDATE" button
     * when the time goal is in an incorrect format (must be an integer).
     */
    @Test
    fun errorPrintedWhithIncorrectTimeGoal() {
        var t = goToProfilePhotoInitFragment()
        // incorrect double
        Espresso.onView(withId(R.id.input_timeGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10.0"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())
        // test if the text printed is correct
        Espresso.onView(withId(R.id.timeGoalError_text_userProfileCreation))
            .check(matches(withText("* This field can be only composed of integer number.")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your number of Paths Goal" edit text,
     * when we click on the "VALIDATE" button
     * when the number of paths goal is in an incorrect format (must be an integer).
     */
    @Test
    fun errorPrintedWhithIncorrectNbOfPathsGoal() {
        var t = goToProfilePhotoInitFragment()
        // incorrect double
        Espresso.onView(withId(R.id.input_nbOfPathsGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10.0"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())
        // test if the text printed is correct
        Espresso.onView(withId(R.id.nbOfPathsGoalError_text_userProfileCreation))
            .check(matches(withText("* This field can be only composed of integer number.")))

        t.close()
    }

    /**
     * Test if no error message is output below the "Enter your distance goal" edit text,
     * when we click on the "VALIDATE" button
     * when the number of paths goal is correct (this number is an integer)
     */
    @Test
    fun noErrorPrintedWhithCorrectDistanceGoal() {
        var t = goToProfilePhotoInitFragment()

        Espresso.onView(withId(R.id.input_distanceGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        Espresso.onView(withId(R.id.distanceGoalError_text_userProfileCreation))
            .check(matches(withText("")))

        t.close()
    }

    /**
     * Test if no error message is output below the "Enter your time goal" edit text,
     * when we click on the "VALIDATE" button
     * when the time goal is correct (this number is an integer)
     */
    @Test
    fun noErrorPrintedWhithCorrectTimeGoal() {
        var t = goToProfilePhotoInitFragment()

        Espresso.onView(withId(R.id.input_timeGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        Espresso.onView(withId(R.id.timeGoalError_text_userProfileCreation))
            .check(matches(withText("")))

        t.close()
    }

    /**
     * Test if no error message is output below the "Enter your number of paths goal" edit text,
     * when we click on the "VALIDATE" button
     * when the number of paths goal is correct (this number is an integer)
     */
    @Test
    fun noErrorPrintedWhithCorrectNbOfPathsGoal() {
        var t = goToProfilePhotoInitFragment()

        Espresso.onView(withId(R.id.input_nbOfPathsGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the text printed is correct
        Espresso.onView(withId(R.id.nbOfPathsGoalError_text_userProfileCreation))
            .check(matches(withText("")))

        t.close()
    }

    /**
     * Test that we pass to the next correct fragment after we click on the validate button if the data
     * are correct
     */
    @Test
    fun correctTransition() {
        var t = goToProfilePhotoInitFragment()

        Espresso.onView(withId(R.id.input_distanceGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("10"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.input_timeGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("60"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.input_nbOfPathsGoal_text_UserProfileCreation))
            .perform(ViewActions.replaceText("5"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setUserGoals_button_userProfileCreation))
            .perform(ViewActions.click())

        // test if the correct fragment is show after clicking on VALIDATE button
        Espresso.onView(withId(R.id.photoProfileInitFragment))
            .check(matches(isDisplayed()))

        t.close()
    }
}

/**
 * Helper function to go from the UserProfileCreation activity to the PersonalInfoFragment in the UI
 * and select the Mock Database for the tests.
 */
private fun goToProfilePhotoInitFragment(): FragmentScenario<UserGoalsInitFragment> {
    return launchFragmentInContainer(
        bundleOf(
            PROFILE_TEST_KEY to true
        )
    )
}
