package com.epfl.drawyourpath.mainpage.fragments.runStats

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ShapePathsDescriptionFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    /**
     * Check the score recognized and the score gives to this form are correctly displayed on this fragment.
     */
    @Test
    fun correctFormScoreDisplayed() {
        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Bootcamp) {
                ShapePathDescriptionFragment(formName = "square", score = 2.0)
            }
        // check that the fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.form_path_description_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // check the form displayed
        Espresso.onView(ViewMatchers.withId(R.id.formDescriptionPath))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.shape_recognized_on_the_path_drawn)} square")))
        // check the score displayed
        Espresso.onView(ViewMatchers.withId(R.id.scorePath))
            .check(ViewAssertions.matches(ViewMatchers.withText("${context.resources.getString(R.string.score_of_the_shape_recognized)} 2.00")))
        // check the description text displayed
        Espresso.onView(ViewMatchers.withId(R.id.descriptionTextFormDescription))
            .check(ViewAssertions.matches(ViewMatchers.withText(context.resources.getString(R.string.ml_shape_recognition_description))))
        scenario.close()
    }
}
