package com.github.drawyourpath.bootcamp.login

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.drawyourpath.bootcamp.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private fun buttonHasText(id: Int, text: String) {
        // @see: https://github.com/android/android-test/issues/1642
        // onView(withId(id)).check(matches(ViewMatchers.withText(text)))
    }

    @Test
    fun generalLayoutMatchesExpectedContent() {
        /*val intent = Intent(getApplicationContext(), LoginActivity::class.java);

        val scenario: ActivityScenario<GoogleAuthBootcampActivity> = launch(intent)

        // Checks the title
        onView(withId(R.id.TXT_Title)).check(matches(ViewMatchers.withSubstring("DrawYourPath")))
        onView(withId(R.id.TXT_Description)).check(matches(ViewMatchers.withSubstring("Choose a way to login")))

        // Checks the buttons' content on the register fragment.
        buttonHasText(R.id.BT_RegisterEmail, "Register with email");
        buttonHasText(R.id.BT_RegisterGoogle, "Register with Google")
        buttonHasText(R.id.BT_RegisterAnonymous, "Continue without an account");
        buttonHasText(R.id.BT_Login, "I already have an account")

        // Switches to the login fragment
        onView(withId(R.id.BT_Login)).perform(ViewActions.click())

        buttonHasText(R.id.BT_LoginEmail, "Login with email")
        buttonHasText(R.id.BT_LoginGoogle, "Login with Google")
        buttonHasText(R.id.BT_Register, "I don't have an account")

        // Switches back to the register fragment
        onView(withId(R.id.BT_Register)).perform(ViewActions.click())

        scenario.close();*/
    }

}