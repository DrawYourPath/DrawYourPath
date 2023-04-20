package com.epfl.drawyourpath.qr

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.login.LoginActivity
import com.epfl.drawyourpath.qrcode.QRScannerActivity
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.qrcode.SCANNER_ACTIVITY_RESULT_CODE
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`


@RunWith(AndroidJUnit4::class)
class QRScannerTests {

    fun launchQRScannerActivity(): ActivityScenario<LoginActivity> {
        val intent = Intent(ApplicationProvider.getApplicationContext(), QRScannerActivity::class.java)
        return launch(intent)
    }

    @Test
    fun generalLayoutMatchesExpectation() {
        val scenario = launchQRScannerActivity()

        onView(withId(R.id.BT_Cancel)).check(matches(isDisplayed()))
        onView(withId(R.id.SV_Scanner)).check(matches(isDisplayed()))
    }

    @Test
    fun activityClosesOnCancelClicked() {
        val scenario = launchQRScannerActivity()

        onView(withId(R.id.BT_Cancel)).perform(click())
        assertThat(scenario.result.resultCode, `is`(SCANNER_ACTIVITY_RESULT_CODE))
    }

}