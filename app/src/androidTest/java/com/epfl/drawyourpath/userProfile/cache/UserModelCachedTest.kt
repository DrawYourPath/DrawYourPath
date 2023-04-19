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
import java.util.concurrent.CompletableFuture
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


    @Test
    fun getCorrectUserFromGetter() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }
    }

    @Test
    fun createNewUserIsInCache() {
        CompletableFuture.supplyAsync {
            // create a new user
            user.createNewUser(newUser)
        }.thenApplyAsync {
            //check if new user is correct
            assertEqualUser(newUser, user.getUser().getOrAwaitValue())
        }.thenApplyAsync {
            // set to another user to evict new user from livedata
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            //check that it is the correct user
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }.thenApplyAsync {
            // set non working database
            user.setDatabase(MockNonWorkingDatabase())
        }.thenApplyAsync {
            // set current user to new user fom cache
            user.setCurrentUser(newUser.getUserId())
        }.thenApplyAsync {
            //check if current user is in cache
            assertEqualUser(newUser, user.getUser().getOrAwaitValue())
        }
    }

    @Test
    fun setUsernameModifyUsername() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenComposeAsync {
            user.setUsername(newUser.getUsername())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newUsername = newUser.getUsername())
        }
    }

    @Test
    fun setUsernameWhenNoInternetDoesNotModifyUsername() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.setUsername(newUser.getUsername())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }
    }

    @Test
    fun setDistanceGoalModifyDistanceGoal() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenComposeAsync {
            user.setDistanceGoal(newUser.getDistanceGoal())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newDistanceGoal = newUser.getDistanceGoal())
        }
    }

    @Test
    fun setDistanceGoalWhenNoInternetDoesNotModifyDistanceGoal() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.setDistanceGoal(newUser.getDistanceGoal())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }
    }

    @Test
    fun setActivityTimeGoalModifyActivityTimeGoal() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenComposeAsync {
            user.setActivityTimeGoal(newUser.getActivityTime())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newTimeGoal = newUser.getActivityTime())
        }
    }

    @Test
    fun setActivityTimeGoalWhenNoInternetDoesNotModifyActivityTimeGoal() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.setActivityTimeGoal(newUser.getActivityTime())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }
    }

    @Test
    fun setNumberOfPathsGoalModifyNumberOfPathsGoal() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenComposeAsync {
            user.setNumberOfPathsGoal(newUser.getNumberOfPathsGoal())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue(), newPathGoal = newUser.getNumberOfPathsGoal())
        }
    }

    @Test
    fun setNumberOfPathsGoalWhenNoInternetDoesNotModifyNumberOfPathsGoal() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.setNumberOfPathsGoal(newUser.getNumberOfPathsGoal())
        }.thenApplyAsync {
            assertEqualUser(testUserModel, user.getUser().getOrAwaitValue())
        }
    }

    @Test
    fun setProfilePhotoModifyProfilePhoto() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenComposeAsync {
            user.setProfilePhoto(newPicture)
        }.thenApplyAsync {
            assertNotNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
        }
    }

    @Test
    fun setProfilePhotoWhenNoInternetDoesNotModifyProfilePhoto() {
        CompletableFuture.supplyAsync {
            user.setCurrentUser(testUserModel.getUserId())
        }.thenApplyAsync {
            user.setDatabase(MockNonWorkingDatabase())
        }.thenComposeAsync {
            user.setProfilePhoto(newPicture)
        }.thenApplyAsync {
           assertNull(user.getUser().getOrAwaitValue().getProfilePhotoAsBitmap())
        }
    }


    @Before
    fun setup() {
        user.clearCache()
    }

    @After
    fun clear() {
        user.clearCache()
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
        time: Long = 10,
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