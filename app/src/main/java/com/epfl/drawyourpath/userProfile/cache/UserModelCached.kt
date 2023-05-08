package com.epfl.drawyourpath.userProfile.cache

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.room.Room
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.*
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.path.cache.RunEntity
import com.epfl.drawyourpath.userProfile.UserProfile
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalEntity
import com.epfl.drawyourpath.utils.Utils
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * This class is persistent between fragment but not between activity.
 *
 * To use it inside fragment: `private val user: UserModelCached by activityViewModels()`
 *
 * To use it inside activity: `private val user: UserModelCached by viewModels()`
 *
 * Inside the activity, either create a new user with [createNewUser] or set to an existing user [setCurrentUser].
 *
 * For testing: either never use [createNewUser] or [setCurrentUser] or use [setDatabase] with [MockDatabase] as argument
 * both options will set the current user to a mock user with the [MockDatabase]
 *
 * @see <a href="https://github.com/DrawYourPath/DrawYourPath/pull/105">more information</a>
 */
class UserModelCached(application: Application) : AndroidViewModel(application) {

    // database where the user is store online
    private var database: Database = FirebaseDatabase()

    // room database
    private val roomDatabase = Room.databaseBuilder(application, UserDatabase::class.java, UserDatabase.NAME).fallbackToDestructiveMigration().build()

    // room database user
    private val userCache = roomDatabase.userDao()

    // room database daily goal
    private val dailyGoalCache = roomDatabase.dailyGoalDao()

    // room database runs
    private val runCache = roomDatabase.runDao()

    // current user id
    private var currentUserID: String? = null

    // current user id
    private val _currentUserID = MutableLiveData<String>()

    // user
    private val user: LiveData<UserProfile> = _currentUserID.switchMap { userCache.getUserById(it) }
        .map { entity -> entity?.let { UserProfile(it) } ?: UserProfile(UserEntity("empty user")) }

    // dailyGoal
    private val todayDailyGoal: LiveData<DailyGoal> = user.switchMap { user ->
        dailyGoalCache.getDailyGoalById(user.userId).map {
            getTodayDailyGoal(user.goals, it.firstOrNull())
        }
    }

    // runs
    private val runHistory: MutableLiveData<List<Run>> = MutableLiveData(listOf())
    // TODO change this in future task
    /*private val runHistory: LiveData<List<Run>> = _currentUserID.switchMap { runCache.getAllRunsAndPoints(it) }.map { runAndPoints ->
        runAndPoints.map { RunEntity.fromEntityToRun(it.key, it.value) }
    }*/

    /**
     * This function will create a new user
     * @param userProfile the profile of the new user
     */
    fun createNewUser(userProfile: UserProfile): CompletableFuture<Unit> {
        setUserId(userProfile.userId)
        Utils.checkNameFormat(userProfile.firstname, "firstname")
        Utils.checkNameFormat(userProfile.surname, "surname")
        Utils.checkDateOfBirth(userProfile.birthDate)
        return CompletableFuture.supplyAsync {
            userCache.insertAll(
                UserEntity(userProfile),
                listOf(DailyGoalEntity(DailyGoal(userProfile.goals), userProfile.userId)),
                listOf(),
                listOf(),
            )
        }
    }

    /**
     * set the user to an existing user
     * @param userId the user id of the new current user
     */
    fun setCurrentUser(userId: String): CompletableFuture<Unit> {
        checkCurrentUser(false)
        setUserId(userId)
        return database.getUserData(userId).thenApplyAsync { userData ->
            val runs = RunEntity.fromRunsToEntities(userData.userId ?: userId, userData.runs ?: listOf())
            userCache.insertAll(
                UserEntity(userData, userId),
                userData.dailyGoals?.map { DailyGoalEntity(it, userId) } ?: listOf(),
                runs.map { it.first },
                runs.map { it.second }.flatten(),
            )
        }
    }

    /**
     * helper function for setting the userId
     * @param userId the user id
     */
    private fun setUserId(userId: String) {
        currentUserID = userId
        _currentUserID.value = userId
    }

    /**
     * get the current user (read-only).
     *
     * Its usage is as follow : `getUser().observe(viewLifecycleOwner) { viewText.text = it.username }`
     * @return the [LiveData] of [UserEntity]
     */
    fun getUser(): LiveData<UserProfile> {
        checkCurrentUser()
        return user
    }

    /**
     * get today's daily goal (read-only).
     *
     * @return the [LiveData] of [DailyGoal]
     */
    fun getTodayDailyGoal(): LiveData<DailyGoal> {
        checkCurrentUser()
        return todayDailyGoal
    }

    /**
     * get the run history (read-only)
     *
     * @return the [liveData] of a list of [Run]
     */
    fun getRunHistory(): LiveData<List<Run>> {
        checkCurrentUser()
        CompletableFuture.supplyAsync {
            runHistory.postValue(
                runCache.getAllRunsAndPoints(currentUserID!!).map {
                    RunEntity.fromEntityToRun(it.key, it.value)
                }.sortedByDescending { it.getStartTime() },
            )
        }
        return runHistory
    }

