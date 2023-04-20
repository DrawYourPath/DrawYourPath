package com.epfl.drawyourpath.userProfile.cache

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.*
import androidx.room.Room
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.database.MockNonWorkingDatabase
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoalEntity
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
 * For testing: either never use [createNewUser] or [setCurrentUser] or use [setDatabase] with [MockDataBase] as argument
 * both options will set the current user to a mock user with the [MockDataBase]
 *
 * @see <a href="https://github.com/DrawYourPath/DrawYourPath/pull/105">more information</a>
 */
class UserModelCached(application: Application) : AndroidViewModel(application) {

    //database where the user is store online
    private var database: Database = FireDatabase()

    // room database
    private val roomDatabase = Room
        .databaseBuilder(application, UserDatabase::class.java, UserDatabase.NAME)
        .fallbackToDestructiveMigration()
        .build()

    // room database user
    private val userCache = roomDatabase.userDao()

    // room database daily goal
    private val dailyGoalCache = roomDatabase.dailyGoalDao()

    // current user id
    private var currentUserID: String? = null

    // current user id
    private val _currentUserID = MutableLiveData<String>()

    // user
    private val user: LiveData<UserEntity> = _currentUserID.switchMap { userCache.getUserById(it)}.map { user -> user?: UserEntity("userID") }

    // dailyGoal
    private val todayDailyGoal: LiveData<DailyGoal> = user.switchMap { user ->
        dailyGoalCache.getDailyGoalById(user.userId).map {
            getTodayDailyGoal(user.goalAndAchievements, it.firstOrNull())
        }
    }

