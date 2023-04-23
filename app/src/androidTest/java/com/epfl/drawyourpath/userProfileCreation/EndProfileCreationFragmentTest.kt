package com.epfl.drawyourpath.userProfileCreation

import android.content.Intent
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndProfileCreationFragmentTest : Fragment() {

    /**
     * Test that the correct text is printed on the screen with the correct username inside the text
     */
    @Test
    fun correctEndMeassageOutput() {
        var t = goToProfilePhotoInitFragment()
        val correctMessage =
            "We are happy to welcome you, "

        // test if the correct fragment is show after clicking on VALIDATE button
        Espresso.onView(withId(R.id.endProfileCreation_text_userProfileCreation))
            .check(ViewAssertions.matches(withSubstring(correctMessage)))

        t.close()
    }
}

/**
 * Helper function to go from the UserProfileCreation activity to the PersonalInfoFragment in the UI
 * and select the Mock Database for the tests.
 */
private fun goToProfilePhotoInitFragment(): FragmentScenario<EndProfileCreationFragment> {
    return launchFragmentInContainer(
        bundleOf(
            PROFILE_TEST_KEY to true
        )
    )
}
