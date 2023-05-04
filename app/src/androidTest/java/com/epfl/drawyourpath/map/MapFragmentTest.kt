package com.epfl.drawyourpath.map

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.path.Path
import com.google.android.gms.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MapFragmentTest {
    private val mockPath = Path(listOf(LatLng(0.0, 0.0), LatLng(0.0, 1.0)))

    @get:Rule
    var permissionLocation = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Map is displayed in drawing scenario
     */
    @Test
    fun checkMapDisplayedInDrawingMode() {
        val scenario = launchFragmentInContainer<MapFragment>(themeResId = R.style.Theme_Bootcamp) {
            MapFragment(focusedOnPosition = true, path = mockPath)
        }
        //check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.fragment_draw_map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    /**
     * Map is displayed in pause/end drawing scenario
     */
    @Test
    fun checkMapDisplayedInNonDrawingMode() {
        val scenario = launchFragmentInContainer<MapFragment>(themeResId = R.style.Theme_Bootcamp) {
            MapFragment(focusedOnPosition = false, path = mockPath)
        }
        //check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.fragment_draw_map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }

    /**
     * Map is displayed in the menu scenario
     */
    @Test
    fun checkMapDisplayedInMenuMode() {
        val scenario = launchFragmentInContainer<MapFragment>(themeResId = R.style.Theme_Bootcamp) {
            MapFragment(focusedOnPosition = false, path = null)
        }
        //check that the map is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapFragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.fragment_draw_map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
}
