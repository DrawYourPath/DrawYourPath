package com.github.drawyourpath.bootcamp.userProfileCreation

import android.content.Intent
import android.widget.DatePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.drawyourpath.bootcamp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonalInfoFragmentTest {
    /**
     * Test if the correct error message is output below the "Enter your first name" edit text,
     * when we click on the "VALIDATE" button
     * when the firstname is empty (equivalent that if the user forgot to enter it).
     */
    @Test
    fun errorPrintedWhithEmptyFirstname() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        var t = goToPersonalInfoFragment()

        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.firstnameError_text_userProfileCreation))
            .check(matches(withText("* This field can't be empty !")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your surname name" edit text,
     * when we click on the "VALIDATE" button
     * when the surname is empty (equivalent that if the user forgot to enter it).
     */
    @Test
    fun errorPrintedWhithEmptySurname() {
        var t = goToPersonalInfoFragment()
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.surnameError_text_userProfileCreation))
            .check(matches(withText("* This field can't be empty !")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your firstname" edit text,
     * when we click on the "VALIDATE" button
     * when the firstname is in an incorrect format (not composed only of '-' or letters).
     */
    @Test
    fun errorPrintedWhithIncorrectFirstname() {
        var t = goToPersonalInfoFragment()
        Espresso.onView(withId(R.id.input_firstname_text_UserProfileCreation))
            .perform(ViewActions.typeText("Hugo852"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.firstnameError_text_userProfileCreation))
            .check(matches(withText("* This field is in an incorrect format ! It must be composed of letters or character '-'")))

        t.close()
    }

    /**
     * Test if the correct error message is output below the "Enter your surname" edit text,
     * when we click on the "VALIDATE" button
     * when the surname is in an incorrect format (not composed only of '-' or letters).
     */
    @Test
    fun errorPrintedWhithIncorrectSurname() {
        var t = goToPersonalInfoFragment()
        Espresso.onView(withId(R.id.input_surname_text_UserProfileCreation))
            .perform(ViewActions.typeText("Hugo852"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.surnameError_text_userProfileCreation))
            .check(matches(withText("* This field is in an incorrect format ! It must be composed of letters or character '-'")))

        t.close()
    }

    /**
     * Test if nothing is output below the "Enter your firstname" edit text,
     * when we click on the "VALIDATE" button
     * when the firstname is in a correct format (composed only of '-' or letters).
     */
    @Test
    fun noErrorPrintedWhithCorrectFirstname() {
        var t = goToPersonalInfoFragment()
        Espresso.onView(withId(R.id.input_firstname_text_UserProfileCreation))
            .perform(ViewActions.typeText("Hugo"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.firstnameError_text_userProfileCreation))
            .check(matches(withText("")))

        t.close()
    }

    /**
     * Test if nothing is output below the "Enter your surname" edit text,
     * when we click on the "VALIDATE" button
     * when the surname is in a correct format (composed only of '-' or letters).
     */
    @Test
    fun noErrorPrintedWhithCorrectSurname() {
        var t = goToPersonalInfoFragment()
        Espresso.onView(withId(R.id.input_surname_text_UserProfileCreation))
            .perform(ViewActions.typeText("Hugo"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.surnameError_text_userProfileCreation))
            .check(matches(withText("")))

        t.close()
    }

    /**
     * Test if the initial date show to the user is dd/mm/yyyy
     */
    @Test
    fun correctInitialDateShow() {
        var t = goToPersonalInfoFragment()

        //test if the text printed is correct
        Espresso.onView(withId(R.id.showDate_text_userProfileCreation))
            .check(matches(withText("dd/mm/yyyy")))

        t.close()
    }

    //REMOVE THIS COMMAND TO TEST THE DATE LOCALLY BECAUSE CI DON'T HAVE A DATE PICKER

    /**
     * Test if the correct date is show to the user after a date has been selected by the user
     * int date picker.
     */
    @Test
    fun correctDateShowAfterSelection() {
        var t = goToPersonalInfoFragment()

        Espresso.onView(withId(R.id.selectDate_button_userProfileCreation))
            .perform(ViewActions.click())
        Espresso.onView(isAssignableFrom(DatePicker::class.java))
            .perform(PickerActions.setDate(2000, 2, 20))
        Espresso.onView(withId(android.R.id.button1))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.showDate_text_userProfileCreation))
            .check(matches(withText("20/2/2000")))


        t.close()
    }

    /**
     * Test if the correct output message is show to the user under the date section
     * when the user forgot to select a data
     */
    @Test
    fun errorWhenNoDateSelected() {
        var t = goToPersonalInfoFragment()

        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct
        Espresso.onView(withId(R.id.dateError_text_userProfileCreation))
            .check(matches(withText("* You forgot to indicate your birth date")))

        t.close()
    }

    /**
     * Test if the correct output message is show to the user under the date section
     * when the user enter an incorrect
     * when the user has under 10 years or over 100 years
     */
    @Test
    fun errorWhenIncorrectDateSelected() {
        var t = goToPersonalInfoFragment()

        Espresso.onView(withId(R.id.selectDate_button_userProfileCreation))
            .perform(ViewActions.click())
        Espresso.onView(isAssignableFrom(DatePicker::class.java))
            .perform(PickerActions.setDate(2022, 2, 20))
        Espresso.onView(withId(android.R.id.button1))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct when the user is too young
        Espresso.onView(withId(R.id.dateError_text_userProfileCreation))
            .check(matches(withText("* Your birthdate is impossible at this date")))

        Espresso.onView(withId(R.id.selectDate_button_userProfileCreation))
            .perform(ViewActions.click())
        Espresso.onView(isAssignableFrom(DatePicker::class.java))
            .perform(PickerActions.setDate(1900, 2, 20))
        Espresso.onView(withId(android.R.id.button1))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.setPersonalInfo_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the text printed is correct when the user is too old
        Espresso.onView(withId(R.id.dateError_text_userProfileCreation))
            .check(matches(withText("* Your birthdate is impossible at this date")))
        t.close()
    }

    /**
     * Test if no output message is show to the user under the date section
     * when the user enter a correct date
     */
    @Test
    fun noErrorWhenCorrectDateSelected() {
        var t = goToPersonalInfoFragment()

        Espresso.onView(withId(R.id.selectDate_button_userProfileCreation))
            .perform(ViewActions.click())
        Espresso.onView(isAssignableFrom(DatePicker::class.java))
            .perform(PickerActions.setDate(2000, 2, 20))
        Espresso.onView(withId(android.R.id.button1))
            .perform(ViewActions.click())

        //test if the text printed is correct when the user is too young
        Espresso.onView(withId(R.id.dateError_text_userProfileCreation))
            .check(matches(withText("")))

        t.close()
    }


    /**
     * Test that we pass to the next correct fragment after we click on the validate button if the data
     * are correct
     */
    @Test
    fun correctTransition() {
        var t = goToPersonalInfoFragment()

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

        //test if the correct fragment is show after clicking on VALIDATE button
        Espresso.onView(withId(R.id.userGoalInitFragment))
            .check(matches(isDisplayed()))

        t.close()
    }
}


/**
 * Helper function to go from the UserProfileCreation activity to the PersonalInfoFragment in the UI
 * and select the Mock Database for the tests.
 */
private fun goToPersonalInfoFragment(): ActivityScenario<UserProfileCreationActivity> {
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
    return t
}