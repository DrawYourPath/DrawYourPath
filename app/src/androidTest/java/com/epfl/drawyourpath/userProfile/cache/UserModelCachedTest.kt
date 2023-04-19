package com.epfl.drawyourpath.userProfile.cache

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.database.MockNonWorkingDatabase
import com.epfl.drawyourpath.userProfile.UserModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(JUnit4::class)
class UserModelCachedTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val mockDataBase = MockDataBase()

    private val user: UserModelCached = UserModelCached(ApplicationProvider.getApplicationContext(), mockDataBase, true)

    private val testUserModel = mockDataBase.userModelTest

    private val newPicture: Bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.RGB_565)

    private val newUser = UserModel(
        "userID",
        "test@Username.com",
        "Jean",
        "Jean",
        "Michel",
        LocalDate.now().minusYears(20),
        325.0,
        100.0,
        22,
        null,
        listOf(),
        mockDataBase
    )

    private val timeout: Long = 2


    @Test
    fun getCorrectUserFromGetter() {
        user.setCurrentUser(testUserModel.getUserId()).thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun createNewUserIsInCache() {
        // create a new user
        user.createNewUser(newUser).thenApplyAsync {
            //check if new user is correct
            assertEqualUser(newUser, user.getUser().getOrAwaitValue())
        }.thenComposeAsync {
            // set to another user to evict new user from livedata
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            //check that it is the correct user
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }.thenApplyAsync {
            // set non working database
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            // set current user to new user fom cache
            user.setCurrentUser(newUser.getUserId())
        }.exceptionally {
            assertEqualUser(newUser, user.getUser().getOrAwaitValue())
            true
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setUsernameModifyUsername() {
        user.setCurrentUser(testUserModel.getUserId()).thenComposeAsync {
            user.updateUsername(newUser.getUsername())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newUsername = newUser.getUsername())
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setUsernameWhenNoInternetDoesNotModifyUsername() {
        user.setCurrentUser(testUserModel.getUserId()).thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.updateUsername(newUser.getUsername())
        }.exceptionally {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
            true
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setDistanceGoalModifyDistanceGoal() {
        user.setCurrentUser(testUserModel.getUserId()).thenComposeAsync {
            user.updateDistanceGoal(newUser.getDistanceGoal())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newDistanceGoal = newUser.getDistanceGoal())
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setDistanceGoalWhenNoInternetDoesNotModifyDistanceGoal() {
        user.setCurrentUser(testUserModel.getUserId()).thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.updateDistanceGoal(newUser.getDistanceGoal())
        }.exceptionally {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
            true
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setActivityTimeGoalModifyActivityTimeGoal() {
        user.setCurrentUser(testUserModel.getUserId()).thenComposeAsync {
            user.updateActivityTimeGoal(newUser.getActivityTime())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newTimeGoal = newUser.getActivityTime())
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setActivityTimeGoalWhenNoInternetDoesNotModifyActivityTimeGoal() {
        user.setCurrentUser(testUserModel.getUserId()).thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.updateActivityTimeGoal(newUser.getActivityTime())
        }.exceptionally {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
            true
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setNumberOfPathsGoalModifyNumberOfPathsGoal() {

        user.setCurrentUser(testUserModel.getUserId()).thenComposeAsync {
            user.updateNumberOfPathsGoal(newUser.getNumberOfPathsGoal())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newPathGoal = newUser.getNumberOfPathsGoal())
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setNumberOfPathsGoalWhenNoInternetDoesNotModifyNumberOfPathsGoal() {
        user.setCurrentUser(testUserModel.getUserId()).thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.updateNumberOfPathsGoal(newUser.getNumberOfPathsGoal())
        }.exceptionally {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
            true
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setProfilePhotoModifyProfilePhoto() {
        user.setCurrentUser(testUserModel.getUserId()).thenComposeAsync {
            user.updateProfilePhoto(newPicture)
        }.thenApplyAsync {
            assertNotNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
        }.get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setProfilePhotoWhenNoInternetDoesNotModifyProfilePhoto() {
        user.setCurrentUser(testUserModel.getUserId()).thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.updateProfilePhoto(newPicture)
        }.exceptionally {
            assertNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
            true
        }.get(timeout, TimeUnit.SECONDS)
    }


    @Before
    fun setup() {
        user.setDatabase(mockDataBase)
        user.clearCache().join()
    }

    @After
    fun clear() {
        user.clearCache().join()
    }

    private fun assertEqualUser(
        expected: UserModel,
        actual: UserEntity,
        newUsername: String = expected.getUsername(),
        newDistanceGoal: Double = expected.getDistanceGoal(),
        newTimeGoal: Double = expected.getActivityTime(),
        newPathGoal: Int = expected.getNumberOfPathsGoal()
    ) {
        assertEquals(expected.getUserId(), actual.userId)
        assertEquals(newUsername, actual.username)
        assertEquals(expected.getEmailAddress(), actual.emailAddress)
        assertEquals(expected.getFirstname(), actual.firstname)
        assertEquals(expected.getSurname(), actual.surname)
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirthAsLocalDate())
        assertEquals(newDistanceGoal, actual.distanceGoal, 0.0)
        assertEquals(newTimeGoal, actual.activityTimeGoal, 0.0)
        assertEquals(newPathGoal, actual.nbOfPathsGoal)
        assertEquals(expected.getProfilePhoto(), actual.getProfilePhotoAsBitmap())
    }

    /**
     * helper function to get result from live data
     */
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = timeout,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }

        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

}