    /**
     * get the current user id
     * @return the current user id
     */
    fun getUserId(): String? {
        return currentUserID
    }

    /**
     * set the database used for online storing (mainly used for tests)
     * @param database the database
     */
    fun setDatabase(database: Database) {
        this.database = database
        // if database is mock then set user to a test user
        if (database is MockDatabase) {
            setCurrentUser(MockDatabase.mockUser.userId!!)
        }
    }

    /**
     * This function will return a future with a boolean to know if the username is available in the database
     * (i.e the userName proposed is not already associated to another user profile)
     * @param userName userName that the user want to use for his user profile
     * @return the future that indicate if the username is available
     */
    fun isUsernameAvailable(userName: String): CompletableFuture<Boolean> {
        return database.isUsernameAvailable(userName)
    }

    /**
     * Use this function to modify the username(the username will be modify only if it is available on the database)
     * @param username that we want to set
     */
    fun updateUsername(username: String): CompletableFuture<Unit> {
        checkCurrentUser()
        return database.setUsername(currentUserID!!, username).thenApplyAsync {
            userCache.updateUsername(currentUserID!!, username)
        }
    }

    /**
     * Use this function to modify the daily goals of the user
     * @param goals the goals to add
     */
    fun updateGoals(goals: UserGoals): CompletableFuture<Unit> {
        checkCurrentUser()
        Utils.checkGoals(goals)
        return database.setGoals(currentUserID!!, goals)
            .thenApplyAsync {
                dailyGoalCache.updateGoals(
                    currentUserID!!,
                    LocalDate.now().toEpochDay(),
                    goals,
                )
            }.thenComposeAsync {
                database.addDailyGoal(currentUserID!!, DailyGoal(it))
            }
    }

    /**
     * TODO add run from cache to database when connection
     * add a new run to the run history and update the daily goal
     * @param run the run to add
     */
    fun addNewRun(run: Run): CompletableFuture<Unit> {
        Log.d("RunRepository", "addNewRun!!")
        checkCurrentUser()
        val distanceInKilometer: Double = run.getDistance() / 1000.0
        val timeInMinute: Double = run.getDuration() / 60.0
        val date = LocalDate.now().toEpochDay()
        val future = CompletableFuture.supplyAsync {
            val runs = RunEntity.fromRunsToEntities(currentUserID!!, listOf(run), false)
            dailyGoalCache.addRunAndUpdateProgress(currentUserID!!, date, distanceInKilometer, timeInMinute, 1, runs[0].first, runs[0].second)
        }
        future.thenComposeAsync {
            database.addDailyGoal(currentUserID!!, DailyGoal(it))
        }.thenComposeAsync {
            database.addRunToHistory(currentUserID!!, run)
        }.thenApplyAsync {
            runCache.runSynced(currentUserID!!, run.getStartTime())
        } /*.thenComposeAsync { TODO add again when in database
            database.updateUserAchievements(currentUserID!!, distanceInKilometer, timeInMinute)
        }*/
        return future.thenApply {}
    }

    /**
     * This function will set the photo as the profile photo of the user
     * @param photo that we want to set
     * @return a completable future that indicate if the photo was correctly stored
     */
    fun updateProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        checkCurrentUser()
        return database.setProfilePhoto(currentUserID!!, photo).thenApplyAsync {
            userCache.updatePhoto(currentUserID!!, Utils.encodePhotoToByteArray(photo))
        }
    }

    /**
     * This function will clear the room database
     */
    fun clearCache(): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync {
            userCache.clear()
        }
    }

    /**
     * check if the current user is normal otherwise it is a mock test
     * @param checkIfNull check if the user is null or not
     */
    private fun checkCurrentUser(checkIfNull: Boolean = true) {
        if (database is MockDatabase || database is MockNonWorkingDatabase) {
            return // already a test
        }
        if ((checkIfNull && currentUserID == null) || currentUserID == MockAuth.MOCK_USER.getUid()) {
            // if current user null then it is a test
            setDatabase(MockDatabase())
            Toast.makeText(getApplication(), R.string.toast_test_error_message, Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * get Today's Daily Goal or create a new one if there is no Daily Goals for today
     *
     * @return DailyGoal representing today's Daily Goal
     */
    private fun getTodayDailyGoal(
        goals: UserProfile.Goals,
        entity: DailyGoalEntity?,
    ): DailyGoal {
        if (entity == null || LocalDate.ofEpochDay(entity.date) != LocalDate.now()) {
            val dailyGoal = DailyGoal(
                goals.distanceGoal,
                goals.activityTimeGoal,
                goals.pathsGoal,
            )
            database.addDailyGoal(currentUserID!!, dailyGoal).thenApplyAsync {
                dailyGoalCache.insertDailyGoal(DailyGoalEntity(dailyGoal, currentUserID!!))
            }
            return dailyGoal
        }
        return DailyGoal(entity)
    }
}