    /**
     * This function will create a new user based on the user model of the app
     * @param userModel the userModel to create the new user
     */
    fun createNewUser(userModel: UserModel): CompletableFuture<Unit> {
        setUserId(userModel.getUserId())
        return CompletableFuture.supplyAsync {
            userCache.insertAll(
                fromUserModelToUserData(userModel),
                listOf(
                    DailyGoal(
                        userModel.getCurrentDistanceGoal(),
                        userModel.getCurrentActivityTime(),
                        userModel.getCurrentNumberOfPathsGoal()
                    ).toDailyGoalEntity(userModel.getUserId())
                )
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
        return CompletableFuture.supplyAsync {
            // insert empty user in cache to avoid null user
            userCache.insertUserIfEmpty(UserEntity(userId))
        }.thenComposeAsync {
            database.getUserAccount(userId)
        }.thenApplyAsync { userModel ->
            userCache.insertAll(fromUserModelToUserData(userModel), userModel.getDailyGoalList().map { it.toDailyGoalEntity(userId) })
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
    fun getUser(): LiveData<UserEntity> {
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
        if (database is MockDataBase) {
            setCurrentUser(database.userIdTest)
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
        return database.updateUsername(username).thenApplyAsync {
            userCache.updateUsername(currentUserID!!, username)
        }
    }


    /**
     * Use this function to modify the daily distance goal of the user
     * @param distanceGoal new daily distance goal
     */
    fun updateDistanceGoal(distanceGoal: Double): CompletableFuture<Unit> {
        checkCurrentUser()
        UserModel.checkDistanceGoal(distanceGoal)
        return database.setCurrentDistanceGoal(distanceGoal).thenApplyAsync {
            dailyGoalCache.updateDistanceGoal(currentUserID!!, LocalDate.now().toEpochDay(), distanceGoal)
        }.thenComposeAsync {
            database.addDailyGoal(DailyGoal(it))
        }
    }

    /**
     * Use this function to modify the daily activity time goal of the user
     * @param activityTimeGoal new daily activity time goal
     */
    fun updateActivityTimeGoal(activityTimeGoal: Double): CompletableFuture<Unit> {
        checkCurrentUser()
        UserModel.checkActivityTimeGoal(activityTimeGoal)
        return database.setCurrentActivityTimeGoal(activityTimeGoal).thenApplyAsync {
            dailyGoalCache.updateTimeGoal(currentUserID!!, LocalDate.now().toEpochDay(), activityTimeGoal)
        }.thenComposeAsync {
            database.addDailyGoal(DailyGoal(it))
        }
    }

    /**
     * Use this function to modify the daily number of paths goal of the user
     * @param nbOfPathsGoal new daily number of paths goal
     */
    fun updateNumberOfPathsGoal(nbOfPathsGoal: Int): CompletableFuture<Unit> {
        checkCurrentUser()
        UserModel.checkNbOfPathsGoal(nbOfPathsGoal)
        return database.setCurrentNbOfPathsGoal(nbOfPathsGoal).thenApplyAsync {
            dailyGoalCache.updatePathsGoal(currentUserID!!, LocalDate.now().toEpochDay(), nbOfPathsGoal)
        }.thenComposeAsync {
            database.addDailyGoal(DailyGoal(it))
        }
    }

    /**
     * update the goal progress from a run
     * @param run the run to add
     */
    fun updateGoalProgress(run: Run): CompletableFuture<Unit> {
        checkCurrentUser()
        val distanceInKilometer: Double = run.getDistance() / 1000.0
        val timeInMinute: Double = run.getDuration() / 60.0
        val date = LocalDate.now().toEpochDay()
        return database.updateUserAchievements(distanceInKilometer, timeInMinute).thenApplyAsync {
            dailyGoalCache.updateProgress(currentUserID!!, date, distanceInKilometer, timeInMinute, 1)
        }.thenComposeAsync {
            database.addDailyGoal(DailyGoal(it))
        }
    }

    /**
     * This function will set the photo as the profile photo of the user
     * @param photo that we want to set
     * @return a completable future that indicate if the photo was correctly stored
     */
    fun updateProfilePhoto(photo: Bitmap): CompletableFuture<Unit> {
        checkCurrentUser()
        return database.setProfilePhoto(photo).thenApplyAsync {
            userCache.updatePhoto(currentUserID!!, UserEntity.fromBitmapToByteArray(photo))
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
        if (database is MockDataBase || database is MockNonWorkingDatabase) {
            return //already a test
        }
        if ((checkIfNull && currentUserID == null) || currentUserID == MockAuth.MOCK_USER.getUid()) {
            // if current user null then it is a test
            setDatabase(MockDataBase())
            Toast.makeText(getApplication(), R.string.toast_test_error_message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * get Today's Daily Goal or create a new one if there is no Daily Goals for today
     *
     * @return DailyGoal representing today's Daily Goal
     */
    private fun getTodayDailyGoal(goalAndAchievements: GoalAndAchievements, entity: DailyGoalEntity?): DailyGoal {
        if (entity == null || LocalDate.ofEpochDay(entity.date) != LocalDate.now()) {
            val dailyGoal = DailyGoal(goalAndAchievements.distanceGoal, goalAndAchievements.activityTimeGoal, goalAndAchievements.nbOfPathsGoal)
            database.addDailyGoal(dailyGoal).thenApplyAsync {
                dailyGoalCache.insertDailyGoal(dailyGoal.toDailyGoalEntity(currentUserID!!))
            }
            return dailyGoal
        }
        return DailyGoal(entity)
    }
}

/**
 * helper function to create a UserData from a UserModel
 * @param userModel the userModel
 * @return the resulting UserData
 */
private fun fromUserModelToUserData(userModel: UserModel): UserEntity {
    return UserEntity(
        userModel.getUserId(),
        userModel.getUsername(),
        userModel.getEmailAddress(),
        userModel.getFirstname(),
        userModel.getSurname(),
        UserEntity.fromLocalDateToLong(userModel.getDateOfBirth()),
        GoalAndAchievements(userModel),
        UserEntity.fromBitmapToByteArray(userModel.getProfilePhoto())
    )
}