package com.github.drawyourpath.bootcamp.userProfileCreation

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith
import com.github.drawyourpath.bootcamp.R

@RunWith(AndroidJUnit4::class)
class UserProfileCreationActvityTest {


    @Test
    fun userIDUnAvailableIsPrintUnAvailable() {
        //pass in test mode to used the Mockdatabase instead of the Firebase
        var intent = Intent(getApplicationContext(), UserProfileCreationActivity::class.java)
        intent.putExtra("isRunningTestForDataBase", true)
        var t: ActivityScenario<UserProfileCreationActivity> = launch(intent)
        onView(withId(R.id.start_profile_creation_button_userProfileCreation)).perform(ViewActions.click())
        onView(withId(R.id.input_userName_text_UserProfileCreation)).perform(ViewActions.typeText("albert"))
        closeSoftKeyboard()
        onView(withId(R.id.testUserName_button_userProfileCreation)).perform(ViewActions.click())

        //test if the text printed is correct
        onView(withId(R.id.testUserName_text_userProfileCreation)).check(matches(withText("*The userID albert is NOT available !")))

        t.close()

    }

}