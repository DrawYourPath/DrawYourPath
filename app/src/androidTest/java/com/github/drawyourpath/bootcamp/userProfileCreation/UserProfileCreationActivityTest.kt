package com.github.drawyourpath.bootcamp.userProfileCreation

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.drawyourpath.bootcamp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileCreationActivityTest {
    /**
     * Test that we pass to the next correct fragment after we click on the Create your profile now button
     */
    @Test
    fun correctTransition() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        var intent = Intent(
            ApplicationProvider.getApplicationContext(),
            UserProfileCreationActivity::class.java
        )
        intent.putExtra("isRunningTestForDataBase", true)
        var t: ActivityScenario<UserProfileCreationActivity> = ActivityScenario.launch(intent)
        Espresso.onView(ViewMatchers.withId(R.id.start_profile_creation_button_userProfileCreation))
            .perform(ViewActions.click())

        //test if the correct fragment is show after clicking on VALIDATE button
        Espresso.onView(ViewMatchers.withId(R.id.userName_frame)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        t.close()
    }

}