package com.github.drawyourpath.bootcamp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoredDisplayActivityTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
    }

    @Test
    fun testSuccessfulResponse() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("success_response.json"))
            }
        }
        var intent = Intent(ApplicationProvider.getApplicationContext(), BoredDisplayActivity::class.java)
        intent.putExtra("testUrl", "http://127.0.0.1:8080")
        var boredDisplay: ActivityScenario<BoredDisplayActivity> = ActivityScenario.launch(intent)
        onView(withId(R.id.boredActivityButton)).perform(click())
        onView(withId(R.id.boredActivityDisplay)).check(matches(withText("Go to the library and find an interesting book\nType : relaxation\n1 participants")))
        boredDisplay.close()
    }

    @Test
    fun testEmptyCacheWithUnsuccessfulResponse() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(404)
            }
        }
        var intent = Intent(ApplicationProvider.getApplicationContext(), BoredDisplayActivity::class.java)
        intent.putExtra("testUrl", "http://127.0.0.1:8080")
        var boredDisplay: ActivityScenario<BoredDisplayActivity> = ActivityScenario.launch(intent)
        onView(withId(R.id.boredActivityClearButton)).perform(click())
        onView(withId(R.id.boredActivityButton)).perform(click())
        onView(withId(R.id.boredActivityDisplay)).check(matches(withText("failed to retrieve activity")))
        boredDisplay.close()
    }

    @Test
    fun testCacheWithUnsuccessfulResponse() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("success_response.json"))
            }
        }
        var intent = Intent(ApplicationProvider.getApplicationContext(), BoredDisplayActivity::class.java)
        intent.putExtra("testUrl", "http://127.0.0.1:8080")
        var boredDisplay: ActivityScenario<BoredDisplayActivity> = ActivityScenario.launch(intent)
        onView(withId(R.id.boredActivityClearButton)).perform(click())
        onView(withId(R.id.boredActivityButton)).perform(click())
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(404)
            }
        }
        onView(withId(R.id.boredActivityButton)).perform(click())
        onView(withId(R.id.boredActivityDisplay)).check(matches(withText("Go to the library and find an interesting book\nType : relaxation\n1 participants\nFrom Cache")))
        boredDisplay.close()
    }




    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

}