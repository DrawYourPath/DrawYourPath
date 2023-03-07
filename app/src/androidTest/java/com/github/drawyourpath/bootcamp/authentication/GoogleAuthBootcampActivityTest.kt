package com.github.drawyourpath.bootcamp.authentication

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
import com.github.drawyourpath.bootcamp.authentication.provider.AuthUserCallback
import com.github.drawyourpath.bootcamp.authentication.provider.FirebaseAuthProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleAuthBootcampActivityTest {
    @Test
    fun singleSignInShowsUsername() {
        val intent = Intent(getApplicationContext(), GoogleAuthBootcampActivity::class.java)

        intent.putExtra(GA_USE_MOCK_AUTH_PROVIDER_KEY, true)

        val scenario: ActivityScenario<GoogleAuthBootcampActivity> = launch(intent)

        onView(withId(R.id.bc_google_signin)).perform(ViewActions.click())
        onView(withId(R.id.sign_in_status)).check(matches(ViewMatchers.withSubstring("John Doe")))

        onView(withId(R.id.bc_google_signin)).perform(ViewActions.click())
        onView(withId(R.id.sign_in_status)).check(matches(ViewMatchers.withSubstring("Failed to sign in")))

        onView(withId(R.id.bc_google_signout)).perform(ViewActions.click())
        // TODO: Uncomment this. There is a bug in expresso:
        //       https://github.com/android/android-test/issues/1642
        // onView(withId(R.id.sign_in_status)).check(matches(ViewMatchers.withSubstring("Not signed")))

        scenario.close()
    }

    @Test
    fun firebaseProviderCorrectlyRegistersCallback() {
        val provider = object : FirebaseAuthProvider() {
            override fun signIn() {}

            fun getCallback(): AuthUserCallback? {
                return firebaseUserCallback
            }
        }

        assertEquals(provider.getCallback(), null)

        provider.setUserCallback { _, _ -> }

        assertNotEquals(provider.getCallback(), null)

        provider.clearUserCallback()

        assertEquals(provider.getCallback(), null)
    }

}