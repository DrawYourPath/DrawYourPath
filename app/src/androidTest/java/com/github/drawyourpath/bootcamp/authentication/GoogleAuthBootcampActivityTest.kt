package com.github.drawyourpath.bootcamp.authentication

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.drawyourpath.bootcamp.R

@RunWith(AndroidJUnit4::class)
class GoogleAuthBootcampActivityTest {
    @Test
    fun singleSignInShowsUsername() {
        val intent = Intent(getApplicationContext(), GoogleAuthBootcampActivity::class.java);

        intent.putExtra(GA_USE_MOCK_AUTH_PROVIDER_KEY, true);

        val scenario: ActivityScenario<GoogleAuthBootcampActivity> = launch(intent)

        onView(withId(R.id.bc_google_signin)).perform(ViewActions.click())
        onView(withId(R.id.sign_in_status)).check(matches(ViewMatchers.withSubstring("John Doe")))

        scenario.close();
    }
}