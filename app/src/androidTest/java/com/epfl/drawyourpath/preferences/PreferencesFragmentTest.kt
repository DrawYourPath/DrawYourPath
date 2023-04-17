package com.epfl.drawyourpath.preferences

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
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
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PreferencesFragmentTest {
    //for the test of the profile photo modification
    private val photoSelectedInPicker : Bitmap = Bitmap.createBitmap(8,5, Bitmap.Config.RGB_565)

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
        val bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.input_username_modify_username)).perform(typeText("albert"))
        closeSoftKeyboard()
        onView(withId(R.id.test_availability_modify_username)).perform(click())

        //test if the text printed is correct
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val text: String =  targetContext.resources.getString(R.string.username_not_vailable_or_previous_one).format("albert")
        onView(withId(R.id.error_text_modify_username)).check(matches(withText(text)))

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

        onView(withId(R.id.test_availability_modify_username)).perform(click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText(R.string.username_can_t_be_empty)))

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
        val bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.input_username_modify_username)).perform(typeText("hugo"))
        closeSoftKeyboard()
        onView(withId(R.id.test_availability_modify_username)).perform(click())

        //test if the text printed is correct
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val text: String =  targetContext.resources.getString(R.string.username_available).format("hugo")
        onView(withId(R.id.error_text_modify_username)).check(matches(withText(text)))

        scenario.close()
    }

    /**
     * Test that click on VALIDATE button if the username is unavailable
     * will show the message that this username is unavailable
     */
    @Test
    fun validateAnUnAvailableUserNameShowMessageModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.input_username_modify_username)).perform(typeText("albert"))
        closeSoftKeyboard()
        onView(withId(R.id.validate_modify_username)).perform(click())

        //test if the text printed is correct
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val text: String =  targetContext.resources.getString(R.string.username_not_vailable_or_previous_one).format("albert")
        onView(withId(R.id.error_text_modify_username)).check(matches(withText(text)))

        scenario.close()
    }

    /**
     * Test that click on VALIDATE button if the username is empty
     * will show the message that this username is empty
     */
    @Test
    fun validateAnEmptyUserNameShowMessageModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyUsernameFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.validate_modify_username)).perform(click())

        //test if the text printed is correct
        onView(withId(R.id.error_text_modify_username)).check(matches(withText(R.string.username_can_t_be_empty)))

        scenario.close()
    }

    /**
     * Test that we pass to the next correct fragment after we click on the cancel button
     * is correct
     */
    @Test
    fun correctTransitionCancelModifyUsername() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<PreferencesFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        clickOnPreference("Modify username")

        onView(withId(R.id.cancel_modify_username))
            .perform(click())

        //test if the correct fragment is show after clicking on VALIDATE button
        // Check fragment is settings
        // see https://stackoverflow.com/questions/45172505/testing-android-preferencefragment-with-espresso
        onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()))

        scenario.close()
    }

    //--------------- Modify Profile Photo ---------------
    /**
     * Test if clicking on modify profile photo on the preferences menu show the fragment to modify the profile photo
     */
    @Test
    fun clickingOnModifyProfilePhotoShowModifyProfilePhotoFragment(){
        val scenario = launchFragmentInContainer<PreferencesFragment>(themeResId = R.style.Theme_Bootcamp)

        clickOnPreference("Modify Profile Photo")

        //check that the fragment that modify the profile photo is displayed
        onView(withId( R.id.fragment_modify_profile_photo)).check(matches(isDisplayed()))

        scenario.close()
    }

    /**
     * Check that click on the cancel button, return back to preferences
     */
    @Test
    fun correctTransitionCancelButtonModifyProfilePhoto(){
        val scenario = launchFragmentInContainer<PreferencesFragment>(themeResId = R.style.Theme_Bootcamp)
        clickOnPreference("Modify Profile Photo")

        onView(withId(R.id.cancel_modify_profile_photo)).perform(click())

        //check that the fragment of preferences is displayed
        // Check fragment is settings
        // see https://stackoverflow.com/questions/45172505/testing-android-preferencefragment-with-espresso
        onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()))

        scenario.close()
    }

    /**
     * Check that the correct description text photo is displayed before selecting a new photo
     */
    @Test
    fun correctDescriptionTextBeforeNewPhotoModifyProfilePhoto(){
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.photo_description_modify_profile_photo)).check(matches(withText(R.string.actual_profile_photo)))

        scenario.close()
    }

    /**
     * Check that the correct photo is displayed before selecting a new photo when the user have no profile photo
     */
    @Test
    fun correctDefaultPhotoBeforeNewPhotoModifyProfilePhoto(){
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.photo_modify_profile_photo)).check(matches(withTagValue(equalTo( R.drawable.profile_placholderpng))))

        scenario.close()
    }

    /**
     * Check that the correct photo is displayed after selecting a new photo
     */
    @Test
    fun correctPhotoAfterNewPhotoModifyProfilePhoto(){
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.select_photo_modify_profile_photo)).perform(click())

        onView(withId(R.id.photo_modify_profile_photo)).check(matches(withTagValue(equalTo(photoSelectedInPicker.byteCount))))

        scenario.close()
    }

    /**
     * Check that the correct description text photo is displayed after selecting a new photo
     */
    @Test
    fun correctDescriptionTextAfterNewPhotoModifyProfilePhoto(){
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.select_photo_modify_profile_photo)).perform(click())

        onView(withId(R.id.photo_description_modify_profile_photo)).check(matches(withText(R.string.new_profile_photo_selected)))

        scenario.close()
    }

    /**
     * Test if the correct error message is output below the "Select a photo" buttom,
     * when we click on the "VALIDATE" button
     * when the user forgot to select a photo
     */
    @Test
    fun errorPrintedWhenValidateNotModifyPhoto() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.validate_modify_profile_photo))
            .perform(click())

        //test if the text printed is correct
        onView(withId(R.id.error_modify_profile_photo)).check(matches(withText(R.string.error_select_new_profile_photo)))

        scenario.close()
    }

    /**
     * Test if no transition occur in fragment
     * when we click on the "VALIDATE" button
     * when the user forgot to select a photo
     */
    @Test
    fun noTransitionWhenValidateNotModifyPhoto() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        onView(withId(R.id.validate_modify_profile_photo))
            .perform(click())

        //test if the text printed is correct
        onView(withId(R.id.fragment_modify_profile_photo)).check(matches(isDisplayed()))

        scenario.close()
    }

    /**
     * Test if no error message is output below the "Select a photo" button,
     * at the initial state
     */
    @Test
    fun noErrorInitModifyPhoto() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        //test if the text printed is correct
        onView(withId(R.id.error_modify_profile_photo)).check(matches(withText("")))

        scenario.close()
    }
    /**
     * Test if no error message is output below the "Select a photo" buttom,
     * when we click on the "VALIDATE" button
     * when the user select a photo after forgot to select a photo
     */
    @Test
    fun noErrorWhenSelectPhotoModifyPhoto() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        val bundle: Bundle = Bundle()
        bundle.putBoolean("isRunningTestForDataBase", true)
        val scenario = launchFragmentInContainer<ModifyProfilePhotoFragment>(fragmentArgs = bundle, themeResId = R.style.Theme_Bootcamp)

        //validate to display an error message
        onView(withId(R.id.validate_modify_profile_photo)).perform(click())

        onView(withId(R.id.select_photo_modify_profile_photo))
            .perform(click())

        //test if the text printed is correct(=error message disappear)
        onView(withId(R.id.error_modify_profile_photo)).check(matches(withText("")))

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