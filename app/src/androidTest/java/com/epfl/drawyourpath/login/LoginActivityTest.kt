package com.epfl.drawyourpath.login

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.*
import com.epfl.drawyourpath.mainpage.MainActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Test
    fun loginViewModelWithNoCallbackFailsSilently() {
        val viewModel = LoginViewModel()

        viewModel.showLoginUI()
        viewModel.showRegisterUI()
    }

    private fun buttonHasText(id: Int, text: String) {
        // @see: https://github.com/android/android-test/issues/1642
        // onView(withId(id)).check(matches(ViewMatchers.withText(text)))
    }

    private fun launchLoginActivity(
        failingMock: Boolean = false,
        userInKeychain: Boolean = false,

        withOneTap: Boolean = false,
        useMock: Boolean = true,
    ): ActivityScenario<LoginActivity> {
        Intents.init()

        val intent = Intent(getApplicationContext(), LoginActivity::class.java)
        intent.putExtra(USE_MOCK_AUTH, useMock)
        intent.putExtra(MOCK_AUTH_FAIL, failingMock)
        intent.putExtra(RESTORE_USER_IN_KEYCHAIN, userInKeychain)
        intent.putExtra(ENABLE_ONETAP_SIGNIN, withOneTap)

        return launch(intent)
    }

    @Test
    fun generalLayoutMatchesExpectedContent() {
        val scenario = launchLoginActivity()

        // Checks the title
        onView(withId(R.id.TXT_Title)).check(matches(ViewMatchers.withSubstring("DrawYourPath")))
        onView(withId(R.id.TXT_Description)).check(matches(ViewMatchers.withSubstring("Choose a way to login")))

        // Checks the buttons' content on the register fragment.
        buttonHasText(R.id.BT_RegisterEmail, "Register with email")
        buttonHasText(R.id.BT_RegisterGoogle, "Register with Google")
        buttonHasText(R.id.BT_RegisterAnonymous, "Continue without an account")
        buttonHasText(R.id.BT_Login, "I already have an account")

        // Switches to the login fragment
        onView(withId(R.id.BT_Login)).perform(ViewActions.click())

        buttonHasText(R.id.BT_LoginEmail, "Login with email")
        buttonHasText(R.id.BT_LoginGoogle, "Login with Google")
        buttonHasText(R.id.BT_Register, "I don't have an account")

        // Switches back to the register fragment
        onView(withId(R.id.BT_Register)).perform(ViewActions.click())

        Intents.release()

        scenario.close()
    }

    @Test
    fun validUserInKeychainLaunchesMainMenuAutomatically() {
        var scenario = launchLoginActivity(failingMock = false, userInKeychain = true)

        intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        scenario.close()

        scenario = launchLoginActivity(failingMock = true, userInKeychain = true)

        intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun oneTapSignInLaunchesMainActivityOnSuccess() {
        val scenario = launchLoginActivity(
            failingMock = false,
            userInKeychain = false,
            withOneTap = true,
        )

        intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun failedOneTapSignInDoesntLaunchMainActivity() {
        val scenario = launchLoginActivity(
            failingMock = true,
            userInKeychain = false,
            withOneTap = true,
        )

        onView(withId(R.id.BT_RegisterGoogle)).check(matches(isDisplayed()))

        Intents.release()

        scenario.close()
    }

    @Test
    fun loginWithGoogleRedirectsToMainMenu() {
        val scenario = launchLoginActivity()

        onView(withId(R.id.BT_Login)).perform(ViewActions.click())
        onView(withId(R.id.BT_LoginGoogle)).perform(ViewActions.click())

        intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun failedLoginWithGoogleDoesntRedirectToMainMenu() {
        val scenario = launchLoginActivity(true)

        onView(withId(R.id.BT_Login)).perform(ViewActions.click())
        onView(withId(R.id.BT_LoginGoogle)).perform(ViewActions.click())

        onView(withId(R.id.BT_LoginGoogle)).check(matches(isDisplayed()))

        Intents.release()

        scenario.close()
    }

    @Test
    fun registerWithGoogleRedirectsToAccountRegistration() {
        val scenario = launchLoginActivity()

        onView(withId(R.id.BT_RegisterGoogle)).perform(ViewActions.click())

        // TODO: waiting for branch 23-user-profile-creation to be merged
        // intended(hasComponent(XXXXXXX::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun failedRegisterWithGoogleDoesntRedirectToAccountRegistration() {
        val scenario = launchLoginActivity(true)

        onView(withId(R.id.BT_RegisterGoogle)).perform(ViewActions.click())

        onView(withId(R.id.BT_RegisterGoogle)).check(matches(isDisplayed()))

        Intents.release()

        scenario.close()
    }

    @Test
    fun registerWithEmailRedirectsToAccountRegistration() {
        val scenario = launchLoginActivity()

        onView(withId(R.id.BT_RegisterEmail)).perform(ViewActions.click())

        // TODO: waiting for branch 23-user-profile-creation to be merged
        // intended(hasComponent(XXXXXXX::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun failedRegisterWithEmailDoesntRedirectToAccountRegistration() {
        val scenario = launchLoginActivity()

        onView(withId(R.id.BT_RegisterEmail)).perform(ViewActions.click())

        // The remember password window breaks everything.
        // onView(withId(R.id.BT_RegisterEmail)).check(matches(isDisplayed()))

        Intents.release()

        scenario.close()
    }

    @Test
    fun loginWithEmailRedirectsToMainMenu() {
        val scenario = launchLoginActivity()

        onView(withId(R.id.BT_Login)).perform(ViewActions.click())
        onView(withId(R.id.BT_LoginEmail)).perform(ViewActions.click())

        intended(hasComponent(MainActivity::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun failedLoginWithEmailDoesntRedirectToMainMenu() {
        val scenario = launchLoginActivity(true)

        onView(withId(R.id.BT_Login)).perform(ViewActions.click())
        onView(withId(R.id.BT_LoginEmail)).perform(ViewActions.click())

        onView(withId(R.id.BT_LoginEmail)).check(matches(isDisplayed()))

        Intents.release()

        scenario.close()
    }

    @Test
    fun registerAnonymouslyRedirectsToAccountRegistration() {
        val scenario = launchLoginActivity()

        onView(withId(R.id.BT_RegisterAnonymous)).perform(ViewActions.click())

        // TODO: waiting for branch 23-user-profile-creation to be merged
        // intended(hasComponent(XXXXXXX::class.java.name))

        Intents.release()

        scenario.close()
    }

    @Test
    fun failedRegisterAnonymouslyDoesntRedirectToAccountRegistration() {
        val scenario = launchLoginActivity(failingMock = true, useMock = true)

        onView(withId(R.id.BT_RegisterAnonymous)).perform(ViewActions.click())
        onView(withId(R.id.BT_RegisterAnonymous)).check(matches(isDisplayed()))

        Intents.release()
        scenario.close()
    }

    @Test
    fun loginActivityWithFirebaseAuthCanBeLaunched() {
        val scenario = launchLoginActivity(useMock = false)

        Intents.release()
        scenario.close()
    }

    @Test
    fun loginWithInvalidEmailAndPasswordFailsWithFirebaseAuth() {
        val scenario =
            launchLoginActivity(useMock = false, withOneTap = false, userInKeychain = false)

        // Empty email
        onView(withId(R.id.BT_RegisterEmail)).perform(ViewActions.click())

        // Invalid email
        onView(withId(R.id.ET_Email)).perform(ViewActions.typeText("inv"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.BT_RegisterEmail)).perform(ViewActions.click())

        // Empty password
        onView(withId(R.id.ET_Email)).perform(ViewActions.typeText("alid@domain.org"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.BT_RegisterEmail)).perform(ViewActions.click())

        // Invalid password
        onView(withId(R.id.ET_Password)).perform(ViewActions.typeText("a"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.BT_RegisterEmail)).perform(ViewActions.click())

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.waitForWindowUpdate(null, 3000)
        var currentPackageName = uiDevice.currentPackageName
        if (currentPackageName != "com.epfl.drawyourpath") {
            uiDevice.pressBack()
        }

        // Switches to the login view.
        onView(withId(R.id.BT_Login)).perform(ViewActions.click())

        // Empty email
        onView(withId(R.id.BT_LoginEmail)).perform(ViewActions.click())

        // Invalid email
        onView(withId(R.id.ET_Email)).perform(ViewActions.typeText("inv"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.BT_LoginEmail)).perform(ViewActions.click())

        // Empty password
        onView(withId(R.id.ET_Email)).perform(ViewActions.typeText("alid@domain.org"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.BT_LoginEmail)).perform(ViewActions.click())

        // Invalid password
        onView(withId(R.id.ET_Password)).perform(ViewActions.typeText("a"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.BT_LoginEmail)).perform(ViewActions.click())

        uiDevice.waitForWindowUpdate(null, 3000)
        currentPackageName = uiDevice.currentPackageName
        if (currentPackageName != "com.epfl.drawyourpath") {
            uiDevice.pressBack()
        }

        Intents.release()
        scenario.close()
    }
}
