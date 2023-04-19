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
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun createNewUserIsInCache() {
        // create a new user
        user.createNewUser(newUser).get(timeout, TimeUnit.SECONDS)
        //check if new user is correct
        assertEqualUser(newUser, user.getUser().getOrAwaitValue())
        // set to another user to evict new user from livedata
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        //check that it is the correct user
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        // set non working database
        user.setDatabase(MockNonWorkingDatabase())
        // set current user to new user fom cache
        user.setCurrentUser(newUser.getUserId()).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        //check the user in the cache
        assertEqualUser(newUser, user.getUser().getOrAwaitValue())
    }

    @Test
    fun setUsernameModifyUsername() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.updateUsername(newUser.getUsername()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newUsername = newUser.getUsername())
    }

    @Test
    fun setUsernameWhenNoInternetDoesNotModifyUsername() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.setDatabase(MockNonWorkingDatabase())
        user.updateUsername(newUser.getUsername()).exceptionally { true }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun setDistanceGoalModifyDistanceGoal() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.updateDistanceGoal(newUser.getDistanceGoal()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newDistanceGoal = newUser.getDistanceGoal())
    }

    @Test
    fun setDistanceGoalWhenNoInternetDoesNotModifyDistanceGoal() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.setDatabase(MockNonWorkingDatabase())
        user.updateDistanceGoal(newUser.getDistanceGoal()).exceptionally { true }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun setActivityTimeGoalModifyActivityTimeGoal() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.updateActivityTimeGoal(newUser.getActivityTime()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newTimeGoal = newUser.getActivityTime())
    }

    @Test
    fun setActivityTimeGoalWhenNoInternetDoesNotModifyActivityTimeGoal() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.setDatabase(MockNonWorkingDatabase())
        user.updateActivityTimeGoal(newUser.getActivityTime()).exceptionally { true }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())

    }

    @Test
    fun setNumberOfPathsGoalModifyNumberOfPathsGoal() {

        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.updateNumberOfPathsGoal(newUser.getNumberOfPathsGoal()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newPathGoal = newUser.getNumberOfPathsGoal())
    }

    @Test
    fun setNumberOfPathsGoalWhenNoInternetDoesNotModifyNumberOfPathsGoal() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.setDatabase(MockNonWorkingDatabase())
        user.updateNumberOfPathsGoal(newUser.getNumberOfPathsGoal()).exceptionally { true }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun setProfilePhotoModifyProfilePhoto() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.updateProfilePhoto(newPicture).get(timeout, TimeUnit.SECONDS)
        assertNotNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
    }

    @Test
    fun setProfilePhotoWhenNoInternetDoesNotModifyProfilePhoto() {
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        user.setDatabase(MockNonWorkingDatabase())
        user.updateProfilePhoto(newPicture).exceptionally { true }.get(timeout, TimeUnit.SECONDS)
        assertNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
    }


    @Before
    fun setup() {
        user.setDatabase(mockDataBase)
        user.clearCache().get(timeout, TimeUnit.SECONDS)
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