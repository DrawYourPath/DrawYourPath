package com.epfl.drawyourpath.mainpage.fragments

import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MOCK_FORCE_SIGNED
import com.epfl.drawyourpath.authentication.USE_MOCK_AUTH
import com.epfl.drawyourpath.preferences.ModifyPasswordFragment
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith

class ToastMatcher : TypeSafeMatcher<Root>() {

    override fun describeTo(description: org.hamcrest.Description?) {
        description?.appendText("is toast")
    }

    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
            val windowToken = root.decorView.windowToken
            val appToken = root.decorView.applicationWindowToken
            if (windowToken == appToken) {
                return true
            }
        }
        return false
    }
}

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    private fun toastHasText(text: String) {
        onView(withText("Not signed in."))
    }

    @Test
    fun shouldShowErrorWhenUserNotSignedIn() {
        val scenario = launchFragmentInContainer<ModifyPasswordFragment>(
            bundleOf(
                Pair(USE_MOCK_AUTH, true),
            ),
        )

        onView(withId(R.id.BT_Apply)).perform(click())
        toastHasText("Not signed in.")
        scenario.close()
    }

    @Test
    fun emptyPasswordFailsToUpdateWithError() {
        val scenario = launchFragmentInContainer<ModifyPasswordFragment>(
            bundleOf(
                Pair(USE_MOCK_AUTH, true),
                Pair(MOCK_FORCE_SIGNED, true),
            ),
        )

        onView(withId(R.id.BT_Apply)).perform(click())
        toastHasText("Password is empty")

        scenario.close()
    }

    @Test
    fun mismatchingPasswordFailsToUpdateWithError() {
        val scenario = launchFragmentInContainer<ModifyPasswordFragment>(
            bundleOf(
                Pair(USE_MOCK_AUTH, true),
                Pair(MOCK_FORCE_SIGNED, true),
            ),
        )

        onView(withId(R.id.ET_Password)).perform(
            clearText(),
            replaceText("NewP4ss\\/\\/0rd"),
        )
        onView(withId(R.id.ET_PasswordRepeat)).perform(
            clearText(),
            replaceText("NewP3ss\\/\\/0rdfoobar"),
        )
        toastHasText("Passwords don't match")
        scenario.close()
    }

    @Test
    fun validPasswordShowsSuccessMessage() {
        val scenario = launchFragmentInContainer<ModifyPasswordFragment>(
            bundleOf(
                Pair(USE_MOCK_AUTH, true),
                Pair(MOCK_FORCE_SIGNED, true),
            ),
        )

        onView(withId(R.id.ET_Password)).perform(
            clearText(),
            replaceText("NewP4ss\\/\\/0rd"),
        )
        onView(withId(R.id.ET_PasswordRepeat)).perform(
            clearText(),
            replaceText("NewP4ss\\/\\/0rd"),
        )
        toastHasText("Passwords updated")
        scenario.close()
    }
}
