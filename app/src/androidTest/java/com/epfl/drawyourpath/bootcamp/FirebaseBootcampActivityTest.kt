package com.epfl.drawyourpath.bootcamp

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
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FirebaseBootcampActivityTest {


    @Test
    fun setDataToFirebaseCorrectly() {
        var intent = Intent(getApplicationContext(), FirebaseBootcampActivity::class.java)
        intent.putExtra("isRunningTestForDataBase", true)
        var t: ActivityScenario<FirebaseBootcampActivity> = launch(intent)
        closeSoftKeyboard()
        onView(withId(R.id.textMailFirebase)).perform(ViewActions.typeText("hugo.hof@epfl.ch"))
        closeSoftKeyboard()
        onView(withId(R.id.textPhoneFirebase)).perform(ViewActions.typeText("12345678"))
        closeSoftKeyboard()
        onView(withId(R.id.setButtonFirebase)).perform(ViewActions.click())
        onView(withId(R.id.textMailFirebase)).perform(ViewActions.clearText())
        onView(withId(R.id.textMailFirebase)).perform(ViewActions.typeText("eric.kurmann@epfl.ch"))
        closeSoftKeyboard()
        onView(withId(R.id.textPhoneFirebase)).perform(ViewActions.clearText())
        onView(withId(R.id.textPhoneFirebase)).perform(ViewActions.typeText("987654321"))
        closeSoftKeyboard()
        onView(withId(R.id.setButtonFirebase)).perform(ViewActions.click())
        onView(withId(R.id.textMailFirebase)).perform(ViewActions.clearText())
        closeSoftKeyboard()
        onView(withId(R.id.textMailFirebase)).perform(ViewActions.typeText("hugo.hof@epfl.ch"))
        onView(withId(R.id.getButtonFirebase)).perform(ViewActions.click())
        closeSoftKeyboard()
        onView(withId(R.id.textPhoneFirebase)).check(matches(withText("12345678")))
        t.close()
    }

}