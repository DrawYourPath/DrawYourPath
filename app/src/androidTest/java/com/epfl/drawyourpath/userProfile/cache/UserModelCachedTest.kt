package com.epfl.drawyourpath.userProfile.cache

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.database.MockNonWorkingDatabase
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.google.android.gms.maps.model.LatLng
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

    private val user: UserModelCached = UserModelCached(ApplicationProvider.getApplicationContext())

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
        listOf(),
        mockDataBase,
    )

    private val dailyGoal =
        DailyGoal(testUserModel.getCurrentDistanceGoal(), testUserModel.getCurrentActivityTime(), testUserModel.getCurrentNumberOfPathsGoal())

    private val run =
        Run(Path(listOf(LatLng(46.518493105924385, 6.561726074747257), LatLng(46.50615811055845, 6.620565690839656))), 0, 1286)

    private val timeout: Long = 5

    @Test
    fun getCorrectUserFromGetter() {
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun getCorrectDailyGoalFromGetter() {
        assertEquals(dailyGoal, user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun createNewUserIsInCache() {
        // create a new user
        user.createNewUser(newUser).get(timeout, TimeUnit.SECONDS)
        // check if new user is correct
        assertEqualUser(newUser, user.getUser().getOrAwaitValue())
        // set to another user to evict new user from livedata
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
        // check that it is the correct user
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        // set non working database
        user.setDatabase(MockNonWorkingDatabase())
        // set current user to new user fom cache
        user.setCurrentUser(newUser.getUserId()).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        // check the user in the cache
        assertEqualUser(newUser, user.getUser().getOrAwaitValue())
    }

    @Test
    fun setUsernameModifyUsername() {
        user.updateUsername(newUser.getUsername()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newUsername = newUser.getUsername())
    }

    @Test
    fun setUsernameWhenNoInternetDoesNotModifyUsername() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateUsername(newUser.getUsername()).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun setDistanceGoalModifyDistanceGoal() {
        user.updateDistanceGoal(newUser.getCurrentDistanceGoal()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newDistanceGoal = newUser.getCurrentDistanceGoal())
        assertEquals(dailyGoal.copy(distanceInKilometerGoal = newUser.getCurrentDistanceGoal()), user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setDistanceGoalWhenNoInternetDoesNotModifyDistanceGoal() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateDistanceGoal(newUser.getCurrentDistanceGoal()).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(dailyGoal, user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setActivityTimeGoalModifyActivityTimeGoal() {
        user.updateActivityTimeGoal(newUser.getCurrentActivityTime()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newTimeGoal = newUser.getCurrentActivityTime())
        assertEquals(dailyGoal.copy(activityTimeInMinutesGoal = newUser.getCurrentActivityTime()), user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setActivityTimeGoalWhenNoInternetDoesNotModifyActivityTimeGoal() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateActivityTimeGoal(newUser.getCurrentActivityTime()).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(dailyGoal, user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setNumberOfPathsGoalModifyNumberOfPathsGoal() {
        user.updateNumberOfPathsGoal(newUser.getCurrentNumberOfPathsGoal()).get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newPathGoal = newUser.getCurrentNumberOfPathsGoal())
        assertEquals(dailyGoal.copy(nbOfPathsGoal = newUser.getCurrentNumberOfPathsGoal()), user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setNumberOfPathsGoalWhenNoInternetDoesNotModifyNumberOfPathsGoal() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateNumberOfPathsGoal(newUser.getCurrentNumberOfPathsGoal()).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(dailyGoal, user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setNewProgressModifyProgress() {
        user.updateGoalProgress(run).get(timeout, TimeUnit.SECONDS)
        val distance = run.getDistance() / 1000.0
        val time = run.getDuration() / 60.0
        assertEqualUser(
            testUserModel,
            user.getUser().getOrAwaitValue(),
            addDistanceProgress = distance,
            addTimeProgress = time,
            addPathProgress = 1,
        )
        assertEquals(
            dailyGoal.copy(distanceInKilometerProgress = distance, activityTimeInMinutesProgress = time, nbOfPathsProgress = 1),
            user.getTodayDailyGoal().getOrAwaitValue(),
        )
    }

    @Test
    fun setNewProgressWhenNoInternetDoesNotModifyProgress() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateGoalProgress(run).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(dailyGoal, user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setProfilePhotoModifyProfilePhoto() {
        user.updateProfilePhoto(newPicture).get(timeout, TimeUnit.SECONDS)
        assertNotNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
    }

    @Test
    fun setProfilePhotoWhenNoInternetDoesNotModifyProfilePhoto() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateProfilePhoto(newPicture).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        assertNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
    }

    @Before
    fun setup() {
        user.setDatabase(mockDataBase)
        user.clearCache().get(timeout, TimeUnit.SECONDS)
        user.setCurrentUser(testUserModel.getUserId()).get(timeout, TimeUnit.SECONDS)
    }

    private fun assertEqualUser(
        expected: UserModel,
        actual: UserEntity,
        newUsername: String = expected.getUsername(),
        newDistanceGoal: Double = expected.getCurrentDistanceGoal(),
        newTimeGoal: Double = expected.getCurrentActivityTime(),
        newPathGoal: Int = expected.getCurrentNumberOfPathsGoal(),
        addDistanceProgress: Double = 0.0,
        addTimeProgress: Double = 0.0,
        addPathProgress: Int = 0,
    ) {
        assertEquals(expected.getUserId(), actual.userId)
        assertEquals(newUsername, actual.username)
        assertEquals(expected.getEmailAddress(), actual.emailAddress)
        assertEquals(expected.getFirstname(), actual.firstname)
        assertEquals(expected.getSurname(), actual.surname)
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirthAsLocalDate())
        assertEquals(newDistanceGoal, actual.goalAndAchievements.distanceGoal, 0.0)
        assertEquals(newTimeGoal, actual.goalAndAchievements.activityTimeGoal, 0.0)
        assertEquals(newPathGoal, actual.goalAndAchievements.nbOfPathsGoal)
        assertEquals(expected.getTotalDistance() + addDistanceProgress, actual.goalAndAchievements.totalDistance, 0.001)
        assertEquals(expected.getTotalActivityTime() + addTimeProgress, actual.goalAndAchievements.totalActivityTime, 0.001)
        assertEquals(expected.getTotalNbOfPaths() + addPathProgress, actual.goalAndAchievements.totalNbOfPaths)
        assertEquals(expected.getProfilePhoto(), actual.getProfilePhotoAsBitmap())
    }

    /**
     * helper function to get result from live data
     */
    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = timeout,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
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
