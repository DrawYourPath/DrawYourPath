package com.epfl.drawyourpath.pathDrawing

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PathDrawingMainFragmentTest {
    private val mockPath = Path(listOf(LatLng(1.0, 1.0), LatLng(1.0, 0.0), LatLng(0.0, 0.0)))
    private val mockRun = Run(path = mockPath, startTime = 110L, endTime = 120L)

    @Test
    fun checkInformationDisplayedInDrawingMode() {
        val scenario = launchFragmentInContainer<PathDrawingMainFragment>(themeResId = R.style.Theme_Bootcamp) {
            PathDrawingMainFragment(run = mockRun, isDrawing = true)
        }
        // check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }
}
