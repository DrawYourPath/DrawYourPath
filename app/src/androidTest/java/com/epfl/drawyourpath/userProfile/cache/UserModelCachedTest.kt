package com.epfl.drawyourpath.userProfile.cache

import android.app.Application
import android.graphics.Bitmap
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.database.MockNonWorkingDatabase
import com.epfl.drawyourpath.database.UserData
import com.epfl.drawyourpath.database.UserGoals
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserProfile
import com.epfl.drawyourpath.utils.Utils
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

    @get:Rule
    var counting = CountingTaskExecutorRule()

    private val applicationContext: Application = ApplicationProvider.getApplicationContext()

    private val mockDataBase = MockDatabase()

    private val user: UserModelCached = UserModelCached(applicationContext)

    private val testUserModel = MockDatabase.mockUser

    private val newPicture: Bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.RGB_565)

    private val newUser = UserData(
        "Jean",
        "userid",
        "Jean",
        "Michel",
        LocalDate.now().minusYears(20).toEpochDay(),
        "test@email.com",
        UserGoals(22, 325.0, 100.0),
        null,
        listOf(),
        listOf(
            Run(
                Path(listOf(listOf(LatLng(46.518493105924385, 6.561726074747257), LatLng(46.50615811055845, 6.620565690839656)))),
                100 + 10,
                duration = 1286,
                100 + 10 + 1286,
                "goalpost",
                0.698532,
            ),
        ),
        listOf(
            DailyGoal(
                testUserModel.goals!!.distance!!,
                testUserModel.goals!!.activityTime!!,
                testUserModel.goals!!.paths!!.toInt(),
            ),
        ),
    )

    private val timeout: Long = 5

    private fun waitUntilAllThreadAreDone() {
        counting.drainTasks(timeout.toInt(), TimeUnit.SECONDS)
        Thread.sleep(10)
    }

    @Test
    fun getCorrectUserFromGetter() {
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test
    fun getCorrectDailyGoalFromGetter() {
        assertEquals(newUser.dailyGoals!![0], user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun createNewUserIsInCache() {
        // create a new user
        user.createNewUser(UserProfile(newUser)).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        // check if new user is correct
        assertEqualUser(newUser, user.getUser().getOrAwaitValue())
        // set to another user to evict new user from livedata
        user.setCurrentUser(testUserModel.userId!!).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        // check that it is the correct user
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEqualRun(testUserModel.runs!!.sortedByDescending { it.getStartTime() }, user.getRunHistory().getOrAwaitValue())
        // set non working database
        user.setDatabase(MockNonWorkingDatabase())
        // set current user to new user fom cache
        user.setCurrentUser(newUser.userId!!).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        // check the user in the cache
        assertEqualUser(newUser, user.getUser().getOrAwaitValue())
    }

    @Test(expected = Error::class)
    fun createUserWithInvalidFirstnameThrowError() {
        user.createNewUser(UserProfile(newUser).copy(firstname = "c moi")).get(timeout, TimeUnit.SECONDS)
    }

    @Test(expected = Error::class)
    fun createUserWithInvalidSurnameThrowError() {
        user.createNewUser(UserProfile(newUser).copy(surname = "c moi")).get(timeout, TimeUnit.SECONDS)
    }

    @Test(expected = Error::class)
    fun createUserWithInvalidBrithDateThrowError() {
        user.createNewUser(UserProfile(newUser).copy(birthDate = LocalDate.now().minusYears(3))).get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setUsernameModifyUsername() {
        user.updateUsername(newUser.username!!).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newUsername = newUser.username!!)
    }

    @Test
    fun setUsernameWhenNoInternetDoesNotModifyUsername() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateUsername(newUser.username!!).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
    }

    @Test(expected = Error::class)
    fun setInvalidDistanceGoalThrowError() {
        user.updateGoals(UserGoals(distance = -1.0)).get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setDistanceGoalModifyDistanceGoal() {
        user.updateGoals(UserGoals(distance = newUser.goals!!.distance!!)).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newDistanceGoal = newUser.goals!!.distance!!)
        assertEquals(newUser.dailyGoals!![0].copy(expectedDistance = newUser.goals!!.distance!!), user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setDistanceGoalWhenNoInternetDoesNotModifyDistanceGoal() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateGoals(UserGoals(distance = newUser.goals!!.distance!!)).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(newUser.dailyGoals!![0], user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test(expected = Error::class)
    fun setInvalidActivityTimeGoalThrowError() {
        user.updateGoals(UserGoals(activityTime = -1.0)).get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setActivityTimeGoalModifyActivityTimeGoal() {
        user.updateGoals(UserGoals(activityTime = newUser.goals!!.activityTime!!)).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newTimeGoal = newUser.goals!!.activityTime!!)
        assertEquals(newUser.dailyGoals!![0].copy(expectedTime = newUser.goals!!.activityTime!!), user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setActivityTimeGoalWhenNoInternetDoesNotModifyActivityTimeGoal() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateGoals(UserGoals(activityTime = newUser.goals!!.activityTime!!)).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(newUser.dailyGoals!![0], user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test(expected = Error::class)
    fun setInvalidNumberOfPathThrowError() {
        user.updateGoals(UserGoals(paths = -1)).get(timeout, TimeUnit.SECONDS)
    }

    @Test
    fun setNumberOfPathsGoalModifyNumberOfPathsGoal() {
        user.updateGoals(UserGoals(paths = newUser.goals!!.paths!!)).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newPathGoal = newUser.goals!!.paths!!.toInt())
        assertEquals(newUser.dailyGoals!![0].copy(expectedPaths = newUser.goals!!.paths!!.toInt()), user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun setNumberOfPathsGoalWhenNoInternetDoesNotModifyNumberOfPathsGoal() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateGoals(UserGoals(paths = newUser.goals!!.paths!!)).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(newUser.dailyGoals!![0], user.getTodayDailyGoal().getOrAwaitValue())
    }

    @Test
    fun addNewRunAddRunAndModifyProgress() {
        val run = newUser.runs!![0]
        user.addNewRun(run).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        val distance = run.getDistance() / 1000.0
        val time = run.getDuration() / 60.0
        assertEqualUser(
            testUserModel,
            user.getUser().getOrAwaitValue(),
        )
        assertEquals(
            newUser.dailyGoals!![0].copy(distance = distance, time = time, paths = 1),
            user.getTodayDailyGoal().getOrAwaitValue(),
        )
        assertEqualRun(testUserModel.runs!!.toMutableList().also { it.add(run) }.sortedByDescending { it.getStartTime() }, user.getRunHistory().getOrAwaitValue())
    }

    @Test
    fun addNewRunWhenNoInternetDoesAddRunAndModifyProgress() {
        user.setDatabase(MockNonWorkingDatabase())
        val run = newUser.runs!![0]
        user.addNewRun(run).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        val distance = run.getDistance() / 1000.0
        val time = run.getDuration() / 60.0
        assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        assertEquals(
            newUser.dailyGoals!![0].copy(distance = distance, time = time, paths = 1),
            user.getTodayDailyGoal().getOrAwaitValue(),
        )
        assertEqualRun(testUserModel.runs!!.toMutableList().also { it.add(run) }.sortedByDescending { it.getStartTime() }, user.getRunHistory().getOrAwaitValue())
    }

    @Test
    fun setProfilePhotoModifyProfilePhoto() {
        user.updateProfilePhoto(newPicture).get(timeout, TimeUnit.SECONDS)
        val compressedPhoto = Utils.decodePhotoOrGetDefault(Utils.encodePhotoToByteArray(newPicture), applicationContext.resources)
        waitUntilAllThreadAreDone()
        assert(user.getUser().getOrAwaitValue().profilePhoto(applicationContext.resources).sameAs(compressedPhoto))
    }

    @Test
    fun setProfilePhotoWhenNoInternetDoesNotModifyProfilePhoto() {
        user.setDatabase(MockNonWorkingDatabase())
        user.updateProfilePhoto(newPicture).exceptionally { }.get(timeout, TimeUnit.SECONDS)
        val compressedPhoto = Utils.decodePhotoOrGetDefault(Utils.encodePhotoToByteArray(newPicture), applicationContext.resources)
        waitUntilAllThreadAreDone()
        assert(!user.getUser().getOrAwaitValue().profilePhoto(applicationContext.resources).sameAs(compressedPhoto))
    }

    @Before
    fun setup() {
        user.setDatabase(mockDataBase)
        user.clearCache().get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
        user.setCurrentUser(testUserModel.userId!!).get(timeout, TimeUnit.SECONDS)
        waitUntilAllThreadAreDone()
    }

    private fun assertEqualUser(
        expected: UserData,
        actual: UserProfile,
        newUsername: String = expected.username!!,
        newDistanceGoal: Double = expected.goals!!.distance!!,
        newTimeGoal: Double = expected.goals!!.activityTime!!,
        newPathGoal: Int = expected.goals!!.paths!!.toInt(),
    ) {
        assertEquals(expected.userId!!, actual.userId)
        assertEquals(newUsername, actual.username)
        assertEquals(expected.email!!, actual.emailAddress)
        assertEquals(expected.firstname!!, actual.firstname)
        assertEquals(expected.surname!!, actual.surname)
        assertEquals(expected.birthDate!!, actual.birthDate.toEpochDay())
        assertEquals(newDistanceGoal, actual.goals.distanceGoal, 0.0)
        assertEquals(newTimeGoal, actual.goals.activityTimeGoal, 0.0)
        assertEquals(newPathGoal, actual.goals.pathsGoal)
    }

    private fun assertEqualRun(expected: List<Run>, actual: List<Run>) {
        expected.forEachIndexed { index, run ->
            assertEquals(run.getStartTime(), actual[index].getStartTime())
            assertEquals(run.getDuration(), actual[index].getDuration())
            assertEquals(run.getEndTime(), actual[index].getEndTime())
            assertEqualPath(run.getPath(), actual[index].getPath())
            assertEquals(run.predictedShape, actual[index].predictedShape)
            assertEquals(run.similarityScore, actual[index].similarityScore, 0.001)
        }
    }

    private fun assertEqualPath(expected: Path, actual: Path) {
        assertEquals(expected.getPoints(), actual.getPoints())
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
